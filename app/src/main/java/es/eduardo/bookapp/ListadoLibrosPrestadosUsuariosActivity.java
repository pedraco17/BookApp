package es.eduardo.bookapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import es.eduardo.bookapp.Modelos.Libros;
import es.eduardo.bookapp.Modelos.Prestamo;

public class ListadoLibrosPrestadosUsuariosActivity extends AppCompatActivity {
    private static final String AWSDNS = "databasebookapp.c3pxyjlxkspm.us-east-1.rds.amazonaws.com";
    private static final String DBNAME = "BookApp";
    private static final int PUERTO = 3306;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pedraco_1998";
    private ListView listView;
    private List<Prestamo> listaPrestamos = new ArrayList<>();
    private ListaLibrosPrestadosUsuariosAdapter adapter;
    private int idUsuario;
    private String fechaDevolucion;
    private boolean itemSeleccionado = false;
    private Prestamo prestamoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_libros_prestados_usuarios);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listView = (ListView) findViewById(R.id.lvListadoLibrosPrestadosUsuarios);
        Intent usuario = getIntent();
        idUsuario = usuario.getIntExtra("idUsuario", -1);
        new Task().execute();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = parent.getItemAtPosition(position);
            prestamoSeleccionado = (Prestamo) selectedItem;
            if (!itemSeleccionado) {
                itemSeleccionado = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(ListadoLibrosPrestadosUsuariosActivity.this);
                builder.setMessage("Te quedan " + DiasRestantes(prestamoSeleccionado.getFechaDevolucion()) + " días para devolver el libro")
                        .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
            itemSeleccionado = false;
        });
    }

    private long DiasRestantes(String fecha) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date fechaDevolucion = format.parse(fecha);
            Date fechaActual = new Date();
            long diffEnMilisegundos = fechaDevolucion.getTime() - fechaActual.getTime();
            long dias = TimeUnit.DAYS.convert(diffEnMilisegundos, TimeUnit.MILLISECONDS);
            return dias;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    class Task extends AsyncTask<Void, Void, Void> {
        String error = "";

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM prestamo WHERE idUsuario=" + idUsuario);
                while (rs.next()) {
                    String idLibro = rs.getString("idLibro");
                    String tituloLibro = rs.getString("tituloLibro");
                    String fechaPrestamo = rs.getString("fechaPrestamo");
                    fechaDevolucion = rs.getString("fechaDevolucion");
                    Prestamo prestamo = new Prestamo(idUsuario, idLibro, tituloLibro, fechaPrestamo, fechaDevolucion);
                    listaPrestamos.add(prestamo);
                }
            } catch (Exception e) {
                error = e.toString();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid){
            adapter = new ListaLibrosPrestadosUsuariosAdapter(ListadoLibrosPrestadosUsuariosActivity.this, listaPrestamos);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
            super.onPostExecute(aVoid);
        }
    }
}