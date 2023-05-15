package es.eduardo.bookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import es.eduardo.bookapp.Modelos.Prestamo;

public class ListaLibrosPrestadosUsuariosAdapter extends ArrayAdapter<Prestamo> {
    private Context context;
    private List<Prestamo> prestamos;

    public ListaLibrosPrestadosUsuariosAdapter(Context context, List<Prestamo> prestamos) {
        super(context, R.layout.lista_libros_usuarios, prestamos);
        this.context = context;
        this.prestamos = prestamos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.lista_libros_usuarios, parent, false);
        TextView tvIdLibro = (TextView) rowView.findViewById(R.id.tvIdLibro);
        TextView tvTituloLibro = (TextView) rowView.findViewById(R.id.tvTituloLibro);
        TextView tvFechaPrestamo = (TextView) rowView.findViewById(R.id.tvFechaPrestamo);
        TextView tvFechaDevolucion = (TextView) rowView.findViewById(R.id.tvFechaDevolucion);

        Prestamo prestamo = prestamos.get(position);
        tvIdLibro.setText(prestamo.getIdLibro());
        tvTituloLibro.setText(prestamo.getTituloLibro());
        tvFechaPrestamo.setText(prestamo.getFechaPrestamo());
        tvFechaDevolucion.setText(prestamo.getFechaDevolucion());

        return rowView;
    }
}