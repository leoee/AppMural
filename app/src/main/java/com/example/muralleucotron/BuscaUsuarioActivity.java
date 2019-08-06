package com.example.muralleucotron;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class BuscaUsuarioActivity extends AppCompatActivity {
    Usuario user;
    FirebaseDAO fb = FirebaseDAO.getInstance();
    EditText nome, sobrenome, cargo, nomeUsuario;
    CheckBox sf, hw, outros;
    String usuario;
    Intent intent;
    TextView txtSenha;
    Usuario auxiliar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_usuario);
        fb.inicializaFirebase();
        fb.atualizaLista();
        fb.atualizaMapa();


        intent = getIntent();
        usuario = intent.getStringExtra("usuario");
        user = fb.buscaUsuario(usuario);

        this.setTitle("Informações sobre: ");

        nome = findViewById(R.id.editTextNomeEdicao);
        sobrenome = findViewById(R.id.editTextSobrenomeEdicao);
        cargo = findViewById(R.id.editTextCargoEdicao);
        nomeUsuario = findViewById(R.id.editTextNomeUsuarioEdicao);
        txtSenha = findViewById(R.id.textView4);

        nome.setEnabled(false);
        sobrenome.setEnabled(false);
        cargo.setEnabled(false);
        txtSenha.setVisibility(View.INVISIBLE);
        txtSenha.setEnabled(false);


        sf = findViewById(R.id.checkBox1Edicao);
        hw = findViewById(R.id.checkBox2Edicao);
        outros = findViewById(R.id.checkBox3Edicao);

        sf.setEnabled(false);
        hw.setEnabled(false);
        outros.setEnabled(false);


        sf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    hw.setChecked(false);
                    outros.setChecked(false);
                }
            }
        });
        hw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sf.setChecked(false);
                    outros.setChecked(false);
                }
            }
        });
        outros.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sf.setChecked(false);
                    hw.setChecked(false);
                }
            }
        });
    }

    public void btnSearchUser(View view) {
        auxiliar = fb.buscaUsuario(nomeUsuario.getText().toString());
        if(auxiliar == null){
            notifica("Nome de usuário inválido!");
        }
        else{
            if(auxiliar.getNomeUsuario().equals(user.getNomeUsuario()) || user.getPermissao().equals("1")){
                txtSenha.setVisibility(View.VISIBLE);
                txtSenha.setEnabled(true);
            }
            nome.setText(auxiliar.getNome());
            sobrenome.setText(auxiliar.getSobrenome());
            cargo.setText(auxiliar.getCargo());
            this.setTitle("Informações sobre: " + auxiliar.getNomeUsuario());
            if(auxiliar.getSetor().equals("sf"))
                sf.setChecked(true);
            else if(auxiliar.getSetor().equals("hw"))
                hw.setChecked(true);
            else if(auxiliar.getSetor().equals("outros"))
                outros.setChecked(true);
        }
    }
    public void notifica(String msg){
        Toast toast = Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT);
        toast.show();
    }

    /*public void btnEditaNome(View view) {
        if(auxiliar == null){
            notifica("Não é possível altera o nome!");
        }
        else if(auxiliar.getNomeUsuario().equals(user.getNomeUsuario()) || user.getPermissao().equals("1")){
            nome.setEnabled(true);
        }
        else{
            notifica("Você não permissão para alterar.");
        }
    }

    public void btnEditaSobrenome(View view) {
        if(auxiliar == null){
            notifica("Não é possível altera o nome!");
        }
        else if(auxiliar.getNomeUsuario().equals(user.getNomeUsuario()) || user.getPermissao().equals("1")){
            sobrenome.setEnabled(true);
        }
        else{
            notifica("Você não permissão para alterar.");
        }
    }

    public void btnEditaCargo(View view) {
        if(auxiliar == null){
            notifica("Não é possível altera o nome!");
        }
        else if(auxiliar.getNomeUsuario().equals(user.getNomeUsuario()) || user.getPermissao().equals("1")){
            cargo.setEnabled(true);
        }
        else{
            notifica("Você não permissão para alterar.");
        }
    }*/

    public void clickResetSenha(View view) {
        if(auxiliar == null){
            notifica("Informe um usuário para resetar a senha.");
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle("Aviso")
                    .setMessage("Você deseja resetar a senha do usuário informado?")
                    .setPositiveButton("Sim.", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            auxiliar.setSenha("null");
                            fb.addUsuario(auxiliar);
                            fb.atualizaMapa();
                            txtSenha.setEnabled(false);
                        }
                    })
                    .setNegativeButton("Não.", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                        }
                    }).show();
        }
    }

    public void btnApagarUsuario(View view) {
        if(auxiliar == null){
            notifica("Escolha um usuário para apagar.");
        }
        else{

        }
    }
}
