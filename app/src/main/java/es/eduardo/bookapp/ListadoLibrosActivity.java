package es.eduardo.bookapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import es.eduardo.bookapp.Controladores.ControladorLibros;
import es.eduardo.bookapp.Controladores.ControladorUsuario;
import es.eduardo.bookapp.Modelos.Libros;

public class ListadoLibrosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ListView listView;
    private BookListAdapter adapter;
    public static Libros libroSeleccionado;
    private SearchView busqueda;
    private ControladorLibros libros;
    private List<Libros> listaLibros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_libros);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listView = (ListView) findViewById(R.id.listView);
        busqueda = (SearchView) findViewById(R.id.busqueda);
        libros = new ControladorLibros();
        listaLibros = libros.ListadoLibros();
        adapter = new BookListAdapter(this, listaLibros);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = parent.getItemAtPosition(position);
            libroSeleccionado = (Libros) selectedItem;
            Intent detalleLibro = new Intent(ListadoLibrosActivity.this, DetalleLibroActivity.class);
            startActivity(detalleLibro);
        });
        busqueda.setOnQueryTextListener(this);
    }

    public void abrirPerfil(View view){
        Intent perfil = new Intent(this, PerfilActivity.class);
        startActivity(perfil);
    }

    public void abrirListaLibrosUsuario(View view) {
        Intent listaLibros = new Intent(this, ListadoLibrosPrestadosUsuariosActivity.class);
        startActivity(listaLibros);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.Filtrado(newText);
        return false;
    }
}