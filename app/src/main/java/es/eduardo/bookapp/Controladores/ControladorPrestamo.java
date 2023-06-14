package es.eduardo.bookapp.Controladores;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import es.eduardo.bookapp.DetalleLibroActivity;
import es.eduardo.bookapp.ListadoLibrosPrestadosActivity;
import es.eduardo.bookapp.LoginActivity;
import es.eduardo.bookapp.Modelos.Prestamo;
import es.eduardo.bookapp.PrestamoLibroActivity;

public class ControladorPrestamo extends AsyncTask<Void, Void, Void> {
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

    public ControladorPrestamo() {}

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

    public List<Prestamo> ListadoPrestamos() {
        int idUsuario = CogerNumExpediente(LoginActivity.u.getEmail());
        List<Prestamo> listaPrestamos = new ArrayList<>();
        AbrirConexión();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM prestamo WHERE idUsuario=" + idUsuario);
            while (rs.next()) {
                String idLibro = rs.getString("idLibro");
                String tituloLibro = rs.getString("tituloLibro");
                String fechaPrestamo = rs.getString("fechaPrestamo");
                String fechaDevolucion = rs.getString("fechaDevolucion");
                Prestamo prestamo = new Prestamo(idUsuario, idLibro, tituloLibro, fechaPrestamo, fechaDevolucion);
                listaPrestamos.add(prestamo);
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
        return listaPrestamos;
    }

    public List<Prestamo> ListadoPrestamosAdmin() {
        List<Prestamo> listaPrestamos = new ArrayList<>();
        AbrirConexión();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM prestamo");
            while (rs.next()) {
                int idUsuario = rs.getInt("idUsuario");
                String idLibro = rs.getString("idLibro");
                String tituloLibro = rs.getString("tituloLibro");
                String fechaPrestamo = rs.getString("fechaPrestamo");
                String fechaDevolucion = rs.getString("fechaDevolucion");
                Prestamo prestamo = new Prestamo(idUsuario, idLibro, tituloLibro, fechaPrestamo, fechaDevolucion);
                listaPrestamos.add(prestamo);
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
        return listaPrestamos;
    }

    public boolean EliminarPrestamo() {
        boolean result = true;
        int delete;
        AbrirConexión();
        try {
            String sql = "DELETE FROM prestamo WHERE idUsuario=" + ListadoLibrosPrestadosActivity.prestamoSeleccionado.getIdUsuario() +
                    " AND idLibro='" + ListadoLibrosPrestadosActivity.prestamoSeleccionado.getIdLibro() + "'";
            if (conn != null) {
                statement = conn.prepareStatement(sql);
                delete = statement.executeUpdate();
                if (delete > 0) {
                    PreparedStatement ps = conn.prepareStatement("UPDATE libros SET stock=(stock+1) WHERE ISBN=" + ListadoLibrosPrestadosActivity.prestamoSeleccionado.getIdLibro());
                    int update = ps.executeUpdate();
                    if (update > 0) result = false;
                }
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

    public int CogerNumExpediente(String email) {
        int num = 0;
        AbrirConexión();
        String sql = "SELECT * FROM usuarios WHERE email LIKE '" + email + "'";
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                num = rs.getInt("numExpediente");
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
        return num;
    }

    public boolean PrestamoLibro(Context context) {
        int idUsuario = CogerNumExpediente(LoginActivity.u.getEmail());
        boolean result = true;
        int insert;
        AbrirConexión();
        try {
            String sql = "INSERT INTO prestamo (idUsuario, idLibro, tituloLibro, fechaPrestamo, fechaDevolucion) VALUES (?, ?, ?, ?, ?)";
            if (conn != null) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM libros WHERE stock > 0 AND ISBN=" + DetalleLibroActivity.isbn);
                if (rs.next()) {
                    ResultSet rs2 = stmt.executeQuery("SELECT * FROM prestamo WHERE idUsuario=" + idUsuario
                            + " AND idLibro='" + DetalleLibroActivity.isbn + "'");
                    if (rs2.next()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Ya tienes prestado este libro")
                                .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
                        builder.show();
                        result = true;
                    } else {
                        PreparedStatement statement = conn.prepareStatement(sql);
                        statement.setInt(1, idUsuario);
                        statement.setString(2, DetalleLibroActivity.isbn);
                        statement.setString(3, DetalleLibroActivity.titulo);
                        statement.setString(4, controlador.FechaPrestamo());
                        statement.setString(5, controlador.FechaDevolucion());
                        insert = statement.executeUpdate();
                        if (insert > 0) {
                            PreparedStatement ps = conn.prepareStatement("UPDATE libros SET stock=(stock-1) WHERE ISBN=" + DetalleLibroActivity.isbn);
                            int update = ps.executeUpdate();
                            if (update > 0) result = false;
                        }
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("No hay stock del libro")
                            .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
                    builder.show();
                    result = true;
                }
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
}