package es.eduardo.bookapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import es.eduardo.bookapp.Modelos.Prestamo;

public class ListadoLibrosPrestadosActivity extends AppCompatActivity {
    private static final String AWSDNS = "databasebookapp.c3pxyjlxkspm.us-east-1.rds.amazonaws.com";
    private static final String DBNAME = "BookApp";
    private static final int PUERTO = 3306;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pedraco_1998";
    private ListView listView;
    private List<Prestamo> listaPrestamos = new ArrayList<>();
    private ListaLibrosPrestadosAdapter adapter;
    private boolean itemSeleccionado = false;
    private Prestamo prestamoSeleccionado;
    private boolean result = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_libros_prestados);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listView = (ListView) findViewById(R.id.lvListadoLibrosPrestados);
        new ListarPrestamos().execute();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = parent.getItemAtPosition(position);
            prestamoSeleccionado = (Prestamo) selectedItem;
            if (!itemSeleccionado) {
                itemSeleccionado = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(ListadoLibrosPrestadosActivity.this);
                builder.setMessage("¿Ha devuelto ya el libro?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new BorrarPrestamo().doInBackground();
                                if (!result) {
                                    recreate();
                                    dialog.dismiss();
                                    Toast.makeText(ListadoLibrosPrestadosActivity.this, "Se ha eliminado el prestamo correctamente", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ListadoLibrosPrestadosActivity.this, "No se ha podido eliminar el prestamo", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
            itemSeleccionado = false;
        });
    }

    class BorrarPrestamo extends AsyncTask<Void, Void, Void> {
        String error = "";
        int delete;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                String sql = "DELETE FROM prestamo WHERE idUsuario=" + prestamoSeleccionado.getIdUsuario() +
                        " AND idLibro='" + prestamoSeleccionado.getIdLibro() + "'";
                if (conn != null) {
                    PreparedStatement statement = conn.prepareStatement(sql);
                    delete = statement.executeUpdate();
                    if (delete > 0) {
                        PreparedStatement ps = conn.prepareStatement("UPDATE libros SET stock=(stock+1) WHERE ISBN=" + prestamoSeleccionado.getIdLibro());
                        int update = ps.executeUpdate();
                        if (update > 0) result = false;
                    }
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

    class ListarPrestamos extends AsyncTask<Void, Void, Void> {
        String error = "";

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM prestamo");
                while (rs.next()) {
                    int idUsuario = rs.getInt("idUsuario");
                    String idLibro = rs.getString("idLibro");
                    String tituloLibro = rs.getString("tituloLibro");
                    String fechaPrestamo = rs.getString("fechaPrestamo");
                    String fechaDevolucion = rs.getString("fechaDevolucion");
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
            adapter = new ListaLibrosPrestadosAdapter(ListadoLibrosPrestadosActivity.this, listaPrestamos);
            listView.setAdapter(adapter);
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent lista = new Intent(this, ListadoLibrosAdminActivity.class);
            startActivity(lista);
        }
        return super.onKeyDown(keyCode, event);
    }
}