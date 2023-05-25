package es.eduardo.bookapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.eduardo.bookapp.Modelos.Libros;

public class ListadoLibrosActivity extends AppCompatActivity {
    private static final String AWSDNS = "databasebookapp.c3pxyjlxkspm.us-east-1.rds.amazonaws.com";
    private static final String DBNAME = "BookApp";
    private static final int PUERTO = 3306;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pedraco_1998";
    private ListView listView;
    private ArrayList<Libros> listaLibros = new ArrayList<>();
    private BookListAdapter adapter;
    private Libros libroSeleccionado;
    private int idUsuario;
    private String tituloLibro;
    private EditText etBusqueda;
    private ImageButton btBusqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_libros);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listView = (ListView) findViewById(R.id.listView);
        etBusqueda = (EditText) findViewById(R.id.etBusqueda);
        btBusqueda = (ImageButton) findViewById(R.id.btFiltro);
        btBusqueda.getBackground().setAlpha(0);
        new Task().execute();
        new CogerUsuario().execute();
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = parent.getItemAtPosition(position);
            libroSeleccionado = (Libros) selectedItem;
            Intent detalleLibro = new Intent(ListadoLibrosActivity.this, DetalleLibroActivity.class);
            detalleLibro.putExtra("isbn", libroSeleccionado.getISBN());
            startActivity(detalleLibro);
        });
    }

    public void abrirPerfil(View view){
        Intent perfil = new Intent(this, PerfilActivity.class);
        startActivity(perfil);
    }

    public void abrirListaLibrosUsuario(View view) {
        Intent listaLibros = new Intent(this, ListadoLibrosPrestadosUsuariosActivity.class);
        listaLibros.putExtra("idUsuario", idUsuario);
        startActivity(listaLibros);
    }

    public void Busqueda(View view) {
        buscarLibros(etBusqueda.getText().toString());
    }

    class Task extends AsyncTask<Void, Void, Void> {
        String error = "";

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM libros");
                while (rs.next()) {
                    String isbn = rs.getString("ISBN");
                    tituloLibro = rs.getString("titulo");
                    String autor = rs.getString("autor");
                    String editorial = rs.getString("editorial");
                    String fechaLanzamiento = rs.getString("fechaLanzamiento");
                    int numPaginas = rs.getInt("numPaginas");
                    int stock = rs.getInt("stock");
                    Libros libro = new Libros(isbn, tituloLibro, autor, editorial, fechaLanzamiento, numPaginas, stock);
                    listaLibros.add(libro);
                }
            } catch (Exception e) {
                error = e.toString();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid){
            listView.setAdapter(null);
            adapter = new BookListAdapter(ListadoLibrosActivity.this, listaLibros);
            listView.setAdapter(adapter);
            super.onPostExecute(aVoid);
        }
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
                    idUsuario = rs.getInt("numExpediente");
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

    public List<Libros> buscarLibros(String texto) {
        new Filtro().doInBackground(texto);
        return listaLibros;
    }

    class Filtro extends AsyncTask<String, String, String> {
        String error = "";

        @Override
        protected String doInBackground(String... voids) {
            String texto = voids[0];
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM libros WHERE titulo=%" + texto + "% OR autor=%"
                        + texto + "% OR editorial=%" + texto + "%");
                while (rs.next()) {
                    String isbn = rs.getString("ISBN");
                    tituloLibro = rs.getString("titulo");
                    String autor = rs.getString("autor");
                    String editorial = rs.getString("editorial");
                    String fechaLanzamiento = rs.getString("fechaLanzamiento");
                    int numPaginas = rs.getInt("numPaginas");
                    int stock = rs.getInt("stock");
                    Libros libro = new Libros(isbn, tituloLibro, autor, editorial, fechaLanzamiento, numPaginas, stock);
                    listaLibros.add(libro);
                }
            } catch (Exception e) {
                error = e.toString();
            }
            return null;
        }

        @Override
        public void onPostExecute(String aVoid) {
            adapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Desea salir de BOOKAPP?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
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
        return super.onKeyDown(keyCode, event);
    }
}