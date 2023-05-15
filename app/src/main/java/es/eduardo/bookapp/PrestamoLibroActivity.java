package es.eduardo.bookapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class PrestamoLibroActivity extends AppCompatActivity {
    private static final String AWSDNS = "databasebookapp.c3pxyjlxkspm.us-east-1.rds.amazonaws.com";
    private static final String DBNAME = "BookApp";
    private static final int PUERTO = 3306;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pedraco_1998";
    private String idLibro;
    private int idUsuario;
    private String tituloLibro;
    private TextView tvISBN;
    private TextView tvFechaPrestamo;
    private TextView tvFechaDevolucion;
    private TextView tvTituloLibro;
    private Button btAceptar;
    private Button btVolverAtras;
    private boolean result = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestamo_libro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        Intent prestarLibro = getIntent();
        idUsuario = prestarLibro.getIntExtra("idUsuario", -1);
        idLibro = prestarLibro.getStringExtra("idLibro");
        tituloLibro = prestarLibro.getStringExtra("tituloLibro");

        btAceptar.getBackground().setAlpha(0);
        btVolverAtras.getBackground().setAlpha(0);
        tvISBN.setText(idLibro);
        tvFechaPrestamo.setText(FechaPrestamo());
        tvFechaDevolucion.setText(FechaDevolucion());
        tvTituloLibro.setText(tituloLibro);
    }

    class Task extends AsyncTask<Void, Void, Void> {
        private int insert;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                String sql = "INSERT INTO prestamo (idUsuario, idLibro, tituloLibro, fechaPrestamo, fechaDevolucion) VALUES (?, ?, ?, ?, ?)";
                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM libros WHERE stock > 0 AND ISBN=" + idLibro);
                    if (rs.next()) {
                        ResultSet rs2 = stmt.executeQuery("SELECT * FROM prestamo WHERE idUsuario=" + idUsuario
                            + " AND idLibro='" + idLibro + "'");
                        if (rs2.next()) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PrestamoLibroActivity.this);
                            builder.setMessage("Ya tienes prestado este libro")
                                    .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
                            builder.show();
                            result = true;
                        } else {
                            PreparedStatement statement = conn.prepareStatement(sql);
                            statement.setInt(1, idUsuario);
                            statement.setString(2, idLibro);
                            statement.setString(3, tituloLibro);
                            statement.setString(4, FechaPrestamo());
                            statement.setString(5, FechaDevolucion());
                            insert = statement.executeUpdate();
                            if (insert > 0) {
                                PreparedStatement ps = conn.prepareStatement("UPDATE libros SET stock=(stock-1) WHERE ISBN=" + idLibro);
                                int update = ps.executeUpdate();
                                if (update > 0) result = false;
                            }
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PrestamoLibroActivity.this);
                        builder.setMessage("No hay stock del libro")
                                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
                        builder.show();
                        result = true;
                    }
                } else {
                    Toast.makeText(PrestamoLibroActivity.this, "No se ha podido conectar", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                result = true;
            }
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
        }
    }

    private String FechaDevolucion() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, 3);
        Date fechaEnTresSemanas = calendar.getTime();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String fechaDevolucion = formato.format(fechaEnTresSemanas);
        return fechaDevolucion;
    }

    private String FechaPrestamo() {
        Date hoy = Calendar.getInstance().getTime();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String fechaHoy = formato.format(hoy);
        return fechaHoy;
    }

    public void Aceptar(View view) {
        new Task().doInBackground();
        if (!result) {
            Intent todos = new Intent(this, ListadoLibrosActivity.class);
            startActivity(todos);
            Toast.makeText(this, "Se ha prestado el libro correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No se ha podido prestar el libro", Toast.LENGTH_LONG).show();
        }
    }

    public void VolverAtras(View v) {
        this.finish();
    }

    private void ObtenerReferenciasInterfaz() {
        tvISBN = (TextView) findViewById(R.id.tvISBN);
        tvTituloLibro = (TextView) findViewById(R.id.tvTituloLibro);
        tvFechaPrestamo = (TextView) findViewById(R.id.tvFechaPrestamo);
        tvFechaDevolucion = (TextView) findViewById(R.id.tvFechaDevolucion);
        btAceptar = (Button) findViewById(R.id.btAceptar);
        btVolverAtras = (Button) findViewById(R.id.btVolverAtras);
    }

    private void configurarPoliticaThreads() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}