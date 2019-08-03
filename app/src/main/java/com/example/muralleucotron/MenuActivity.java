package com.example.muralleucotron;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity {
    private DrawerLayout dl;
    SwipeRefreshLayout pullToRefresh;
    private ArrayList<Atividade> lista;
    private ArrayList<Atividade> software, hardware, outros;
    FirebaseDAO fb = FirebaseDAO.getInstance();
    private ActionBarDrawerToggle abdt;
    Intent intent;
    String usuario;
    Usuario user, aux;
    Custom_List adapter;
    ListView listView;
    CircleImageView imgPerfil;
    int mPosition;
    Boolean mSortable;
    String mDragString;
    Atividade e;
    public static final int ATT = 3;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        this.setTitle("Lista de Atividades");
        listView = (ListView) findViewById(R.id.list);
        listView.setLongClickable(true);
        fb.inicializaFirebase();
        fb.atualizaLista();
        fb.atualizaMapa();
        lista = fb.getLista();
        user = new Usuario();

        adapter = new Custom_List(lista, this);
        listView.setAdapter(adapter);

        pullToRefresh = findViewById(R.id.pullToRefresh);

        dl = findViewById(R.id.dl);
        abdt = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);

        dl.addDrawerListener(abdt);
        abdt.syncState();
        intent = getIntent();
        usuario = intent.getStringExtra("usuario");
        aux = fb.buscaUsuario(usuario);
        if(aux.getPermissao().equals("0"))
            adapter.getFilter().filter(aux.getSetor());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("atividade", adapter.getAtividade(position).getTitulo());
                bundle.putString("usuario", usuario);
                Intent intent = new Intent(getApplicationContext(), DetalheAtividadeActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, ATT);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           final int index, long arg3) {

                if(aux.getPermissao().equals("1")){
                    new AlertDialog.Builder(listView.getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Aviso")
                            .setMessage("Você desejar apagar essa atividade?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    fb.removeViagem(adapter.getAtividade(index).getTitulo());
                                    lista = fb.getLista();
                                    adapter.attLista(lista);
                                    adapter.notifyDataSetChanged();
                                    listView.invalidateViews();
                                    listView.refreshDrawableState();
                                    notifica("Atividade apagada com sucesso.");
                                }

                            })
                            .setNegativeButton("Não", null)
                            .show();
                }
                else{
                    System.out.println(adapter.getAtividade(index).getUsuarioCriador() + " " + usuario);
                    if(adapter.getAtividade(index).getUsuarioCriador().equals(usuario)){
                        new AlertDialog.Builder(listView.getContext())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Aviso")
                                .setMessage("Você desejar apagar essa atividade?")
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        fb.removeViagem(adapter.getAtividade(index).getTitulo());
                                        lista = fb.getLista();
                                        adapter.attLista(lista);
                                        adapter.getFilter().filter(aux.getSetor());
                                        adapter.notifyDataSetChanged();
                                        listView.invalidateViews();
                                        listView.refreshDrawableState();
                                        notifica("Atividade apagada com sucesso.");
                                    }

                                })
                                .setNegativeButton("Não", null)
                                .show();
                    }
                    else{
                        notifica("Você não pode apagar essa atividade.");
                    }
                }
                return true;
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setTitle("Lista de Atividades");
                lista = fb.getLista();
                adapter.attLista(lista);
                if(aux.getPermissao().equals("0"))
                    adapter.getFilter().filter(aux.getSetor());
                adapter.notifyDataSetChanged();
                listView.invalidateViews();
                listView.refreshDrawableState();
                pullToRefresh.setRefreshing(false);
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView nav_view = findViewById(R.id.nv);
        View headerView = nav_view.getHeaderView(0);
        TextView nomeUsuarioNavi = headerView.findViewById(R.id.txtNomeMenu);
        final Usuario aux;
        aux = fb.buscaUsuario(usuario);
        String nome = aux.getNome();
        nomeUsuarioNavi.setText(nome + " (" + usuario + ")");

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                if(id == R.id.addUsuario){
                    if(aux.getPermissao().equals("0")){
                        notifica("Somente usuários com permissão de administrador podem realizar cadastro.");
                    }
                    else {
                        Intent intent = new Intent(getApplicationContext(), CadastrarUsuarioActivity.class);
                        startActivity(intent);
                    }
                }
                else if(id == R.id.cadastrarAtividade){
                    Bundle bundle = new Bundle();
                    bundle.putString("usuario", usuario);
                    Intent intent = new Intent(getApplicationContext(), CadastrarAtividadeActivity.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, ATT);
                }
                else if(id == R.id.doing){
                    lista = fb.getLista();
                    adapter.attLista(lista);
                    adapter.getFilter().filter("minha" + aux.getNome());
                    dl.closeDrawers();
                }
                else if (id == R.id.done) {
                    lista = fb.getLista();
                    adapter.attLista(lista);
                    adapter.getFilter().filter("doing" + aux.getNome() + "(" + usuario + ")");
                    dl.closeDrawers();

                }
                else if(id == R.id.sair){
                    finish();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.dl);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Aviso")
                    .setMessage("Você desejar realmente sair do aplicativo?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("Não", null)
                    .show();
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 3){
            fb.atualizaLista();
            lista = fb.getLista();
            adapter.attLista(lista);
            if(aux.getPermissao().equals("0"))
                adapter.getFilter().filter(aux.getSetor());
            adapter.notifyDataSetChanged();
            listView.invalidateViews();
            listView.refreshDrawableState();
            dl.closeDrawers();
        }

        fb.atualizaLista();
        lista = fb.getLista();
        adapter.attLista(lista);
        if(aux.getPermissao().equals("0"))
            adapter.getFilter().filter(aux.getSetor());
        adapter.notifyDataSetChanged();
        listView.invalidateViews();
        listView.refreshDrawableState();
        dl.closeDrawers();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.meu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.hardware) {
            lista = fb.getLista();
            adapter.attLista(lista);
            adapter.getFilter().filter("hw");
        }
        else if(id == R.id.software){
            lista = fb.getLista();
            adapter.attLista(lista);
            adapter.getFilter().filter("sf");

        }
        else if(id == R.id.Outros){
            lista = fb.getLista();
            adapter.attLista(lista);
            adapter.getFilter().filter("outros");
        }
        return abdt.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
    public void notifica(String msg){
        Toast toast = Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT);
        toast.show();
    }
    public void startDrag(String string) {
        mPosition = -1;
        mSortable = true;
        mDragString = string;
        adapter.notifyDataSetChanged();
    }

    public void stopDrag() {
        mPosition = -1;
        mSortable = false;
        mDragString = null;
        adapter.notifyDataSetChanged();
    }
}
