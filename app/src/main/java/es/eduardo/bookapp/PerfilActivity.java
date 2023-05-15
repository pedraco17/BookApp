package es.eduardo.bookapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

import es.eduardo.bookapp.Modelos.Usuarios;

public class PerfilActivity extends AppCompatActivity {
    private static final String AWSDNS = "databasebookapp.c3pxyjlxkspm.us-east-1.rds.amazonaws.com";
    private static final String DBNAME = "BookApp";
    private static final int PUERTO = 3306;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pedraco_1998";
    private TextView etNumExpediente;
    private TextView etEmail;
    private TextView etNombre;
    private TextView etApellidos;
    private TextView tvFecha;
    private ImageButton volverAtras;
    private ImageButton modificar;
    private ImageButton cerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        modificar.getBackground().setAlpha(0);
        volverAtras.getBackground().setAlpha(0);
        cerrarSesion.getBackground().setAlpha(0);
        new Task().execute();
    }

    class Task extends AsyncTask<Void, Void, Void> {
        String error = "";

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                String sql = "SELECT * FROM usuarios WHERE email='" + LoginActivity.u.getEmail() + "'";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    etNumExpediente.setText(rs.getInt("numExpediente") + "");
                    etNombre.setText(rs.getString("nombre"));
                    etApellidos.setText(rs.getString("apellidos"));
                    etEmail.setText(rs.getString("email"));
                    tvFecha.setText(rs.getString("fechaNacimiento"));
                }
            } catch (Exception e) {
                error = e.toString();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
        }
    }

    public void ModificarPerfil(View view) {
        Intent modificarPerfil = new Intent(this, ModificarPerfilActivity.class);
        startActivity(modificarPerfil);
    }

    public void CerrarSesion(View view) {
        Intent cerrarSesion = new Intent(this, LoginActivity.class);
        startActivity(cerrarSesion);
    }

    public void VolverAtras(View v) {
        this.finish();
    }

    private void ObtenerReferenciasInterfaz() {
        etNumExpediente = (TextView) findViewById(R.id.tvNumExpediente);
        etEmail = (TextView) findViewById(R.id.tvEmail);
        etNombre = (TextView) findViewById(R.id.tvNombre);
        etApellidos = (TextView) findViewById(R.id.tvApellidos);
        tvFecha = (TextView) findViewById(R.id.tvFecha);
        volverAtras = (ImageButton) findViewById(R.id.btVolverAtras);
        modificar = (ImageButton) findViewById(R.id.btModificar);
        cerrarSesion = (ImageButton) findViewById(R.id.btCerrarSesion);
    }

    private void configurarPoliticaThreads() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
}