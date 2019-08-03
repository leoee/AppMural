package com.example.muralleucotron;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class Custom_List extends BaseAdapter implements Filterable {
    private List<Atividade> atividades = null;
    private List<Atividade> software = null;
    private ItemFilter itemFilter = new ItemFilter();
    private Activity act;

    public Custom_List(List<Atividade> viagens, Activity act){
        this.atividades = viagens;
        this.act = act;
        this.software = viagens;
    }
    @Override
    public int getCount() {
        return atividades.size();
    }

    @Override
    public Object getItem(int position) {
        return atividades.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void resetaMinhas(){
        this.atividades = this.atividades;
    }

    public void removeItem(int position){
        atividades.remove(position);
    }

    public void attLista(List<Atividade> viagens){
        this.atividades = viagens;
    }


    public Atividade getAtividade(int position){
        return atividades.get(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = act.getLayoutInflater().inflate(R.layout.list_item, parent, false);

        TextView txtNomeUsuario = view.findViewById(R.id.textNomeUsuarioList);
        ProgressBar mProgress = view.findViewById(R.id.progressBar);
        if(atividades.get(position).getProgresso() == 100)
            view.setBackgroundColor(Color.GREEN);
        else
            view.setBackgroundColor(Color.YELLOW);
        view.setMinimumHeight(600);
        TextView txtTitulo = view.findViewById(R.id.textTituloList);
        TextView data = view.findViewById(R.id.data);
        //TextView dataTermino = view.findViewById(R.id.dataTermino);
        TextView progresso = view.findViewById(R.id.textPorcentagem);
        TextView descricao = view.findViewById(R.id.textDescricaoList);
        txtTitulo.setText(atividades.get(position).getTitulo());
        data.setText(atividades.get(position).getDataInicio() + " até " + atividades.get(position).getDataTermino());
       // dataTermino.setText(atividades.get(position).getDataTermino());
        String desc = atividades.get(position).getDescricao();
        if(desc.length() <= 30)
            descricao.setText(desc);
        else
            descricao.setText(desc.substring(0, 30) + " ...");
        progresso.setText(Integer.toString(atividades.get(position).getProgresso()) + "%");
        mProgress.setProgress(atividades.get(position).getProgresso());
            txtNomeUsuario.setText(atividades.get(position).getNomeCriador());
       // }
        return view;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence setor) {
            FilterResults results = new FilterResults();
            final List<Atividade> list = atividades;
            boolean entrou = false;
            int count = list.size();
            final ArrayList<Atividade> nlist = new ArrayList<Atividade>(count);
            Atividade aux;
            if(setor.length() > 5 && !setor.equals("outros")) {
                if (setor.subSequence(0, 5).equals("doing")) { //doing+usuario
                    List<String> usuarios;
                    for (int i = 0; i < list.size(); i++) {
                        usuarios = list.get(i).getUsuarios();
                        for (int j = 0; j < usuarios.size(); j++) {
                            if (usuarios.get(j).equals(setor.subSequence(5, setor.length()))) {
                                entrou = true;
                                break;
                            }
                        }
                        if (entrou)
                            nlist.add(list.get(i));
                        entrou = false;
                    }
                    System.out.println(nlist.size());
                }
                else if(setor.subSequence(0, 5).equals("minha")){
                    for(int i = 0 ; i < list.size(); i++){
                        aux = list.get(i);
                        if(aux.getNomeCriador().equals(setor.subSequence(5, setor.length()))){
                            nlist.add(aux);
                        }
                    }
                }
            }
            if(setor.equals("sf") || setor.equals("hw") || setor.equals("todos") || setor.equals("outros")) {
                for (int i = 0; i < list.size(); i++) {
                    aux = list.get(i);
                    if (aux.getSetor().contains(setor) || aux.getSetor().equals("todos")) {
                        nlist.add(aux);
                    }
                }
            }
            results.values = nlist;
            results.count = nlist.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            atividades = (ArrayList<Atividade>) results.values;
            notifyDataSetChanged();
        }

    }

    public String padronizaHora(String data){
        if(data.length() == 5)
            return data;
        int pos = 0;
        String resu = "";
        for(int i = 0; i < data.length(); i++){
            if(data.charAt(i) == ':'){
                pos = i;
                break;
            }
        }
        if(pos < 2){
            resu += "0";
        }
        resu += data.substring(0, pos);
        resu += ":";
        if(data.charAt(data.length()-1) == '0'){
            resu += "0";
        }
        resu += data.substring(pos + 1);
        return resu;
    }

    @Override
    public Filter getFilter() {
        return itemFilter;
    }
}
