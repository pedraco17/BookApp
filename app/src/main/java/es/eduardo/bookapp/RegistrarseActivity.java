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

import es.eduardo.bookapp.Controladores.Controlador;
import es.eduardo.bookapp.Controladores.ControladorUsuario;

public class RegistrarseActivity extends AppCompatActivity {
    private EditText etNumExpediente;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfPassword;
    private EditText etNombre;
    private EditText etApellidos;
    private TextView tvFecha;
    private Button btAceptarRegistro;
    private Button btVolverAtras;
    private ImageButton btFecha;
    private Controlador controlador;
    private ControladorUsuario controladorBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        controlador = new Controlador();
        controladorBD = new ControladorUsuario();
        btAceptarRegistro.getBackground().setAlpha(0);
        btVolverAtras.getBackground().setAlpha(0);
        btFecha.getBackground().setAlpha(0);
    }

    public void VolverAtras(View v) {
        this.finish();
    }

    public void Aceptar(View v) {
        if (!controladorBD.Registrarse(etNumExpediente, etNombre, etApellidos, etEmail, etPassword, etConfPassword, tvFecha)) {
            Intent todos = new Intent(this, LoginActivity.class);
            startActivity(todos);
            Toast.makeText(this, "Se ha regristado correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No se ha podido registrar", Toast.LENGTH_LONG).show();
        }
    }

    public void ShowHidePass(View view){
        if (view.getId() == R.id.show_pass_btn) {
            controlador.MostrarPassword(etPassword, view);
        }
    }

    public void ShowHideConfPass(View view){
        if (view.getId() == R.id.show_confPass_btn) {
            controlador.MostrarPassword(etConfPassword, view);
        }
    }

    public void EscogerFecha(View view) {
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

    private void ObtenerReferenciasInterfaz() {
        etNumExpediente = (EditText) findViewById(R.id.etNumExpediente);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfPassword = (EditText) findViewById(R.id.etConfPassword);
        etNombre = (EditText) findViewById(R.id.etNombre);
        etApellidos = (EditText) findViewById(R.id.etApellidos);
        tvFecha = (TextView) findViewById(R.id.tvFecha);
        btAceptarRegistro = (Button) findViewById(R.id.btAceptar);
        btVolverAtras = (Button) findViewById(R.id.btVolverAtras);
        btFecha = (ImageButton) findViewById(R.id.btFecha);
    }

    private void configurarPoliticaThreads() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}