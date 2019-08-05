package com.example.muralleucotron;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DetalheAtividadeActivity extends AppCompatActivity {
    Intent intent;
    String nomeAtividade, nomeUsuario;
    Atividade atividade;
    boolean entrou = false;
    ImageView  btnMenos, btnMais;
    FloatingActionButton btnAdd;
    Sample_List adapter;
    TextView titulo, descricao, dataInicio, dataTermino, criador, porcentagem;
    ProgressBar progress;
    private ArrayList<Atividade> lista;
    private ArrayList<String> usuarios;
    ListView listView;
    Usuario aux;
    private String m_Text = "";
    FirebaseDAO fb = FirebaseDAO.getInstance();
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_atividade);
        fb.inicializaFirebase();
        fb.atualizaLista();
        fb.atualizaMapa();
        lista = fb.getLista();
        intent = getIntent();
        usuarios = new ArrayList<>();
        nomeAtividade = intent.getStringExtra("atividade");
        nomeUsuario = intent.getStringExtra("usuario");
        aux = fb.buscaUsuario(nomeUsuario);
        this.setTitle("Informações sobre " + nomeAtividade);

        for(int i = 0 ; i < lista.size(); i++){
            if(lista.get(i).getTitulo().equals(nomeAtividade)){
                atividade = lista.get(i);
                break;
            }
        }

        listView = findViewById(R.id.listViewMostraDetalhes);
        titulo = findViewById(R.id.textTituloMostraDetalhes);
        descricao = findViewById(R.id.textDescricao);
        dataInicio = findViewById(R.id.textViewDataInicio);
        dataTermino = findViewById(R.id.textViewDataTermino);
        criador = findViewById(R.id.textCriador);
        porcentagem = findViewById(R.id.textProgresso);
        progress = findViewById(R.id.progressBar2);
        btnAdd = findViewById(R.id.btnAddNaAtividade);
        btnMais = findViewById(R.id.imageView3);
        btnMenos = findViewById(R.id.imageView2);

        usuarios = atividade.getUsuarios();
        entrou = false;
        for(int i = 0 ; i < usuarios.size(); i++){
            if(usuarios.get(i).equals(aux.getNome() + " (" + nomeUsuario + ")")){
                entrou = true;
                break;
            }
        }
        System.out.println(entrou);
        if(aux.getPermissao().equals("0")) {
            if (atividade.getProgresso() == 100 || !atividade.getSetor().contains(aux.getSetor())) {
                btnMenos.setVisibility(View.INVISIBLE);
                btnMais.setVisibility(View.INVISIBLE);
                btnAdd.setVisibility(View.INVISIBLE);
            } else if (entrou == false) {
                btnMenos.setVisibility(View.INVISIBLE);
                btnMais.setVisibility(View.INVISIBLE);
                btnAdd.setVisibility(View.VISIBLE);
            } else if (entrou) {
                btnAdd.setVisibility(View.INVISIBLE);
                btnMenos.setVisibility(View.VISIBLE);
                btnMais.setVisibility(View.VISIBLE);
            }
        }
        if(!atividade.getSetor().contains(aux.getSenha()) && entrou){
            btnAdd.setVisibility(View.INVISIBLE);
            btnMenos.setVisibility(View.VISIBLE);
            btnMais.setVisibility(View.VISIBLE);
        }
        adapter = new Sample_List(usuarios, this);
        listView.setAdapter(adapter);
        titulo.setText(atividade.getTitulo());
        descricao.setText(atividade.getDescricao());
        dataInicio.setText(atividade.getDataInicio());
        dataTermino.setText(atividade.getDataTermino());
        criador.setText(atividade.getNomeCriador() + " (" + atividade.getUsuarioCriador() + ")");
        porcentagem.setText(Integer.toString(atividade.getProgresso()) + "%");
        progress.setProgress(atividade.getProgresso());

    }

    public void btnAddNaAtividade(View view) {
        if(usuarios.get(0).equals("Sem Usuários")){
            usuarios.clear();
        }
        usuarios.add(aux.getNome() + " (" + nomeUsuario + ")");
        atividade.setUsuarios(usuarios);
        fb.addAtividade(atividade);
        fb.atualizaLista();
        finish();
    }
    public void btnMais(View view) {
        if(progress.getProgress() >= 100){
            notifica("Não é possível aumentar a porcentagem.");
            return;
        }
        atividade.setProgresso(atividade.getProgresso() + 1);
        progress.setProgress(progress.getProgress() + 1);
        porcentagem.setText(Integer.toString(progress.getProgress()) + "%");
        fb.addAtividade(atividade);
        fb.atualizaLista();
    }

    public void btnMenos(View view) {
        if(progress.getProgress() <= 0){
            notifica("Não é possível diminuir a porcentagem.");
            return;
        }
        atividade.setProgresso(atividade.getProgresso() - 1);
        progress.setProgress(progress.getProgress() - 1);
        porcentagem.setText(Integer.toString(progress.getProgress()) + "%");
        fb.addAtividade(atividade);
        fb.atualizaLista();
    }
    public void notifica(String msg){
        Toast toast = Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT);
        toast.show();
    }

    public void btnAjustePorcentagem(View view) {
        if(entrou) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Configurar Porcentagem");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

            builder.setPositiveButton("Ajustar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    int valor = Integer.parseInt(m_Text);
                    if (valor < 0 || valor > 100) {
                        notifica("Valor inválido!");
                    } else {
                        porcentagem.setText(m_Text);
                        atividade.setProgresso(valor);
                        progress.setProgress(valor);
                        fb.addAtividade(atividade);
                        fb.atualizaLista();
                    }

                }
            });
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
        else{
            notifica("Somente integrantes da atividade podem ajustar a porcentagem");
        }
    }
}
