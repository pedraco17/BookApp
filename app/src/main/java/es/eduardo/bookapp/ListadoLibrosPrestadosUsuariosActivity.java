package es.eduardo.bookapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import es.eduardo.bookapp.Controladores.Controlador;
import es.eduardo.bookapp.Controladores.ControladorPrestamo;
import es.eduardo.bookapp.Controladores.ControladorUsuario;
import es.eduardo.bookapp.Modelos.Prestamo;

public class ListadoLibrosPrestadosUsuariosActivity extends AppCompatActivity {
    private ListView listView;
    private List<Prestamo> listaPrestamos = new ArrayList<>();
    private ListaLibrosPrestadosUsuariosAdapter adapter;
    private boolean itemSeleccionado = false;
    private Prestamo prestamoSeleccionado;
    private Controlador controlador;
    private ControladorPrestamo prestamo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_libros_prestados_usuarios);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        listView = (ListView) findViewById(R.id.lvListadoLibrosPrestadosUsuarios);
        controlador = new Controlador();
        prestamo = new ControladorPrestamo();
        listaPrestamos = prestamo.ListadoPrestamos();
        adapter = new ListaLibrosPrestadosUsuariosAdapter(this, listaPrestamos);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Object selectedItem = parent.getItemAtPosition(position);
            prestamoSeleccionado = (Prestamo) selectedItem;
            if (!itemSeleccionado) {
                itemSeleccionado = true;
                AlertDialog.Builder builder = new AlertDialog.Builder(ListadoLibrosPrestadosUsuariosActivity.this);
                builder.setMessage("Te quedan " + controlador.DiasRestantes(prestamoSeleccionado.getFechaDevolucion()) + " días para devolver el libro")
                        .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
                builder.show();
            }
            itemSeleccionado = false;
        });
    }
}