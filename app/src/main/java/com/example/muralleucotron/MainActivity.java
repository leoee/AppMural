package com.example.muralleucotron;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText nomeUsuario;
    EditText senha;
    FirebaseDAO fb = FirebaseDAO.getInstance();
    Usuario aux;
    String senhaDigitada, confirmarSenha;
    private ArrayList<Atividade> lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setTitle("Credenciamento");

        fb.inicializaFirebase();
        fb.atualizaLista();
        fb.atualizaMapa();
        lista = fb.getLista();

        nomeUsuario = findViewById(R.id.editTextNomeUsuario);
        senha = findViewById(R.id.editTextSenha);
    }

    public void btnEntrar(View view) {
        String usuario = nomeUsuario.getText().toString();
        try{
            Thread.sleep(1000);
        }catch(Exception e){

        }
        aux = fb.buscaUsuario(usuario);
        System.out.println("nome: " + usuario);
        if(aux == null){
            notifica("Nome de usuário incorreto.");
        }
        else{
            if(aux.getSenha().equals("null")){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //you should edit this to fit your needs
                builder.setTitle("Este é seu primeiro login e você deve cadastrar uma senha");

                final EditText one = new EditText(this);
                one.setHint("Senha");
                final EditText two = new EditText(this);
                two.setHint("Confirmar senha");

                //in my example i use TYPE_CLASS_NUMBER for input only numbers
                one.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                two.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

                LinearLayout lay = new LinearLayout(this);
                lay.setOrientation(LinearLayout.VERTICAL);
                lay.addView(one);
                lay.addView(two);
                builder.setView(lay);

                // Set up the buttons
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        senhaDigitada = one.getText().toString();
                        confirmarSenha = two.getText().toString();

                        if(!senhaDigitada.equals(confirmarSenha)){
                            notifica("Senhas não conferem!");
                        }
                        else if(senhaDigitada.length() < 4){
                            notifica("Senha deve conter mais de 4 caracteres!");
                        }
                        else{
                            notifica("Senha alterada com sucesso!");
                            aux.setSenha(senhaDigitada);
                            fb.addUsuario(aux);
                            finish();
                            Bundle bundle = new Bundle();
                            bundle.putString("usuario", aux.getNomeUsuario());
                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
            else if(senha.getText().toString().equals(aux.getSenha())){
                finish();
                Bundle bundle = new Bundle();
                bundle.putString("usuario", aux.getNomeUsuario());
                Intent intent = new Intent(this, MenuActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
            else{
                notifica("Dados inválidos!");
            }
        }
    }
    public void notifica(String msg){
        Toast toast = Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT);
        toast.show();
    }
}
