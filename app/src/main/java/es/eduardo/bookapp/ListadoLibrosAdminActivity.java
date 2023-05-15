package es.eduardo.bookapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import es.eduardo.bookapp.Modelos.Libros;

public class ListadoLibrosAdminActivity extends AppCompatActivity {
    private static final String AWSDNS = "databasebookapp.c3pxyjlxkspm.us-east-1.rds.amazonaws.com";
    private static final String DBNAME = "BookApp";
    private static final int PUERTO = 3306;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pedraco_1998";
    private ListView listView;
    private List<Libros> listaLibros = new ArrayList<>();
    private BookListAdapter adapter;
    private Libros libroSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_libros_admin);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listView = (ListView) findViewById(R.id.listView);
        new Task().execute();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Aquí obtienes el elemento seleccionado de la lista
                Object selectedItem = parent.getItemAtPosition(position);

                // Aquí puedes hacer lo que necesites con el elemento seleccionado
                // Por ejemplo, si tu lista contiene objetos del tipo Libros:
                libroSeleccionado = (Libros) selectedItem;
                // Luego puedes obtener sus propiedades y hacer lo que necesites con ellas
                Intent detalleLibro = new Intent(ListadoLibrosAdminActivity.this, DetalleLibroAdminActivity.class);
                detalleLibro.putExtra("isbn", libroSeleccionado.getISBN());
                startActivity(detalleLibro);
            }
        });
    }

    public void abrirPerfil(View view){
        Intent perfil = new Intent(this, PerfilActivity.class);
        startActivity(perfil);
    }

    public void addLibro(View view) {
        Intent addLibro = new Intent(this, AddLibroActivity.class);
        startActivity(addLibro);
    }

    public void abrirListaPrestados(View view) {
        Intent listaPrestados = new Intent(this, ListadoLibrosPrestadosActivity.class);
        startActivity(listaPrestados);
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
                    String titulo = rs.getString("titulo");
                    String autor = rs.getString("autor");
                    String editorial = rs.getString("editorial");
                    String fechaLanzamiento = rs.getString("fechaLanzamiento");
                    int numPaginas = rs.getInt("numPaginas");
                    int stock = rs.getInt("stock");
                    Libros libro = new Libros(isbn, titulo, autor, editorial, fechaLanzamiento, numPaginas, stock);
                    listaLibros.add(libro);
                }
            } catch (Exception e) {
                error = e.toString();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid){
            adapter = new BookListAdapter(ListadoLibrosAdminActivity.this, listaLibros);
            listView.setAdapter(adapter);
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