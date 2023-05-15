package es.eduardo.bookapp;

import android.content.DialogInterface;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DetalleLibroAdminActivity extends AppCompatActivity {
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
    private ImageButton modificarLibro;
    private ImageButton eliminarLibro;
    private boolean result = true;
    public static String isbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_libro_admin);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        Intent detalle = getIntent();
        isbn = detalle.getStringExtra("isbn");
        volverAtras.getBackground().setAlpha(0);
        modificarLibro.getBackground().setAlpha(0);
        eliminarLibro.getBackground().setAlpha(0);
        new Task().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    public void EliminarLibro(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetalleLibroAdminActivity.this);
        builder.setMessage("¿Desea eliminar el libro?")
                .setPositiveButton("Si", (dialog, which) -> {
                    new EliminarLibro().doInBackground();
                    if (!result) {
                        Toast.makeText(DetalleLibroAdminActivity.this, "Se ha eliminado el libro correctamente", Toast.LENGTH_LONG).show();
                        Intent lista = new Intent(DetalleLibroAdminActivity.this, ListadoLibrosAdminActivity.class);
                        startActivity(lista);
                    } else {
                        Toast.makeText(DetalleLibroAdminActivity.this, "No se ha podido eliminar el libro", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void ModificarLibro(View view) {
        Intent modificarLibro = new Intent(this, ModificarLibroActivity.class);
        startActivity(modificarLibro);
    }

    public void VolverAtras(View v) {
        Intent lista = new Intent(this, ListadoLibrosAdminActivity.class);
        startActivity(lista);
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
        modificarLibro = (ImageButton) findViewById(R.id.btModificarLibro);
        eliminarLibro = (ImageButton) findViewById(R.id.btEliminarLibro);
    }

    private void configurarPoliticaThreads() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    class EliminarLibro extends AsyncTask<Void, Void, Void> {
        String error = "";
        int delete;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                String sql = "DELETE FROM libros WHERE ISBN='" + isbn + "'";
                if (conn != null) {
                    PreparedStatement statement = conn.prepareStatement(sql);
                    delete = statement.executeUpdate();
                    if (delete > 0) result = false;
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