package es.eduardo.bookapp;

import android.app.DatePickerDialog;
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

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

public class ModificarLibroActivity extends AppCompatActivity {
    private static final String AWSDNS = "databasebookapp.c3pxyjlxkspm.us-east-1.rds.amazonaws.com";
    private static final String DBNAME = "BookApp";
    private static final int PUERTO = 3306;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pedraco_1998";
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
    private boolean result = true;
    public static String isbnMod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_libro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        isbnMod = DetalleLibroAdminActivity.isbn;
        btAceptarRegistro.getBackground().setAlpha(0);
        btVolverAtras.getBackground().setAlpha(0);
        btFecha.getBackground().setAlpha(0);
        etISBN.setEnabled(false);
        new CargarLibro().execute();
    }

    class Task extends AsyncTask<Void, Void, Void> {
        private int update;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                String sql = "UPDATE libros SET titulo=?, autor=?, editorial=?, fechaLanzamiento=?, numPaginas=?, stock=? " +
                        "WHERE ISBN=?";
                if (conn != null) {
                    PreparedStatement statement = conn.prepareStatement(sql);
                    if (!etTitulo.getText().toString().equals(""))
                        statement.setString(1, etTitulo.getText().toString());
                    if (!etAutor.getText().toString().equals(""))
                        statement.setString(2, etAutor.getText().toString());
                    if (!etEditorial.getText().toString().equals(""))
                        statement.setString(3, etEditorial.getText().toString());
                    if (!tvFecha.getText().toString().equals(""))
                        statement.setString(4, tvFecha.getText().toString());
                    if (!etNumPaginas.getText().toString().equals(""))
                        statement.setInt(5, Integer.parseInt(etNumPaginas.getText().toString()));
                    if (!etStock.getText().toString().equals(""))
                        statement.setInt(6, Integer.parseInt(etStock.getText().toString()));
                    statement.setString(7, etISBN.getText().toString());
                    update = statement.executeUpdate();
                    if (update > 0){
                        result = false;
                    }
                } else {
                    Toast.makeText(ModificarLibroActivity.this, "No se ha podido conectar", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                result = false;
            }
            return null;
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
        Intent lista = new Intent(this, DetalleLibroAdminActivity.class);
        startActivity(lista);
    }

    public void Aceptar(View v) {
        new Task().doInBackground();
        if (!result) {
            Intent todos = new Intent(this, ListadoLibrosAdminActivity.class);
            startActivity(todos);
            Toast.makeText(this, "Se ha modificado el libro correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No se ha podido modificar el libro", Toast.LENGTH_LONG).show();
        }
    }

    private void ObtenerReferenciasInterfaz() {
        etISBN = (EditText) findViewById(R.id.etISBN);
        etTitulo = (EditText) findViewById(R.id.etTitulo);
        etAutor = (EditText) findViewById(R.id.etAutor);
        etEditorial = (EditText) findViewById(R.id.etEditorial);
        tvFecha = (TextView) findViewById(R.id.tvFecha);
        etNumPaginas = (EditText) findViewById(R.id.etNumPaginas);
        etStock = (EditText) findViewById(R.id.etStock);
        btAceptarRegistro = (Button) findViewById(R.id.btAceptar);
        btVolverAtras = (Button) findViewById(R.id.btVolverAtras);
        btFecha = (ImageButton) findViewById(R.id.btFecha);
    }

    private void configurarPoliticaThreads() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    class CargarLibro extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                String sql = "SELECT * FROM libros WHERE ISBN='" + isbnMod + "'";
                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    if (rs.next()) {
                        etISBN.setText(rs.getString("ISBN"));
                        etTitulo.setText(rs.getString("titulo"));
                        etAutor.setText(rs.getString("autor"));
                        etEditorial.setText(rs.getString("editorial"));
                        tvFecha.setText(rs.getString("fechaLanzamiento"));
                        etNumPaginas.setText(rs.getInt("numPaginas") + "");
                        etStock.setText(rs.getInt("stock") + "");
                    }
                } else {
                    Toast.makeText(ModificarLibroActivity.this, "No se ha podido conectar", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
        }
    }
}