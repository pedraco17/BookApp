package es.eduardo.bookapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import es.eduardo.bookapp.Controladores.ControladorLibros;
import es.eduardo.bookapp.Controladores.ControladorUsuario;

public class ModificarLibroActivity extends AppCompatActivity {
    private EditText etISBN;
    private EditText etTitulo;
    private EditText etAutor;
    private EditText etEditorial;
    private EditText etNumPaginas;
    private EditText etStock;
    private TextView tvFecha;
    private Button btAceptarRegistro;
    private Button btVolverAtras;
    private ImageButton btFecha;
    private ControladorLibros libros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_libro);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        btAceptarRegistro.getBackground().setAlpha(0);
        btVolverAtras.getBackground().setAlpha(0);
        btFecha.getBackground().setAlpha(0);
        etISBN.setEnabled(false);
        libros = new ControladorLibros();
        libros.CargarLibro(etISBN, etTitulo, etAutor, etEditorial, tvFecha, etNumPaginas , etStock);
    }

    public void EscogerFecha(View v) {
        Calendar fecha = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener seleccionFechalistener = (datePicker, year, month, day) -> {
            String dia = (day < 10) ? "0" + day : String.valueOf(day);
            String mes = ((month + 1) < 10) ? "0" + (month + 1) : String.valueOf(month + 1);
            String fechaSelec = dia + "/" + mes + "/"+ year;
            tvFecha.setText(fechaSelec);
        };
        DatePickerDialog dialogoFecha = new DatePickerDialog(this, seleccionFechalistener,
                fecha.get(Calendar.YEAR), fecha.get(Calendar.MONTH), fecha.get(Calendar.DAY_OF_MONTH));
        dialogoFecha.show();
    }

    public void VolverAtras(View v) {
        Intent lista = new Intent(this, DetalleLibroAdminActivity.class);
        startActivity(lista);
    }

    public void Aceptar(View v) {
        if (!libros.ModificarLibro(etTitulo, etAutor, etEditorial, tvFecha, etNumPaginas, etStock, etISBN)) {
            Intent todos = new Intent(this, ListadoLibrosAdminActivity.class);
            startActivity(todos);
            Toast.makeText(this, "Se ha modificado el libro correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No se ha podido modificar el libro", Toast.LENGTH_LONG).show();
        }
    }

    private void ObtenerReferenciasInterfaz() {
        etISBN = (EditText) findViewById(R.id.etISBN);
        etTitulo = (EditText) findViewById(R.id.etTitulo);
        etAutor = (EditText) findViewById(R.id.etAutor);
        etEditorial = (EditText) findViewById(R.id.etEditorial);
        tvFecha = (TextView) findViewById(R.id.tvFecha);
        etNumPaginas = (EditText) findViewById(R.id.etNumPaginas);
        etStock = (EditText) findViewById(R.id.etStock);
        btAceptarRegistro = (Button) findViewById(R.id.btAceptar);
        btVolverAtras = (Button) findViewById(R.id.btVolverAtras);
        btFecha = (ImageButton) findViewById(R.id.btFecha);
    }

    private void configurarPoliticaThreads() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}