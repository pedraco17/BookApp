package es.eduardo.bookapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import es.eduardo.bookapp.Controladores.Controlador;
import es.eduardo.bookapp.Controladores.ControladorUsuario;
import es.eduardo.bookapp.Modelos.Usuarios;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail;
    private EditText etPassword;
    private Button btIniciarSesion;
    private Button btRegistrarse;
    public static Usuarios u = new Usuarios();
    private ControladorUsuario controladorBD;
    private Controlador controlador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        pedirPermisos();
        configurarPoliticaThreads();
        controlador = new Controlador();
        controladorBD = new ControladorUsuario();
        btIniciarSesion.getBackground().setAlpha(0);
        btRegistrarse.getBackground().setAlpha(0);
    }

    public void IniciarSesion(View v) {
        String result = controladorBD.IniciarSesion(etEmail.getText().toString(), etPassword.getText().toString());
        if (result.equals("admin")) {
            Intent admin = new Intent(this, ListadoLibrosAdminActivity.class);
            startActivity(admin);
            Toast.makeText(this, "Se ha iniciado sesión correctamente", Toast.LENGTH_LONG).show();
        } else if (result.equals("estudiante")) {
            Intent usuario = new Intent(this, ListadoLibrosActivity.class);
            startActivity(usuario);
            Toast.makeText(this, "Se ha iniciado sesión correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Email o contraseña mal introducidos", Toast.LENGTH_LONG).show();
        }
        u.setEmail(etEmail.getText().toString());
    }

    public void Registrarse(View v) {
        Intent todos = new Intent(this, RegistrarseActivity.class);
        startActivity(todos);
    }

    public void ShowHidePass(View view) {
        if (view.getId() == R.id.show_pass_btn) {
            controlador.MostrarPassword(etPassword, view);
        }
    }

    private void ObtenerReferenciasInterfaz() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btIniciarSesion = (Button) findViewById(R.id.btIniciarSesion);
        btRegistrarse = (Button) findViewById(R.id.btRegistrarse);
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

    private void pedirPermisos() {
        if (ContextCompat. checkSelfPermission(this, android.Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET,
                    android.Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE}, 0);
        }
    }

    private void configurarPoliticaThreads() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}