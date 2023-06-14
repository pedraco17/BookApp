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

import es.eduardo.bookapp.Controladores.ControladorUsuario;

public class ModificarPerfilActivity extends AppCompatActivity {
    private EditText etNumExpediente;
    private EditText etEmail;
    private EditText etNombre;
    private EditText etApellidos;
    private TextView tvFecha;
    private Button btAceptarRegistro;
    private Button btVolverAtras;
    private ImageButton btFecha;
    private ControladorUsuario controladorBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_perfil);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        btAceptarRegistro.getBackground().setAlpha(0);
        btVolverAtras.getBackground().setAlpha(0);
        btFecha.getBackground().setAlpha(0);
        etNumExpediente.setEnabled(false);
        controladorBD = new ControladorUsuario();
        controladorBD.CargarUsuario(etNombre, etApellidos, etEmail, tvFecha, etNumExpediente);
    }

    public void VolverAtras(View v) {
        Intent lista = new Intent(this, PerfilActivity.class);
        startActivity(lista);
    }

    public void Aceptar(View v) {
        if (!controladorBD.ModificarPerfil(etNombre, etApellidos, etEmail, tvFecha, etNumExpediente, this)) {
            Intent todos = new Intent(this, PerfilActivity.class);
            startActivity(todos);
            Toast.makeText(this, "Se ha modificado correctamente", Toast.LENGTH_LONG).show();
            LoginActivity.u.setEmail(etEmail.getText().toString());
        } else {
            Toast.makeText(this, "No se ha podido modificar", Toast.LENGTH_LONG).show();
        }
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

    private void ObtenerReferenciasInterfaz() {
        etNumExpediente = (EditText) findViewById(R.id.etNumExpediente);
        etEmail = (EditText) findViewById(R.id.etEmail);
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