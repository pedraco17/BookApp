package es.eduardo.bookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import es.eduardo.bookapp.Modelos.Libros;

public class BookListAdapter extends ArrayAdapter<Libros> {
    private Context context;
    private List<Libros> libros;
    private List<Libros> listaOriginal;

    public BookListAdapter(Context context, List<Libros> libros) {
        super(context, R.layout.lista_libros, libros);
        this.context = context;
        this.libros = libros;
        listaOriginal = new ArrayList<>();
        listaOriginal.addAll(libros);
    }

    public void Filtrado(String cad) {
        int longitud = cad.length();
        if (longitud == 0) {
            libros.clear();
            libros.addAll(listaOriginal);
        } else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                List<Libros> lista = libros.stream().filter(i -> i.getTitulo().toLowerCase()
                        .contains(cad.toLowerCase())).collect(Collectors.toList());
                libros.clear();
                libros.addAll(lista);
            } else {
                for (Libros l : listaOriginal) {
                    if(l.getTitulo().toLowerCase().contains(cad.toLowerCase())) {
                        libros.add(l);
                    }
                }
            }
        }
        notifyDataSetChanged();
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