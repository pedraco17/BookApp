package es.eduardo.bookapp;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddLibroActivity extends AppCompatActivity {
    private static final String AWSDNS = "databasebookapp.c3pxyjlxkspm.us-east-1.rds.amazonaws.com";
    private static final String DBNAME = "BookApp";
    private static final int PUERTO = 3306;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pedraco_1998";
    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes";
    private EditText etISBN;
    private EditText etTitulo;
    private EditText etAutor;
    private EditText etEditorial;
    private EditText etNumPaginas;
    private EditText etStock;
    private TextView tvFecha;
    private Button btAceptarRegistro;
    private Button btVolverAtras;
    private ImageButton btFecha;
    private ImageButton btISBN;
    private boolean result = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_libro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        btAceptarRegistro.getBackground().setAlpha(0);
        btVolverAtras.getBackground().setAlpha(0);
        btFecha.getBackground().setAlpha(0);
        btISBN.getBackground().setAlpha(0);
    }

    class Task extends AsyncTask<Void, Void, Void> {
        private int insert;

        @Override
        protected void onPreExecute() {
            if (comprobarISBN(etISBN.getText().toString())) {
                Toast.makeText(AddLibroActivity.this, "El ISBN introducido ya existe", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                String sql = "INSERT INTO libros (ISBN, titulo, autor, editorial, fechaLanzamiento, numPaginas, stock) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
                if (conn != null) {
                    PreparedStatement statement = conn.prepareStatement(sql);
                    if (!etISBN.getText().toString().equals("")) {
                        if (!comprobarISBN(etISBN.getText().toString())) {
                            statement.setString(1, etISBN.getText().toString());
                        } else {
                            etISBN.setText("");
                        }
                    }
                    if (!etTitulo.getText().toString().equals(""))
                        statement.setString(2, etTitulo.getText().toString());
                    if (!etAutor.getText().toString().equals(""))
                        statement.setString(3, etAutor.getText().toString());
                    if (!etEditorial.getText().toString().equals(""))
                        statement.setString(4, etEditorial.getText().toString());
                    if (!tvFecha.getText().toString().equals("")) {
                        statement.setString(5, tvFecha.getText().toString());
                    }
                    if (!etNumPaginas.getText().toString().equals("")) {
                        statement.setInt(6, Integer.parseInt(etNumPaginas.getText().toString()));
                    }
                    if (!etStock.getText().toString().equals("")) {
                        statement.setInt(7, Integer.parseInt(etStock.getText().toString()));
                    }
                    insert = statement.executeUpdate();
                    if (insert > 0) {
                        result = false;
                    }
                } else {
                    Toast.makeText(AddLibroActivity.this, "No se ha podido conectar", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                result = false;
            }
            return null;
        }

        public boolean comprobarISBN(String isbn) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM libros");
                while (rs.next()) {
                    if (isbn.equals(rs.getString("ISBN"))) return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        public void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
        }
    }

    public void EscogerFecha(View v) {
        Calendar fecha = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener seleccionFechalistener = (datePicker, year, month, day) -> {
            String dia = (day < 10) ? "0" + day : String.valueOf(day);
            String mes = ((month + 1) < 10) ? "0" + (month + 1) : String.valueOf(month + 1);
            String fechaSelec = dia + "/" + mes + "/"+ year;
            tvFecha.setText(fechaSelec);
        };
        DatePickerDialog dialogoFecha = new DatePickerDialog(this, seleccionFechalistener,
                fecha.get(Calendar.YEAR), fecha.get(Calendar.MONTH), fecha.get(Calendar.DAY_OF_MONTH));
        dialogoFecha.show();
    }

    public void VolverAtras(View v) {
        this.finish();
    }

    public void Aceptar(View v) {
        new Task().doInBackground();
        if (!result) {
            Intent todos = new Intent(this, ListadoLibrosAdminActivity.class);
            startActivity(todos);
            Toast.makeText(this, "Se ha añadido el libro correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No se ha podido añadir el libro", Toast.LENGTH_LONG).show();
        }
    }

    public void BuscarISBN(View view) {
        try {
            URL url = new URL(GOOGLE_BOOKS_API_URL + "?q=isbn:" + etISBN.getText());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                String bookInfo = response.toString();
                parseBookInfo(bookInfo);
            }
            connection.disconnect();
        } catch (IOException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No se encuentra el libro")
                    .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
            builder.show();
            e.printStackTrace();
        }
    }

    private void parseBookInfo(String bookInfo) {
        try {
            JSONObject jsonObject = new JSONObject(bookInfo);
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            if (itemsArray.length() > 0) {
                JSONObject bookObject = itemsArray.getJSONObject(0);
                JSONObject volumeInfo = bookObject.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");
                JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                StringBuilder authorsBuilder = new StringBuilder();
                for (int i = 0; i < authorsArray.length(); i++) {
                    authorsBuilder.append(authorsArray.getString(i));
                    if (i < authorsArray.length() - 1) {
                        authorsBuilder.append(", ");
                    }
                }
                String authors = authorsBuilder.toString();
                String publisher = volumeInfo.optString("publisher", "");
                int pageCount = volumeInfo.optInt("pageCount", 0);
                String publishedDate = volumeInfo.optString("publishedDate", "");
                SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
                String fechaFormateada = "";
                if (!publishedDate.equals("")) {
                    try {
                        Date fecha = formatoFecha.parse(publishedDate);
                        SimpleDateFormat formatoFechaFormateada = new SimpleDateFormat("dd/MM/yyyy");
                        fechaFormateada = formatoFechaFormateada.format(fecha);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                etTitulo.setText(title);
                etAutor.setText(authors);
                etEditorial.setText(publisher);
                etNumPaginas.setText(pageCount + "");
                tvFecha.setText(fechaFormateada);
            } else {
                System.out.println("No book found for the given ISBN.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ObtenerReferenciasInterfaz() {
        etISBN = (EditText) findViewById(R.id.etISBN);
        etTitulo = (EditText) findViewById(R.id.etTitulo);
        etAutor = (EditText) findViewById(R.id.etAutor);
        etEditorial = (EditText) findViewById(R.id.etEditorial);
        etNumPaginas = (EditText) findViewById(R.id.etNumPaginas);
        etStock = (EditText) findViewById(R.id.etStock);
        tvFecha = (TextView) findViewById(R.id.tvFechaLanzamiento);
        btAceptarRegistro = (Button) findViewById(R.id.btAceptar);
        btVolverAtras = (Button) findViewById(R.id.btVolverAtras);
        btFecha = (ImageButton) findViewById(R.id.btFecha);
        btISBN = (ImageButton) findViewById(R.id.btISBN);
    }

    private void configurarPoliticaThreads() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}