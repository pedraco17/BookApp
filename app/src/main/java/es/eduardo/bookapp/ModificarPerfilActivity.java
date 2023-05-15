package es.eduardo.bookapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

public class ModificarPerfilActivity extends AppCompatActivity {
    private static final String AWSDNS = "databasebookapp.c3pxyjlxkspm.us-east-1.rds.amazonaws.com";
    private static final String DBNAME = "BookApp";
    private static final int PUERTO = 3306;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pedraco_1998";
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
    private boolean result = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ObtenerReferenciasInterfaz();
        configurarPoliticaThreads();
        btAceptarRegistro.getBackground().setAlpha(0);
        btVolverAtras.getBackground().setAlpha(0);
        btFecha.getBackground().setAlpha(0);
        etNumExpediente.setEnabled(false);
        new CargarUsuario().execute();
    }

    class Task extends AsyncTask<Void, Void, Void> {
        public int update;

        @Override
        protected void onPreExecute() {
            if (!String.valueOf(etPassword.getText()).equals(String.valueOf(etConfPassword.getText()))) {
                etPassword.setText("");
                etConfPassword.setText("");
                Toast.makeText(ModificarPerfilActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                String sql = "UPDATE usuarios SET nombre=?, apellidos=?, email=?, password=?, confPassword=?, fechaNacimiento=? " +
                        "WHERE numExpediente=?";
                if (conn != null) {
                    PreparedStatement statement = conn.prepareStatement(sql);
                    if (!etNombre.getText().toString().equals(""))
                        statement.setString(1, etNombre.getText().toString());
                    if (!etApellidos.getText().toString().equals(""))
                        statement.setString(2, etApellidos.getText().toString());
                    if (!etEmail.getText().toString().equals(""))
                        statement.setString(3, etEmail.getText().toString());
                    if (!etPassword.getText().toString().equals("") && !etConfPassword.getText().toString().equals("")) {
                        if (String.valueOf(etPassword.getText()).equals(String.valueOf(etConfPassword.getText()))) {
                            statement.setString(4, etPassword.getText().toString());
                            statement.setString(5, etConfPassword.getText().toString());
                        }
                    }
                    if (!tvFecha.getText().toString().equals(""))
                        statement.setString(6, tvFecha.getText().toString());
                    statement.setInt(7, Integer.parseInt(etNumExpediente.getText().toString()));
                    update = statement.executeUpdate();
                    if (update > 0){
                        result = false;
                    }
                } else {
                    Toast.makeText(ModificarPerfilActivity.this, "No se ha podido conectar", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
        }
    }

    public void VolverAtras(View v) {
        this.finish();
    }

    public void Aceptar(View v) {
        new Task().doInBackground();
        if (!result) {
            Intent todos = new Intent(this, PerfilActivity.class);
            startActivity(todos);
            Toast.makeText(this, "Se ha modificado correctamente", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No se ha podido modificar", Toast.LENGTH_LONG).show();
        }
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

    public void ShowHideConfPass(View view){
        if (view.getId() == R.id.show_confPass_btn) {
            if (etConfPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView)(view)).setImageResource(R.drawable.hide_eye);
                //Show Password
                etConfPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView)(view)).setImageResource(R.drawable.show_eye);
                //Hide Password
                etConfPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }

    public void EscogerFecha(View v) {
        Calendar fecha = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener seleccionFechalistener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String dia = (day < 10) ? "0" + day : String.valueOf(day);
                String mes = ((month + 1) < 10) ? "0" + (month + 1) : String.valueOf(month + 1);
                String fechaSelec = dia + "/" + mes + "/"+ year;
                tvFecha.setText(fechaSelec);
            }
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

    class CargarUsuario extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
                String sql = "SELECT * FROM usuarios WHERE email='" + LoginActivity.u.getEmail() + "'";
                if (conn != null) {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);
                    if (rs.next()) {
                        etNumExpediente.setText(rs.getInt("numExpediente") + "");
                        etNombre.setText(rs.getString("nombre"));
                        etApellidos.setText(rs.getString("apellidos"));
                        etEmail.setText(rs.getString("email"));
                        etPassword.setText(rs.getString("password"));
                        etConfPassword.setText(rs.getString("confPassword"));
                        tvFecha.setText(rs.getString("fechaNacimiento"));
                    }
                } else {
                    Toast.makeText(ModificarPerfilActivity.this, "No se ha podido conectar", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
        }
    }
}