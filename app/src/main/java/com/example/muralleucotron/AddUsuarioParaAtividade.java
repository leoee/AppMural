package com.example.muralleucotron;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class AddUsuarioParaAtividade extends AppCompatActivity {
    ListView lista;
    FirebaseDAO fb = FirebaseDAO.getInstance();
    EditText campoUsuario;
    Sample_List adapter;
    Usuario aux;
    NestedScrollView scroll;
    private ArrayList<String> usuarios;
    private ArrayList<Atividade> listaDeUsuarios;
    Intent intent;
    Atividade atividade;
    String nomeAtividade;
    HashMap<String, String> mapaUsuariosJaAdicionados;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_usuario_para_atividade);
        fb.inicializaFirebase();
        fb.atualizaLista();
        fb.atualizaMapa();
        lista = findViewById(R.id.listViewAddUsuario);
        listaDeUsuarios = fb.getLista();
        campoUsuario = findViewById(R.id.editTextAddUsuario);
        usuarios = new ArrayList<>();
        mapaUsuariosJaAdicionados = new HashMap<>();

        adapter = new Sample_List(usuarios, this);
        lista.setAdapter(adapter);
        scroll = findViewById(R.id.Scroll);

        intent = getIntent();
        nomeAtividade = intent.getStringExtra("atividade");

        for(int i = 0 ; i < listaDeUsuarios.size(); i++){
            if(listaDeUsuarios.get(i).getTitulo().equals(nomeAtividade)){
                atividade = listaDeUsuarios.get(i);
                break;
            }
        }
    }

    public void btnAddUsuario(View view) {
        aux = fb.buscaUsuario(campoUsuario.getText().toString());

        if(aux == null){
            notifica("Usuário não existe.");
        }
        else if(mapaUsuariosJaAdicionados.containsKey(campoUsuario.getText().toString())){
            notifica("Usuário já existe na atividade.");
        }
        else{
            usuarios.add(aux.getNome() + " (" + campoUsuario.getText().toString()+ ")");
            mapaUsuariosJaAdicionados.put(campoUsuario.getText().toString(), "null");
            adapter.attLista(usuarios);
            adapter.notifyDataSetChanged();
            lista.invalidateViews();
            lista.refreshDrawableState();
        }
        campoUsuario.setText("");
    }
    public void notifica(String msg){
        Toast toast = Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT);
        toast.show();
    }

    public void btnFinalizar(View view) {
        if(usuarios.size() == 0){
            notifica("A lista está vazia");
        }
        else{
            for(int i = 0; i < listaDeUsuarios.size(); i++){
                if(atividade.getTitulo().equals(listaDeUsuarios.get(i).getTitulo())){
                    Atividade aux = listaDeUsuarios.get(i);
                    aux.setUsuarios(usuarios);

                    fb.addAtividade(aux);
                    fb.atualizaLista();
                    break;
                }
            }
            finish();
        }
    }
}
