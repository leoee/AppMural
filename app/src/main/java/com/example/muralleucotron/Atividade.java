package com.example.muralleucotron;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;

public class Atividade {
    public String getNomeCriador() {
        return nomeCriador;
    }

    public void setNomeCriador(String nomeCriador) {
        this.nomeCriador = nomeCriador;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    private String nomeCriador;

    public String getUsuarioCriador() {
        return usuarioCriador;
    }

    public void setUsuarioCriador(String usuarioCriador) {
        this.usuarioCriador = usuarioCriador;
    }

    private String usuarioCriador;
    private String descricao;
    private String titulo;

    public ArrayList<String> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ArrayList<String> usuarios) {
        this.usuarios = usuarios;
    }

    private ArrayList<String> usuarios;

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    private String setor;

    public String getDataInicio() {
        return DataInicio;
    }

    public void setDataInicio(String dataInicio) {
        DataInicio = dataInicio;
    }

    public String getDataTermino() {
        return DataTermino;
    }

    public void setDataTermino(String dataTermino) {
        DataTermino = dataTermino;
    }

    private String DataInicio;
    private String DataTermino;

    public int getProgresso() {
        return progresso;
    }

    public void setProgresso(int progresso) {
        this.progresso = progresso;
    }

    private int progresso;

}
/*            <de.hdodenhof.circleimageview.CircleImageView
                android:id="@+id/fotoPerfil"
                android:layout_marginTop="5dp"
                android:layout_marginLeft="5dp"
                android:layout_width="90dp"
                android:layout_height="90dp"
                android:layout_alignParentLeft="true"
                android:layout_alignParentTop="true"
                android:src="@drawable/ic_clear_black_24dp" />
                */
