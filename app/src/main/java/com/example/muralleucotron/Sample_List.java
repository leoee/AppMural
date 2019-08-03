package com.example.muralleucotron;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class Sample_List extends BaseAdapter{
    private List<String> usuarios = null;
    private Activity act;

    public Sample_List(List<String> usuarios, Activity act){
        this.usuarios = usuarios;
        this.act = act;
    }
    @Override
    public int getCount() {
        return usuarios.size();
    }

    @Override
    public Object getItem(int position) {
        return usuarios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void attLista(List<String> usuarios){
        this.usuarios = usuarios;
    }
    public void removeItem(int position){
        usuarios.remove(position);
    }


    public String getViagem(int position){
        return usuarios.get(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = act.getLayoutInflater().inflate(R.layout.lista_simples, parent, false);
        TextView txt = view.findViewById(R.id.txtUsuarioLista);
        txt.setText(this.getItem(position).toString());

        return view;
    }
}
