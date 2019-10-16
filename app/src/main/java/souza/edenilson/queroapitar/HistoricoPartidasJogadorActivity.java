package souza.edenilson.queroapitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import souza.edenilson.Adapter.AdapterHistoricoDePartidasDoJogador;
import souza.edenilson.Adapter.AdapterPartidasJogador;
import souza.edenilson.DataBase.PartidaDataBase;
import souza.edenilson.Interface.HistoricoPartidasJogadorRecyclerView_Interface;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.MetodosPublicos.PermissoesAcesso;
import souza.edenilson.Model.Partida;
import souza.edenilson.Model.PartidaTemp;
import souza.edenilson.Model.Usuario;

public class HistoricoPartidasJogadorActivity extends AppCompatActivity implements HistoricoPartidasJogadorRecyclerView_Interface {

    private static String TITULO = "Histórico de Partidas";

    // FIREBASE
    FirebaseDatabase firebaseDatabase;
    //FirebaseFirestore firebaseFirestore;
    DatabaseReference databaseReferenceUsuario;
    DatabaseReference databaseReferencePartida;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    // OBJETOS
    Usuario usuarioLogado;
    MetodosPublicos metodosPublicos;
    PermissoesAcesso permissoesAcesso;
    PartidaTemp partidaTemp;
    PartidaDataBase partidaDataBase;

    // COMPONENTES DE TELA
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView txt_sem_registro;

    // RECYCLERVIEW
    private RecyclerView recyclerViewHistoricoPartidasDoJogador;
    private RecyclerView.LayoutManager mLayoutManager;
    AdapterHistoricoDePartidasDoJogador adapterHistoricoDePartidasDoJogador;
    private List<Partida> listaDePartidasDoJogador = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.activity_historico_partidas_jogador);

        //Titulo da TELA
        App.setContext(this);
        Toolbar toolbar = findViewById(R.id.toolbarHistoricoPartidasJogador);
        toolbar.setTitle(TITULO);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão

        metodosPublicos = new MetodosPublicos();
        metodosPublicos.TemInternet(App.getContext(), HistoricoPartidasJogadorActivity.this, LoginActivity.class);

        // vem das telas de Login e Cadastro de Usuario
        Intent intent = getIntent();
        usuarioLogado =  (Usuario)intent.getSerializableExtra("usuarioLogado");

        if(usuarioLogado == null){
            Intent i = new Intent(HistoricoPartidasJogadorActivity.this, CriarContaActivity.class);
            startActivity(i);
        }

        InicializaObjetos();
        InicializaFirebase();
        InicializaRecyclerView();
        InicializaCampos();
        ConfiguraSwipeRefreshLayout();

        if(usuarioLogado != null){
            if(usuarioLogado.getTipoDeUsuario() == 2) {
                PreencheListaDePartidasJogador();
            }
        }

    }

    @Override
    protected void onStart() {
        metodosPublicos.TemInternet(App.getContext(), HistoricoPartidasJogadorActivity.this, LoginActivity.class );
        super.onStart();
    }

    @Override
    protected void onResume() {
        metodosPublicos.TemInternet(App.getContext(), HistoricoPartidasJogadorActivity.this, LoginActivity.class );
        super.onResume();
    }

    private void InicializaFirebase(){
        mAuth = FirebaseAuth.getInstance();
        // firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceUsuario = firebaseDatabase.getReference("usuario");
        databaseReferencePartida = firebaseDatabase.getReference("partida");
    }

    private void InicializaObjetos(){
        metodosPublicos = new MetodosPublicos();
        permissoesAcesso = new PermissoesAcesso();
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeToRefreshHistoricoPartidasJogador);
        partidaDataBase = new PartidaDataBase();
    }

    private void InicializaCampos(){
        txt_sem_registro = (TextView) findViewById(R.id.txt_sem_registro_historico_jogador);
    }

    //Aqui é instanciado o Recyclerview
    public void InicializaRecyclerView(){
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewHistoricoPartidasDoJogador = (RecyclerView) findViewById(R.id.recycler_historico_partidas_jogador);
        recyclerViewHistoricoPartidasDoJogador.setLayoutManager(mLayoutManager);
    }

    private void PopulaRecyclerViewPartidas(){
        adapterHistoricoDePartidasDoJogador = new AdapterHistoricoDePartidasDoJogador(this, listaDePartidasDoJogador, this, usuarioLogado);

        if(listaDePartidasDoJogador.size() <= 0){
            txt_sem_registro.setVisibility(View.VISIBLE);
        }

        recyclerViewHistoricoPartidasDoJogador.setAdapter(adapterHistoricoDePartidasDoJogador);
    }

    private void ConfiguraSwipeRefreshLayout(){
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(usuarioLogado != null){
                    if(usuarioLogado.getTipoDeUsuario() == 2) {
                        PreencheListaDePartidasJogador();
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void PreencheListaDePartidasJogador(){

        databaseReferencePartida = firebaseDatabase.getReference("partida");
        databaseReferencePartida.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaDePartidasDoJogador.clear();
                if(dataSnapshot.exists()){
                    Partida partida = dataSnapshot.getValue(Partida.class);

                    if(partida.getUsuarioDonoDaPartida() != null &&
                            partida.getUsuarioDonoDaPartida().getID().equals(usuarioLogado.getID()) &&
                            partida.getUsuarioDonoDaPartida().getTipoDeUsuario() == 2){

                        if(!listaDePartidasDoJogador.contains(partida)){
                            listaDePartidasDoJogador.add(partida);
                        }
                    }
                }

                for(DataSnapshot child: dataSnapshot.getChildren()){

                    if(child.exists()){
                        Partida partida1 = child.getValue(Partida.class);

                        if(partida1.getUsuarioDonoDaPartida() != null &&
                                partida1.getUsuarioDonoDaPartida().getID().equals(usuarioLogado.getID()) &&
                                partida1.getUsuarioDonoDaPartida().getTipoDeUsuario() == 2) {

                            if(!listaDePartidasDoJogador.contains(partida1)){
                                listaDePartidasDoJogador.add(partida1);
                            }
                        }
                    }
                }

                PopulaRecyclerViewPartidas();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        int itemId = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("usuarioLogado", usuarioLogado);
                startActivity(i);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    public void onCustomClick(Object object) {
        Partida partida = null;
        Usuario arbitro = null;
        String objeto = object.toString();

        if(objeto.contains("Model.Usuario")){
            arbitro = (Usuario) object;
        }else{
            partida = (Partida) object;
        }

        // QUANDO FOR USUARIO TIPO ARBITRO
        if(partida != null){
            //    Toast.makeText(MainActivity.this, "Voce Clicou na PARTIDA : " + partida.getUsuarioDonoDaPartida().getNome(), Toast.LENGTH_LONG).show();
            ///AbreCadastroConexaoArbitroSeOfereceParaPartida(partida);
        }

        //  QUANDO FOR USUARIO TIPO JOGADOR
        if(arbitro != null){
            //AbreCadastroConexaoUsuarioConvidaArbitro(arbitro);
        }
    }
}
