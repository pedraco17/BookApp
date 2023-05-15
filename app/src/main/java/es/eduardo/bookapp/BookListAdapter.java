package es.eduardo.bookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import es.eduardo.bookapp.Modelos.Libros;

public class BookListAdapter extends ArrayAdapter<Libros> {
    private Context context;
    private List<Libros> libros;

    public BookListAdapter(Context context, List<Libros> libros) {
        super(context, R.layout.lista_libros, libros);
        this.context = context;
        this.libros = libros;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.lista_libros, parent, false);
        TextView tvTitulo = (TextView) rowView.findViewById(R.id.tvTitulo);
        TextView tvAutor = (TextView) rowView.findViewById(R.id.tvAutor);
        TextView tvEditorial = (TextView) rowView.findViewById(R.id.tvEditorial);

        Libros libro = libros.get(position);
        tvTitulo.setText(libro.getTitulo());
        tvAutor.setText(libro.getAutor());
        tvEditorial.setText(libro.getEditorial());

        return rowView;
    }
}