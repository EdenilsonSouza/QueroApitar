package souza.edenilson.queroapitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import souza.edenilson.Adapter.AdapterConvitesJogador;
import souza.edenilson.DataBase.ConviteDataBase;
import souza.edenilson.DataBase.PartidaDataBase;
import souza.edenilson.DataBase.UsuarioDataBase;
import souza.edenilson.Interface.ConviteJogadorRecyclerView_Interface;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.MetodosPublicos.PermissoesAcesso;
import souza.edenilson.Model.Convite;
import souza.edenilson.Model.Partida;
import souza.edenilson.Model.PartidaTemp;
import souza.edenilson.Model.Usuario;

public class ConvitesJogadorActivity extends AppCompatActivity implements ConviteJogadorRecyclerView_Interface {

    private static String TITULO = "Histórico de Convites";

    // FIREBASE
    FirebaseDatabase firebaseDatabase;
    //FirebaseFirestore firebaseFirestore;
    DatabaseReference databaseReferenceConvite;
    DatabaseReference databaseReferencePartida;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    // OBJETOS
    Usuario usuarioLogado;
    Convite convite = null;
    MetodosPublicos metodosPublicos;
    PermissoesAcesso permissoesAcesso;
    PartidaTemp partidaTemp;
    PartidaDataBase partidaDataBase;
    ConviteDataBase conviteDataBase;
    UsuarioDataBase usuarioDataBase;
    private AlertDialog alerta;

    // COMPONENTES DE TELA
    SwipeRefreshLayout mSwipeRefreshLayout;
    TextView txt_sem_registro;
    TextView txt_texto_avaliar;

