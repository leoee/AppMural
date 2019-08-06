package com.example.muralleucotron;

import android.support.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//https://www.youtube.com/watch?v=sT8jJPJqMEg
public class FirebaseDAO {
    private static FirebaseDAO fb;
    Usuario user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public static synchronized FirebaseDAO getInstance(){
        if(fb == null){
            fb = new FirebaseDAO();
        }
        return fb;
    }
    private static HashMap<String, Usuario> mapaUsuario = new HashMap<>();
    private static ArrayList<Atividade> atividades = new ArrayList<Atividade>();

    public ArrayList<Atividade> getLista() {
        return atividades;
    }

    Atividade atividadeAux;

    public void inicializaFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void addAtividade(Atividade atividade){
        databaseReference.child("Atividades").child(atividade.getTitulo()).setValue(atividade);
        atividades.add(atividade);
    }
    public void addAlgo(){
        databaseReference.child("nulo").child("nulo").setValue("nulo");
    }
    /*public void addImagem(String nomeUsuario, byte[] bytes){
        final String postId = FirebaseDatabase.getInstance().getReference().push().getKey();
        StorageReference mountainsRef = mStorageRef.child(nomeUsuario);

        UploadTask uploadTask = mountainsRef.putBytes(bytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }*/
   /* public void carregaImagem(final String nome){
        System.out.println("carrega imagem");
        StorageReference islandRef = mStorageRef.child(nome);

        final long ONE_MEGABYTE = 1024 * 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                mapaBytes.put(nome, bytes);
                System.out.println("passou aq");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }*/
    public void addUsuario(Usuario usuario){
        databaseReference.child("Usuarios").child(usuario.getNomeUsuario()).setValue(usuario);
        atualizaMapa();
    }

    public void atualizaLista(){
        Query query;

        query = databaseReference.child("Atividades");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                atividades.clear();
                for(DataSnapshot dados:dataSnapshot.getChildren()){
                    atividadeAux = new Atividade();
                    String key = dados.getKey();
                    atividadeAux.setTitulo(dados.child("titulo").getValue().toString());
                    atividadeAux.setProgresso(Integer.parseInt(dados.child("progresso").getValue().toString()));
                    atividadeAux.setDataTermino(dados.child("dataTermino").getValue().toString());
                    atividadeAux.setDataInicio(dados.child("dataInicio").getValue().toString());
                    atividadeAux.setNomeCriador(dados.child("nomeCriador").getValue().toString());
                    atividadeAux.setUsuarios((ArrayList) dados.child("usuarios").getValue());
                    atividadeAux.setUsuarioCriador(dados.child("usuarioCriador").getValue().toString());
                    atividadeAux.setSetor(dados.child("setor").getValue().toString());
                    atividadeAux.setDescricao(dados.child("descricao").getValue().toString());
                    atividades.add(atividadeAux);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void removeAtividade(final String valor){
        for(int i = 0; i < atividades.size(); i++){
            if(atividades.get(i).getTitulo().equals(valor))
                atividades.remove(i);
        }
        databaseReference.child("Atividades").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    String key = dados.getKey().toString();
                    if(key.equals(valor)){
                        dados.getRef().removeValue();
                        break;
                    }
                }
                atualizaLista();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void atualizaMapa(){
        databaseReference.child("Usuarios").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mapaUsuario.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    user = new Usuario();
                    String key = dados.getKey();
                    user.setNomeUsuario(key);
                    user.setNome(dados.child("nome").getValue().toString());
                    user.setSenha(dados.child("senha").getValue().toString());
                    user.setSobrenome(dados.child("sobrenome").getValue().toString());
                    user.setSetor(dados.child("setor").getValue().toString());
                    user.setCargo(dados.child("cargo").getValue().toString());
                    user.setPermissao(dados.child("permissao").getValue().toString());
                    mapaUsuario.put(key, user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public Usuario buscaUsuario(String usuario){
        if(mapaUsuario.containsKey(usuario)){
            return mapaUsuario.get(usuario);
        }
        else{
            return null;
        }
    }
}
