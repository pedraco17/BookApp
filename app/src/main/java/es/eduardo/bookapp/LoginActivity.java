package es.eduardo.bookapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import es.eduardo.bookapp.Modelos.Usuarios;

public class LoginActivity extends AppCompatActivity {
    private static final String AWSDNS = "databasebookapp.c3pxyjlxkspm.us-east-1.rds.amazonaws.com";
    private static final String DBNAME = "BookApp";
    private static final int PUERTO = 3306;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pedraco_1998";
    private EditText etEmail;
    private EditText etPassword;
    private Button btIniciarSesion;
    private Button btRegistrarse;
    public static Usuarios u = new Usuarios();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        pedirPermisos();
        configurarPoliticaThreads();
        btIniciarSesion.getBackground().setAlpha(0);
        btRegistrarse.getBackground().setAlpha(0);
    }

    public void IniciarSesion(View v) {
        new Login().execute(etEmail.getText().toString(), etPassword.getText().toString());
    }

    public void Registrarse(View v) {
        Intent todos = new Intent(this, RegistrarseActivity.class);
        startActivity(todos);
    }

    class Login extends AsyncTask<Object, Void, Boolean> {
        @Override
        public void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            String email = (String) objects[0];
            String pass = (String) objects[1];
            String sql = "SELECT * FROM usuarios WHERE email LIKE '" + email + "'";
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    if (email.equals("admin") && pass.equals("admin")) {
                        Intent admin = new Intent(LoginActivity.this, ListadoLibrosAdminActivity.class);
                        startActivity(admin);
                        return true;
                    } else if (email.equals(rs.getString("email")) && pass.equals(rs.getString("password"))) {
                        Intent todos = new Intent(LoginActivity.this, ListadoLibrosActivity.class);
                        startActivity(todos);
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            return false;
        }

        @Override
        public void onPostExecute(Boolean aVoid){
            super.onPostExecute(aVoid);
            if (aVoid) {
                u.setEmail(etEmail.getText().toString());
                Toast.makeText(LoginActivity.this, "Se ha iniciado sesión correctamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, "Email o contraseña mal introducidos", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void ObtenerReferenciasInterfaz() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btIniciarSesion = (Button) findViewById(R.id.btIniciarSesion);
        btRegistrarse = (Button) findViewById(R.id.btRegistrarse);
    }

    public void ShowHidePass(View view){
        if (view.getId() == R.id.show_pass_btn) {
            if (etPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView)(view)).setImageResource(R.drawable.hide_eye);
                //Show Password
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView)(view)).setImageResource(R.drawable.show_eye);
                //Hide Password
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
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