    // RECYCLERVIEW
    private RecyclerView recyclerViewConvitesJogador;
    private RecyclerView.LayoutManager mLayoutManager;
    AdapterConvitesJogador adapterConvitesJogador;
    private List<Convite> listaDeConvitesJogador = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.activity_convites_jogador);

        App.setContext(this);
        Toolbar toolbar = findViewById(R.id.toolbarConvitesJogador);
        toolbar.setTitle(TITULO);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão

        metodosPublicos = new MetodosPublicos();
        metodosPublicos.TemInternet(App.getContext(),ConvitesJogadorActivity.this, LoginActivity.class );

        Intent intent = getIntent();
        usuarioLogado =  (Usuario)intent.getSerializableExtra("usuarioLogado");

        Convite novo_convite =  (Convite)getIntent().getSerializableExtra("convite");

        InicializaCampos();
        InicializaFirebase();
        InicializaObjetos();
        InicializaRecyclerView();
        ConfiguraSwipeRefreshLayout();

        PreencheListaDeConvitesUsuarioJogador();
    }

    @Override
    protected void onStart() {
        metodosPublicos.TemInternet(App.getContext(), ConvitesJogadorActivity.this, LoginActivity.class );
        super.onStart();
    }

    @Override
    protected void onResume() {
        metodosPublicos.TemInternet(App.getContext(), ConvitesJogadorActivity.this, LoginActivity.class );
        super.onResume();
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

    private void InicializaFirebase(){
        mAuth = FirebaseAuth.getInstance();
        // firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceConvite = firebaseDatabase.getReference("convite");
        databaseReferencePartida = firebaseDatabase.getReference("partida");
    }

    private void InicializaObjetos(){
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeToRefreshHistoricoConvitesJogador);
        metodosPublicos = new MetodosPublicos();
        permissoesAcesso = new PermissoesAcesso();
        partidaDataBase = new PartidaDataBase();
        conviteDataBase = new ConviteDataBase();
        usuarioDataBase = new UsuarioDataBase();
    }

    private void InicializaCampos(){
        txt_sem_registro = (TextView) findViewById(R.id.txt_sem_registro_jogador);
        txt_texto_avaliar = (TextView) findViewById(R.id.txt_texto_avaliar_jogador);
    }

    //Aqui é instanciado o Recyclerview
    public void InicializaRecyclerView(){
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewConvitesJogador = (RecyclerView) findViewById(R.id.recycler_historico_convites_Jogador);
        recyclerViewConvitesJogador.setLayoutManager(mLayoutManager);
    }

    private void PopulaRecyclerViewConvites(){
        adapterConvitesJogador = new AdapterConvitesJogador(App.getContext(), listaDeConvitesJogador, this, usuarioLogado, this);
        recyclerViewConvitesJogador.setAdapter(adapterConvitesJogador);

        if(listaDeConvitesJogador.size() > 0){
            txt_texto_avaliar.setVisibility(View.VISIBLE);
        }

        if(listaDeConvitesJogador.size() <= 0){
            txt_sem_registro.setVisibility(View.VISIBLE);
        }
    }

    private void PreencheListaDeConvitesUsuarioJogador(){

        databaseReferencePartida = firebaseDatabase.getReference("convite");
        databaseReferencePartida.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaDeConvitesJogador.clear();
                if(dataSnapshot.exists()){
                    Convite convite = dataSnapshot.getValue(Convite.class);

                    if(convite.getRemetente() != null && convite.getRemetente().getID().equals(usuarioLogado.getID())) {
                        if(!listaDeConvitesJogador.contains(convite)){
                            listaDeConvitesJogador.add(convite);
                        }
                    }
                }

                for(DataSnapshot child: dataSnapshot.getChildren()){

                    if(child.exists()){
                        Convite convite1 = child.getValue(Convite.class);

                        if(convite1.getRemetente() != null && convite1.getRemetente().getID().equals(usuarioLogado.getID())) {
                            if(!listaDeConvitesJogador.contains(convite1)){
                                listaDeConvitesJogador.add(convite1);
                            }
                        }
                    }
                }
                PopulaRecyclerViewConvites();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ConfiguraSwipeRefreshLayout(){
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(usuarioLogado != null){

                 // PreencheListaDeConvitesUsuarioJogador();

                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    // MODAL DE AVALIAÇAO DO ARBITRO
    private void AbreModalAvaliacaoArbitro(Convite convite, Activity activity){
        permissoesAcesso.PermissaoGaleria(this);
        LayoutInflater li = getLayoutInflater();

        //inflamos o layout conecta_arbitro_partida.xml na view
        View view = li.inflate(R.layout.avaliacao_arbitro, null);

        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

        view.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Usuario arbitro = convite.getConvidado();
                double avaliacao = ratingBar.getRating() + arbitro.getAvaliacaoGeral();

                if(avaliacao > 5.0) avaliacao = 5.0;
                arbitro.setAvaliacaoGeral(avaliacao);
                usuarioDataBase.Atualizar(arbitro);

                Partida p = convite.getPartida();
                p.setStatusConvite(7);
                partidaDataBase.Atualiza(p);

                convite.setStatus(7);
                convite.getPartida().setStatusConvite(7);
                convite.setAvaliado(true);
                convite.getConvidado().setAvaliacaoGeral(avaliacao);
                convite.getPartida().setAvaliacaoArbitro((int)ratingBar.getRating());
                conviteDataBase.Atualiza(convite,usuarioLogado, activity,"arbitroRecebeAvaliacao");

                Toast.makeText(ConvitesJogadorActivity.this, "Avaliação realizada com sucesso." , Toast.LENGTH_LONG).show();
                alerta.dismiss();

            }
        });

        if(convite.getConvidado() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            String nomeArbitro = convite.getConvidado().getNome().substring(0,1).toUpperCase() + convite.getConvidado().getNome().substring(1);
            String sobreNomeArbitro = convite.getConvidado().getSobreNome().substring(0,1).toUpperCase() + convite.getConvidado().getSobreNome().substring(1);

            builder.setTitle("Avalie o árbitro " + nomeArbitro + " " + sobreNomeArbitro);
            builder.setView(view);
            alerta = builder.create();
            alerta.show();
        }else{
            Toast.makeText(this, "Partida realizada sem árbitro.", Toast.LENGTH_LONG).show();
        }



    }


    @Override
    public void onCustomClick(Object object) {
        String objeto = object.toString();

        if(objeto.contains("Model.Convite")){
            convite = (Convite) object;
        }

        if(convite.getConvidado() != null && convite.isAvaliado()) {

            String nomeArbitro = convite.getConvidado().getNome().substring(0,1).toUpperCase() + convite.getConvidado().getNome().substring(1);
            String sobreNomeArbitro = convite.getConvidado().getSobreNome().substring(0,1).toUpperCase() + convite.getConvidado().getSobreNome().substring(1);
            Toast.makeText(ConvitesJogadorActivity.this, nomeArbitro + " " + sobreNomeArbitro + " já foi avaliada(o) por esta partida.", Toast.LENGTH_LONG).show();

        }else{

            AtomicReference<Date> dataAtual = new AtomicReference<>(new Date());
            Date dataDaPartida = metodosPublicos.ConvertStringParaData(convite.getPartida().getDataDaPartida());

            int dataPartidaMenor = dataDaPartida.compareTo(dataAtual.get());

            if(usuarioLogado.getTipoDeUsuario() == 2 && dataPartidaMenor < 0 ){
                AbreModalAvaliacaoArbitro(convite, this);
            }else{
                String nomeArbitro = convite.getConvidado().getNome().substring(0,1).toUpperCase() + convite.getConvidado().getNome().substring(1);
                String sobreNomeArbitro = convite.getConvidado().getSobreNome().substring(0,1).toUpperCase() + convite.getConvidado().getSobreNome().substring(1);
                Toast.makeText(ConvitesJogadorActivity.this, "Aguarde finalizar a partida para avaliar o Árbitro.", Toast.LENGTH_LONG).show();
            }

        }
    }

    /*
    public void AceitarPedidoArbitro_click(View view) {

        Toast.makeText(ConvitesJogadorActivity.this, "VC CLICOU NO ACEITAR ARBITRO " + convite.getConvidado().getNome(), Toast.LENGTH_LONG).show();
    }
    */

}
