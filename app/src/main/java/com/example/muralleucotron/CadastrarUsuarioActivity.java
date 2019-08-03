package com.example.muralleucotron;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


public class CadastrarUsuarioActivity extends AppCompatActivity {
    Usuario user;
    FirebaseDAO fb = FirebaseDAO.getInstance();
    EditText nome, sobrenome, nomeUsuario, cargo;
    CheckBox sf, hw, outros;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_usuario);
        this.setTitle("Cadastro de Usuário");
        fb.inicializaFirebase();

        nome = findViewById(R.id.editTextNome);
        sobrenome = findViewById(R.id.editTextSobrenome);
        nomeUsuario = findViewById(R.id.editTextUsername);
        cargo = findViewById(R.id.editTextCargo);

        sf = findViewById(R.id.checkBox1);
        hw = findViewById(R.id.checkBox2);
        outros = findViewById(R.id.checkBox3);

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

    public void btnCadastrar(View view) {

        if(nome.getText().toString().equals("") || sobrenome.getText().toString().equals("")
             || nomeUsuario.getText().toString().equals("")
                || cargo.getText().toString().equals("")){
            notifica("Preencha todos os campos!");
        }
        else{
            if(!sf.isChecked() && !hw.isChecked() && !outros.isChecked()){
                notifica("Selecione um setor!");
            }
            else if(fb.buscaUsuario(nomeUsuario.getText().toString()) != null){
                notifica("Nome de usuário já existente!");
            }
            else if(!validaNome(nomeUsuario.getText().toString())){
                notifica("Nome de usuário inválido!");
            }
            else{
                user = new Usuario();
                user.setNome(nome.getText().toString());
                user.setSobrenome(sobrenome.getText().toString());
                user.setNomeUsuario(nomeUsuario.getText().toString());
                user.setCargo(cargo.getText().toString());
                user.setSenha("null");
                if(sf.isChecked()){
                    user.setSetor("sf");
                }
                else if(outros.isChecked()){
                    user.setSetor("outros");
                }
                else if(hw.isChecked()){
                    user.setSetor("hw");
                }


                new AlertDialog.Builder(this)
                        .setTitle("Permissão")
                        .setMessage("O usuário deve possuir permissão de administrador?")
                        .setPositiveButton("Sim.", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                user.setPermissao("1");
                                fb.addUsuario(user);
                                notifica("Usuário cadastrado com sucesso!");
                                finish();
                            }
                        })
                        .setNegativeButton("Não.", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                user.setPermissao("0");
                                fb.addUsuario(user);
                                notifica("Usuário cadastrado com sucesso!");
                                finish();
                            }
                        }).show();
            }
        }
    }

    public void notifica(String msg){
        Toast toast = Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT);
        toast.show();
    }
    public Boolean validaNome(String nome){
        if(nome.equals("null") || nome.equals("Sem Usuários") || nome.equals("doing") || nome.equals("done")
                || nome.equals("outros") || nome.contains("/") || nome.contains("\\") || nome.length() < 4){
            return false;
        }


        return true;
    }
}
