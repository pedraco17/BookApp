package es.eduardo.bookapp.Controladores;

import android.annotation.SuppressLint;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.eduardo.bookapp.R;

public class Controlador {
    public Controlador() {}

    public boolean validarEmail(String email){
        String EMAIL_PATTERN = "^\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*(\\.\\w{2,3})+$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @SuppressLint("NewApi")
    public String getSHA256Hash(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }

    public void MostrarPassword(EditText etPassword, View view){
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

    public String FechaDevolucion() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, 3);
        Date fechaEnTresSemanas = calendar.getTime();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String fechaDevolucion = formato.format(fechaEnTresSemanas);
        return fechaDevolucion;
    }

    public String FechaPrestamo() {
        Date hoy = Calendar.getInstance().getTime();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String fechaHoy = formato.format(hoy);
        return fechaHoy;
    }

    public long DiasRestantes(String fecha) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date fechaDevolucion = format.parse(fecha);
            Date fechaActual = new Date();
            long diffEnMilisegundos = fechaDevolucion.getTime() - fechaActual.getTime();
            long dias = TimeUnit.DAYS.convert(diffEnMilisegundos, TimeUnit.MILLISECONDS);
            return dias;
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
}