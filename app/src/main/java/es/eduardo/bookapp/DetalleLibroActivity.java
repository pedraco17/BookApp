package es.eduardo.bookapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import es.eduardo.bookapp.Controladores.ControladorLibros;
import es.eduardo.bookapp.Controladores.ControladorUsuario;

public class DetalleLibroActivity extends AppCompatActivity {
    private TextView tvISBN;
    private TextView tvTitulo;
    private TextView tvAutor;
    private TextView tvEditorial;
    private TextView tvFecha;
    private TextView tvNumPaginas;
    private TextView tvStock;
    private ImageButton volverAtras;
    private Button cogerPrestado;
    public static String isbn, titulo;
    private ControladorLibros libros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_libro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        volverAtras.getBackground().setAlpha(0);
        cogerPrestado.getBackground().setAlpha(0);
        libros = new ControladorLibros();
        libros.DetalleLibro(tvISBN, tvTitulo, tvAutor, tvEditorial, tvFecha, tvNumPaginas, tvStock);
        isbn = tvISBN.getText().toString();
        titulo = tvTitulo.getText().toString();
    }

    public void PrestarLibro(View view) {
        Intent prestarLibro = new Intent(this, PrestamoLibroActivity.class);
        startActivity(prestarLibro);
    }

    public void VolverAtras(View v) {
        this.finish();
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
        cogerPrestado = (Button) findViewById(R.id.btPrestarLibro);
    }

    private void configurarPoliticaThreads() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}