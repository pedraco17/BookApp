package es.eduardo.bookapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import es.eduardo.bookapp.Controladores.Controlador;
import es.eduardo.bookapp.Controladores.ControladorPrestamo;
import es.eduardo.bookapp.Controladores.ControladorUsuario;

public class PrestamoLibroActivity extends AppCompatActivity {
    private TextView tvISBN;
    private TextView tvFechaPrestamo;
    private TextView tvFechaDevolucion;
    private TextView tvTituloLibro;
    private Button btAceptar;
    private Button btVolverAtras;
    private ControladorPrestamo prestamo;
    private Controlador controlador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestamo_libro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        controlador = new Controlador();
        prestamo = new ControladorPrestamo();
        btAceptar.getBackground().setAlpha(0);
        btVolverAtras.getBackground().setAlpha(0);
        tvISBN.setText(DetalleLibroActivity.isbn);
        tvFechaPrestamo.setText(controlador.FechaPrestamo());
        tvFechaDevolucion.setText(controlador.FechaDevolucion());
        tvTituloLibro.setText(DetalleLibroActivity.titulo);
    }

    public void Aceptar(View view) {
        if (!prestamo.PrestamoLibro(this)) {
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