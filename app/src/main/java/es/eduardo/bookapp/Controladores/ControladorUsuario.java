package es.eduardo.bookapp.Controladores;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import es.eduardo.bookapp.ListadoLibrosActivity;
import es.eduardo.bookapp.ListadoLibrosAdminActivity;
import es.eduardo.bookapp.ListadoLibrosPrestadosActivity;
import es.eduardo.bookapp.LoginActivity;
import es.eduardo.bookapp.Modelos.Libros;
import es.eduardo.bookapp.Modelos.Prestamo;

public class ControladorUsuario extends AsyncTask<Void, Void, Void> {
    private static final String AWSDNS = "databasebookapp.c3pxyjlxkspm.us-east-1.rds.amazonaws.com";
    private static final String DBNAME = "BookApp";
    private static final int PUERTO = 3306;
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Pedraco_1998";
    private Controlador controlador = new Controlador();
    private static Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private PreparedStatement statement;

    public ControladorUsuario() {}

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    public void AbrirConexión() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://" + AWSDNS + ":" + PUERTO + "/" + DBNAME, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    public void desconectar() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Conexión desconectada.");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean ModificarPerfil(EditText etNombre, EditText etApellidos, EditText etEmail, TextView tvFecha, EditText etNumExpediente, Context context) {
        boolean result = true;
        int update;
        AbrirConexión();
        try {
            String sql = "UPDATE usuarios SET nombre=?, apellidos=?, email=?, fechaNacimiento=? WHERE numExpediente=?";
            if (conn != null) {
                statement = conn.prepareStatement(sql);
                if (!etNombre.getText().toString().equals(""))
                    statement.setString(1, etNombre.getText().toString());
                if (!etApellidos.getText().toString().equals(""))
                    statement.setString(2, etApellidos.getText().toString());
                if (!etEmail.getText().toString().equals(""))
                    if (controlador.validarEmail(etEmail.getText().toString())) {
                        statement.setString(3, etEmail.getText().toString());
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Formato de email inadecuado")
                                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
                        builder.show();
                    }
                if (!tvFecha.getText().toString().equals(""))
                    statement.setString(4, tvFecha.getText().toString());
                statement.setInt(5, Integer.parseInt(etNumExpediente.getText().toString()));
                update = statement.executeUpdate();
                if (update > 0) result = false;
            }
        } catch (Exception e) {
            result = true;
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public void CargarPerfil(TextView etNumExpediente, TextView etNombre, TextView etApellidos, TextView etEmail, TextView tvFecha) {
        AbrirConexión();
        try {
            String sql = "SELECT * FROM usuarios WHERE email='" + LoginActivity.u.getEmail() + "'";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                etNumExpediente.setText(rs.getInt("numExpediente") + "");
                etNombre.setText(rs.getString("nombre"));
                etApellidos.setText(rs.getString("apellidos"));
                etEmail.setText(rs.getString("email"));
                tvFecha.setText(rs.getString("fechaNacimiento"));
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            desconectar();
        }
    }

    public void CargarUsuario(EditText etNombre, EditText etApellidos, EditText etEmail, TextView tvFecha, EditText etNumExpediente) {
        AbrirConexión();
        try {
            String sql = "SELECT * FROM usuarios WHERE email='" + LoginActivity.u.getEmail() + "'";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                etNumExpediente.setText(rs.getInt("numExpediente") + "");
                etNombre.setText(rs.getString("nombre"));
                etApellidos.setText(rs.getString("apellidos"));
                etEmail.setText(rs.getString("email"));
                tvFecha.setText(rs.getString("fechaNacimiento"));
            }
        }  catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            desconectar();
        }
    }

    public String IniciarSesion(String email, String password) {
        AbrirConexión();
        String usuario = "";
        String pass = "";
        try {
            pass = controlador.getSHA256Hash(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        String sql = "SELECT * FROM usuarios WHERE email LIKE '" + email + "'";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                if (rs.getString("email").equals("admin") && pass.equals(rs.getString("password"))) {
                    usuario = "admin";
                } else if (email.equals(rs.getString("email")) && pass.equals(rs.getString("password"))) {
                    usuario = "estudiante";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            desconectar();
        }
        return usuario;
    }

    public boolean Registrarse(EditText etNumExpediente, EditText etNombre, EditText etApellidos, EditText etEmail, EditText etPassword, EditText etConfPassword, TextView tvFecha) {
        AbrirConexión();
        int insert;
        String sql = "INSERT INTO usuarios (numExpediente, nombre, apellidos, email, password, fechaNacimiento) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            if (conn != null) {
                statement = conn.prepareStatement(sql);
                if (!etNumExpediente.getText().toString().equals("")) {
                    if (!comprobarNumExp(Integer.parseInt(etNumExpediente.getText().toString()))) {
                        statement.setInt(1, Integer.parseInt(etNumExpediente.getText().toString()));
                    } else {
                        etNumExpediente.setText("");
                    }
                }
                if (!etNombre.getText().toString().equals(""))
                    statement.setString(2, etNombre.getText().toString());
                if (!etApellidos.getText().toString().equals(""))
                    statement.setString(3, etApellidos.getText().toString());
                if (!etEmail.getText().toString().equals(""))
                    if (controlador.validarEmail(etEmail.getText().toString())) {
                        statement.setString(4, etEmail.getText().toString());
                    }
                if (!etPassword.getText().toString().equals("") && !etConfPassword.getText().toString().equals("")) {
                    if (!String.valueOf(etPassword.getText()).equals(String.valueOf(etConfPassword.getText()))) {
                        etPassword.setText("");
                        etConfPassword.setText("");
                    } else {
                        statement.setString(5, controlador.getSHA256Hash(etPassword.getText().toString()));
                    }
                }
                if (!tvFecha.getText().toString().equals(""))
                    statement.setString(6, tvFecha.getText().toString());
                insert = statement.executeUpdate();
                if (insert > 0){
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            desconectar();
        }
        return true;
    }

    private boolean comprobarNumExp(int numExp) {
        AbrirConexión();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM usuarios");
            while (rs.next()) {
                if (numExp == rs.getInt("numExpediente")) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            desconectar();
        }
        return false;
    }
}