package es.eduardo.bookapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.eduardo.bookapp.Controladores.ControladorLibros;
import es.eduardo.bookapp.Controladores.ControladorUsuario;

public class AddLibroActivity extends AppCompatActivity {
    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
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
    private ControladorLibros libros;

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
        libros = new ControladorLibros();
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
        if (!libros.AñadirLibro(etISBN, etTitulo, etAutor, etEditorial, tvFecha, etNumPaginas, etStock)) {
            Intent todos = new Intent(this, ListadoLibrosAdminActivity.class);
            startActivity(todos);
            Toast.makeText(this, "Se ha añadido el libro correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No se ha podido añadir el libro", Toast.LENGTH_LONG).show();
        }
    }

    public void BuscarISBN(View view) {
        try {
            URL url = new URL(GOOGLE_BOOKS_API_URL +  etISBN.getText());
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
                    if (i < authorsArray.length() - 1) authorsBuilder.append(", ");
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