package es.eduardo.bookapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import es.eduardo.bookapp.Controladores.ControladorPrestamo;
import es.eduardo.bookapp.Controladores.ControladorUsuario;
import es.eduardo.bookapp.Modelos.Prestamo;

public class ListadoLibrosPrestadosActivity extends AppCompatActivity {
    private ListView listView;
    private List<Prestamo> listaPrestamos = new ArrayList<>();
    private ListaLibrosPrestadosAdapter adapter;
    private boolean itemSeleccionado = false;
    private ControladorPrestamo prestamo;
    public static Prestamo prestamoSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_libros_prestados);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listView = (ListView) findViewById(R.id.lvListadoLibrosPrestados);
        prestamo = new ControladorPrestamo();
        listaPrestamos = prestamo.ListadoPrestamosAdmin();
        adapter = new ListaLibrosPrestadosAdapter(this, listaPrestamos);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = parent.getItemAtPosition(position);
            prestamoSeleccionado = (Prestamo) selectedItem;
            if (!itemSeleccionado) {
                itemSeleccionado = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(ListadoLibrosPrestadosActivity.this);
                builder.setMessage("¿Ha devuelto ya el libro?")
                        .setPositiveButton("Si", (dialog, which) -> {
                            if (!prestamo.EliminarPrestamo()) {
                                recreate();
                                dialog.dismiss();
                                Toast.makeText(ListadoLibrosPrestadosActivity.this, "Se ha eliminado el prestamo correctamente", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(ListadoLibrosPrestadosActivity.this, "No se ha podido eliminar el prestamo", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
            itemSeleccionado = false;
        });
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