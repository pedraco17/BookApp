package es.eduardo.bookapp.Modelos;

public class Usuarios {
    private int numExpediente;
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    private String confPassword;
    private String fechaNacimiento;

    public Usuarios() {}

    public Usuarios(int numExpediente, String nombre, String apellidos, String email, String password, String confPassword, String fechaNacimiento) {
        this.numExpediente = numExpediente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.password = password;
        this.confPassword = confPassword;
        this.fechaNacimiento = fechaNacimiento;
    }

    public int getNumExpediente() {
        return numExpediente;
    }

    public void setNumExpediente(int numExpediente) {
        this.numExpediente = numExpediente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfPassword() {
        return confPassword;
    }

    public void setConfPassword(String confPassword) {
        this.confPassword = confPassword;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}