package es.eduardo.bookapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import es.eduardo.bookapp.Controladores.ControladorLibros;
import es.eduardo.bookapp.Controladores.ControladorUsuario;

public class DetalleLibroAdminActivity extends AppCompatActivity {
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
    private ControladorLibros libros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_libro_admin);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        volverAtras.getBackground().setAlpha(0);
        modificarLibro.getBackground().setAlpha(0);
        eliminarLibro.getBackground().setAlpha(0);
        libros = new ControladorLibros();
        libros.DetalleLibroAdmin(tvISBN, tvTitulo, tvAutor, tvEditorial, tvFecha, tvNumPaginas, tvStock);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void EliminarLibro(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DetalleLibroAdminActivity.this);
        builder.setMessage("¿Desea eliminar el libro?")
                .setPositiveButton("Si", (dialog, which) -> {
                    if (!libros.EliminarLibro(tvISBN.getText().toString())) {
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
}