package es.eduardo.bookapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import es.eduardo.bookapp.Modelos.Libros;

public class DetalleLibroActivity extends AppCompatActivity {
    private static final String AWSDNS = "databasebookapp.c3pxyjlxkspm.us-east-1.rds.amazonaws.com";
    private static final String DBNAME = "BookApp";
    private static final int PUERTO = 3306;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pedraco_1998";
    private TextView tvISBN;
    private TextView tvTitulo;
    private TextView tvAutor;
    private TextView tvEditorial;
    private TextView tvFecha;
    private TextView tvNumPaginas;
    private TextView tvStock;
    private ImageButton volverAtras;
    private Button cogerPrestado;
    private String isbn;
    private int numExpediente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_libro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        volverAtras.getBackground().setAlpha(0);
        cogerPrestado.getBackground().setAlpha(0);
        Intent detalleLibro = getIntent();
        isbn = detalleLibro.getStringExtra("isbn");
        new Task().execute();
        new CogerUsuario().execute();
    }

    class Task extends AsyncTask<Void, Void, Void> {
        String error = "";

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                String sql = "SELECT * FROM libros WHERE ISBN='" + isbn + "'";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    tvISBN.setText(rs.getString("ISBN"));
                    tvTitulo.setText(rs.getString("titulo"));
                    tvAutor.setText(rs.getString("autor"));
                    tvEditorial.setText(rs.getString("editorial"));
                    tvFecha.setText(rs.getString("fechaLanzamiento"));
                    tvNumPaginas.setText(rs.getString("numPaginas"));
                    tvStock.setText(rs.getString("stock"));
                }
            } catch (Exception e) {
                error = e.toString();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
        }
    }

    public void PrestarLibro(View view) {
        Intent prestarLibro = new Intent(this, PrestamoLibroActivity.class);
        prestarLibro.putExtra("idUsuario", numExpediente);
        prestarLibro.putExtra("idLibro", isbn);
        prestarLibro.putExtra("tituloLibro", tvTitulo.getText().toString());
        startActivity(prestarLibro);
    }

    public void VolverAtras(View v) {
        this.finish();
    }

    private void ObtenerReferenciasInterfaz() {
        tvISBN = (TextView) findViewById(R.id.tvISBN);
        tvTitulo = (TextView) findViewById(R.id.tvTitulo);
        tvAutor = (TextView) findViewById(R.id.tvAutor);
        tvEditorial = (TextView) findViewById(R.id.tvEditorial);
        tvFecha = (TextView) findViewById(R.id.tvFecha);
        tvNumPaginas = (TextView) findViewById(R.id.tvNumPaginas);
        tvStock = (TextView) findViewById(R.id.tvStock);
        volverAtras = (ImageButton) findViewById(R.id.btVolverAtras);
        cogerPrestado = (Button) findViewById(R.id.btPrestarLibro);
    }

    private void configurarPoliticaThreads() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    class CogerUsuario extends AsyncTask<Void, Void, Void> {
        String error = "";

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                String sql = "SELECT * FROM usuarios WHERE email='" + LoginActivity.u.getEmail() + "'";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    numExpediente = rs.getInt("numExpediente");
                }
            } catch (Exception e) {
                error = e.toString();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
        }
    }
}