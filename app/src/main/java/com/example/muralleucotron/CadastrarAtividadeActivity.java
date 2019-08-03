package com.example.muralleucotron;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;

public class CadastrarAtividadeActivity extends AppCompatActivity {
    EditText titulo, descricao;
    FirebaseDAO fb = FirebaseDAO.getInstance();
    Usuario user;
    String usuario;
    Intent intent;
    CheckBox sf, hd, outros;
    private String m_Text = "";
    ImageView imgDataInicio, imgDataTermino;
    boolean setDataInicio = false, setDataTermino = false;
    int year_x, month_x, day_x, year_x_termino, month_x_termino, day_x_termino;
    TextView lblDataInicio, lblDataTermino;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_atividade);
        this.setTitle("Cadastro de Atividades");
        fb.inicializaFirebase();
        fb.atualizaMapa();
        fb.atualizaLista();
        user = new Usuario();

        titulo = findViewById(R.id.editTextTituloCadastrar);
        descricao = findViewById(R.id.editTextDescricaoCadastrar);
        sf = findViewById(R.id.checkBoxCadastrarSF);
        hd = findViewById(R.id.checkBoxCadastrarHD);
        outros = findViewById(R.id.checkBoxCadastrarOutros);


        intent = getIntent();
        usuario = intent.getStringExtra("usuario");
        user = fb.buscaUsuario(usuario);

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        day_x = cal.get(Calendar.DAY_OF_MONTH);
        month_x = cal.get(Calendar.MONTH);
        year_x_termino = cal.get(Calendar.YEAR);
        day_x_termino = cal.get(Calendar.DAY_OF_MONTH);
        month_x_termino = cal.get(Calendar.MONTH);
        imgDataInicio = (ImageView) findViewById(R.id.imagemData);
        imgDataTermino = (ImageView) findViewById(R.id.imagemData2);
        lblDataInicio = (TextView) findViewById(R.id.lblData);
        lblDataTermino = (TextView) findViewById(R.id.lblData2);

    }


    public void clickSetData(View view) {
        showDialog(0);
    }

    public void clickSetDataTermino(View view) {
        showDialog(1);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == 0)
            return new DatePickerDialog(this, dpickerListener, year_x, month_x, day_x);
        else if(id == 1)
            return new DatePickerDialog(this, dpickerListener, year_x_termino, month_x_termino, day_x_termino);
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            if(setDataInicio == false && setDataTermino == false)
            {
                month_x = monthOfYear;
                day_x = dayOfMonth;
                lblDataInicio.setTextColor(Color.GREEN);
                setDataInicio = true;
            }
            else if(setDataTermino == false){
                month_x_termino = monthOfYear;
                day_x_termino = dayOfMonth;
                lblDataTermino.setTextColor(Color.GREEN);
                setDataTermino = true;
            }
        }
    };
    public void btnAddAtividade(View view) {
        if (titulo.getText().toString().equals("") || descricao.getText().toString().equals("")){
            notifica("Preencha todos os campos!");
        }
        else if (!sf.isChecked() && !hd.isChecked() && !outros.isChecked()){
            notifica("Selecione pelo menos um setor!");
        }
        else if(setDataInicio == false || setDataTermino == false){
            notifica("Selecione o prazo desta atividade.");
        }
        else if(titulo.getText().toString().length() > 20){
            notifica("O título deve conter no máximo 20 caracteres.");
        }
        else{
            new AlertDialog.Builder(this)
                    .setTitle("Aviso")
                    .setMessage("Desejar adicionar usuários para esta atividade?")
                    .setPositiveButton("Sim.", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                            ArrayList<String> usuarios = new ArrayList<>();
                            usuarios.add("Sem Usuários");
                            Atividade e = new Atividade();
                            e.setTitulo(titulo.getText().toString());
                            e.setDescricao(descricao.getText().toString());
                            e.setUsuarioCriador(usuario);
                            System.out.println("user: " + user);
                            e.setNomeCriador(user.getNome());
                            if(sf.isChecked() && hd.isChecked() && outros.isChecked()){
                                e.setSetor("todos");
                            }
                            else if(sf.isChecked() && outros.isChecked()){
                                e.setSetor("sfoutros");
                            }
                            else if(outros.isChecked() && hd.isChecked()){
                                e.setSetor("hdoutros");
                            }
                            else if(sf.isChecked() && hd.isChecked()){
                                e.setSetor("sfhd");
                            }
                            else if(sf.isChecked()){
                                e.setSetor("sf");
                            }
                            else if(hd.isChecked()){
                                e.setSetor("hw");
                            }
                            else if(outros.isChecked()){
                                e.setSetor("outros");
                            }
                            e.setProgresso(0);
                            String dataInicio = (day_x) + "/" + (month_x);
                            e.setDataInicio(dataInicio);
                            String dataTermino = (day_x_termino) + "/" + (month_x_termino);
                            e.setDataTermino(dataTermino);
                            e.setUsuarios(usuarios);
                            fb.addAtividade(e);
                            Bundle bundle = new Bundle();
                            bundle.putString("atividade", e.getTitulo());
                            Intent intent = new Intent(getApplicationContext(), AddUsuarioParaAtividade.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Não.", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finish();
                            ArrayList<String> usuarios = new ArrayList<>();
                            usuarios.add("Sem Usuários");
                            Atividade e = new Atividade();
                            e.setTitulo(titulo.getText().toString());
                            e.setDescricao(descricao.getText().toString());
                            e.setUsuarioCriador(usuario);
                            e.setNomeCriador(user.getNome());
                            if(sf.isChecked() && hd.isChecked() && outros.isChecked()){
                                e.setSetor("todos");
                            }
                            else if(sf.isChecked() && outros.isChecked()){
                                e.setSetor("sfoutros");
                            }
                            else if(outros.isChecked() && hd.isChecked()){
                                e.setSetor("hwoutros");
                            }
                            else if(sf.isChecked() && hd.isChecked()){
                                e.setSetor("sfhw");
                            }
                            else if(sf.isChecked()){
                                e.setSetor("sf");
                            }
                            else if(hd.isChecked()){
                                e.setSetor("hw");
                            }
                            else if(outros.isChecked()){
                                e.setSetor("outros");
                            }
                            e.setProgresso(0);
                            String dataInicio = (day_x) + "/" + (month_x);
                            e.setDataInicio(dataInicio);
                            String dataTermino = (day_x_termino) + "/" + (month_x_termino);
                            e.setDataTermino(dataTermino);
                            e.setUsuarios(usuarios);
                            fb.addAtividade(e);
                            fb.atualizaLista();
                        }
                    }).show();
        }

    }

    public void notifica(String msg){
        Toast toast = Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT);
        toast.show();
    }
}
