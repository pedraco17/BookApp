package es.eduardo.bookapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import es.eduardo.bookapp.Controladores.ControladorUsuario;

public class PerfilActivity extends AppCompatActivity {
    private TextView etNumExpediente;
    private TextView etEmail;
    private TextView etNombre;
    private TextView etApellidos;
    private TextView tvFecha;
    private ImageButton modificar;
    private ImageButton cerrarSesion;
    private ControladorUsuario controladorBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        modificar.getBackground().setAlpha(0);
        cerrarSesion.getBackground().setAlpha(0);
        controladorBD = new ControladorUsuario();
        controladorBD.CargarPerfil(etNumExpediente, etNombre, etApellidos, etEmail, tvFecha);
    }

    public void ModificarPerfil(View view) {
        Intent modificarPerfil = new Intent(this, ModificarPerfilActivity.class);
        startActivity(modificarPerfil);
    }

    public void CerrarSesion(View view) {
        Intent cerrarSesion = new Intent(this, LoginActivity.class);
        startActivity(cerrarSesion);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent lista = new Intent(this, ListadoLibrosActivity.class);
            startActivity(lista);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void ObtenerReferenciasInterfaz() {
        etNumExpediente = (TextView) findViewById(R.id.tvNumExpediente);
        etEmail = (TextView) findViewById(R.id.tvEmail);
        etNombre = (TextView) findViewById(R.id.tvNombre);
        etApellidos = (TextView) findViewById(R.id.tvApellidos);
        tvFecha = (TextView) findViewById(R.id.tvFecha);
        modificar = (ImageButton) findViewById(R.id.btModificar);
        cerrarSesion = (ImageButton) findViewById(R.id.btCerrarSesion);
    }

    private void configurarPoliticaThreads() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}