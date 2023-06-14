package es.eduardo.bookapp.Controladores;

import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;

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

public class ControladorLibros extends AsyncTask<Void, Void, Void> {
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

    public ControladorLibros() {}

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

    public void DetalleLibro(TextView... tv) {
        AbrirConexión();
        try {
            String sql = "SELECT * FROM libros WHERE ISBN='" + ListadoLibrosActivity.libroSeleccionado.getISBN() + "'";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                tv[0].setText(rs.getString("ISBN"));
                tv[1].setText(rs.getString("titulo"));
                tv[2].setText(rs.getString("autor"));
                tv[3].setText(rs.getString("editorial"));
                tv[4].setText(rs.getString("fechaLanzamiento"));
                tv[5].setText(rs.getString("numPaginas"));
                tv[6].setText(rs.getString("stock"));
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
    }

    public List<Libros> ListadoLibros() {
        List<Libros> listaLibros = new ArrayList<>();
        AbrirConexión();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM libros");
            while (rs.next()) {
                String isbn = rs.getString("ISBN");
                String titulo = rs.getString("titulo");
                String autor = rs.getString("autor");
                String editorial = rs.getString("editorial");
                String fechaLanzamiento = rs.getString("fechaLanzamiento");
                int numPaginas = rs.getInt("numPaginas");
                int stock = rs.getInt("stock");
                Libros libro = new Libros(isbn, titulo, autor, editorial, fechaLanzamiento, numPaginas, stock);
                listaLibros.add(libro);
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
        return listaLibros;
    }

    public void DetalleLibroAdmin(TextView... tv) {
        AbrirConexión();
        try {
            String sql = "SELECT * FROM libros WHERE ISBN='" + ListadoLibrosAdminActivity.libroSeleccionado.getISBN() + "'";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                tv[0].setText(rs.getString("ISBN"));
                tv[1].setText(rs.getString("titulo"));
                tv[2].setText(rs.getString("autor"));
                tv[3].setText(rs.getString("editorial"));
                tv[4].setText(rs.getString("fechaLanzamiento"));
                tv[5].setText(rs.getString("numPaginas"));
                tv[6].setText(rs.getString("stock"));
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
    }

    public boolean AñadirLibro(EditText etISBN, EditText etTitulo, EditText etAutor, EditText etEditorial, TextView tvFecha, EditText etNumPaginas, EditText etStock) {
        int insert;
        boolean result = true;
        AbrirConexión();
        try {
            String sql = "INSERT INTO libros (ISBN, titulo, autor, editorial, fechaLanzamiento, numPaginas, stock) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            if (conn != null) {
                statement = conn.prepareStatement(sql);
                if (!etISBN.getText().toString().equals("")) {
                    if (!comprobarISBN(etISBN.getText().toString())) {
                        statement.setString(1, etISBN.getText().toString());
                    } else {
                        etISBN.setText("");
                    }
                }
                if (!etTitulo.getText().toString().equals(""))
                    statement.setString(2, etTitulo.getText().toString());
                if (!etAutor.getText().toString().equals(""))
                    statement.setString(3, etAutor.getText().toString());
                if (!etEditorial.getText().toString().equals(""))
                    statement.setString(4, etEditorial.getText().toString());
                if (!tvFecha.getText().toString().equals("")) {
                    statement.setString(5, tvFecha.getText().toString());
                }
                if (!etNumPaginas.getText().toString().equals("")) {
                    statement.setInt(6, Integer.parseInt(etNumPaginas.getText().toString()));
                }
                if (!etStock.getText().toString().equals("")) {
                    statement.setInt(7, Integer.parseInt(etStock.getText().toString()));
                }
                insert = statement.executeUpdate();
                if (insert > 0) {
                    result = false;
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

    public boolean ModificarLibro(EditText etTitulo, EditText etAutor, EditText etEditorial, TextView tvFecha, EditText etNumPaginas, EditText etStock, EditText etISBN) {
        boolean result = true;
        int update;
        AbrirConexión();
        try {
            String sql = "UPDATE libros SET titulo=?, autor=?, editorial=?, fechaLanzamiento=?, numPaginas=?, stock=? WHERE ISBN=?";
            if (conn != null) {
                statement = conn.prepareStatement(sql);
                if (!etTitulo.getText().toString().equals(""))
                    statement.setString(1, etTitulo.getText().toString());
                if (!etAutor.getText().toString().equals(""))
                    statement.setString(2, etAutor.getText().toString());
                if (!etEditorial.getText().toString().equals(""))
                    statement.setString(3, etEditorial.getText().toString());
                if (!tvFecha.getText().toString().equals(""))
                    statement.setString(4, tvFecha.getText().toString());
                if (!etNumPaginas.getText().toString().equals(""))
                    statement.setInt(5, Integer.parseInt(etNumPaginas.getText().toString()));
                if (!etStock.getText().toString().equals(""))
                    statement.setInt(6, Integer.parseInt(etStock.getText().toString()));
                statement.setString(7, etISBN.getText().toString());
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

    public boolean EliminarLibro(String isbn) {
        boolean result = true;
        int delete;
        AbrirConexión();
        try {
            String sql = "DELETE FROM libros WHERE ISBN='" + isbn + "'";
            if (conn != null) {
                statement = conn.prepareStatement(sql);
                delete = statement.executeUpdate();
                if (delete > 0) result = false;
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

    public boolean comprobarISBN(String isbn) {
        AbrirConexión();
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM libros");
            while (rs.next()) {
                if (isbn.equals(rs.getString("ISBN"))) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void CargarLibro(EditText etISBN, EditText etTitulo, EditText etAutor, EditText etEditorial, TextView tvFecha, EditText etNumPaginas, EditText etStock) {
        AbrirConexión();
        try {
            String sql = "SELECT * FROM libros WHERE ISBN='" + ListadoLibrosAdminActivity.libroSeleccionado.getISBN() + "'";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                etISBN.setText(rs.getString("ISBN"));
                etTitulo.setText(rs.getString("titulo"));
                etAutor.setText(rs.getString("autor"));
                etEditorial.setText(rs.getString("editorial"));
                tvFecha.setText(rs.getString("fechaLanzamiento"));
                etNumPaginas.setText(rs.getString("numPaginas"));
                etStock.setText(rs.getString("stock"));
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
    }
}
