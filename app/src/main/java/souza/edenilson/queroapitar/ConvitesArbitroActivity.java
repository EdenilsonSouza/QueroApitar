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
import android.widget.Button;
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
import java.util.List;

import souza.edenilson.Adapter.AdapterConvitesArbitro;
import souza.edenilson.DataBase.ConviteDataBase;
import souza.edenilson.DataBase.PartidaDataBase;
import souza.edenilson.DataBase.UsuarioDataBase;
import souza.edenilson.Interface.ConviteArbitroRecyclerView_Interface;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.MetodosPublicos.PermissoesAcesso;
import souza.edenilson.Model.Convite;
import souza.edenilson.Model.PartidaTemp;
import souza.edenilson.Model.Usuario;

public class ConvitesArbitroActivity extends AppCompatActivity implements ConviteArbitroRecyclerView_Interface {

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

    // RECYCLERVIEW
    private RecyclerView recyclerViewConvitesArbitros;
    private RecyclerView.LayoutManager mLayoutManager;
    AdapterConvitesArbitro adapterConvitesArbitro;
    private List<Convite> listaDeConvitesArbitro = new ArrayList<>();
    //  RelativeLayout viewRelativeLayoutBtnAvaliar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.activity_convites_arbitro);

        App.setContext(this);
        Toolbar toolbar = findViewById(R.id.toolbarConvitesArbitro);
        toolbar.setTitle(TITULO);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão

        metodosPublicos = new MetodosPublicos();
        metodosPublicos.TemInternet(App.getContext(),ConvitesArbitroActivity.this, LoginActivity.class );

        Intent intent = getIntent();
        usuarioLogado =  (Usuario)intent.getSerializableExtra("usuarioLogado");

        Convite novo_convite =  (Convite)getIntent().getSerializableExtra("convite");

        InicializaCampos();
        InicializaFirebase();
        InicializaObjetos();
        InicializaRecyclerView();
        ConfiguraSwipeRefreshLayout();

        PreencheListaDeConvitesUsuarioArbitro();
    }

    @Override
    protected void onStart() {
        metodosPublicos.TemInternet(App.getContext(), ConvitesArbitroActivity.this, LoginActivity.class );
        super.onStart();
    }

    @Override
    protected void onResume() {
        metodosPublicos.TemInternet(App.getContext(), ConvitesArbitroActivity.this, LoginActivity.class );
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
        metodosPublicos = new MetodosPublicos();
        permissoesAcesso = new PermissoesAcesso();
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeToRefreshHistoricoConvitesArbitro);
        partidaDataBase = new PartidaDataBase();
        conviteDataBase = new ConviteDataBase();
        usuarioDataBase = new UsuarioDataBase();
    }

    private void InicializaCampos(){
        txt_sem_registro = (TextView) findViewById(R.id.txt_sem_registro_arbitro);
    }

    //Aqui é instanciado o Recyclerview
    public void InicializaRecyclerView(){
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewConvitesArbitros = (RecyclerView) findViewById(R.id.recycler_historico_convites_arbitro);
        recyclerViewConvitesArbitros.setLayoutManager(mLayoutManager);
    }

    private void PopulaRecyclerViewConvites(){
        adapterConvitesArbitro = new AdapterConvitesArbitro(App.getContext(), listaDeConvitesArbitro, this, usuarioLogado, this );

        if(listaDeConvitesArbitro.size() <= 0){
            txt_sem_registro.setVisibility(View.VISIBLE);

        }

        recyclerViewConvitesArbitros.setAdapter(adapterConvitesArbitro);
    }

    private void PreencheListaDeConvitesUsuarioArbitro(){

        databaseReferencePartida = firebaseDatabase.getReference("convite");
        databaseReferencePartida.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaDeConvitesArbitro.clear();
                if(dataSnapshot.exists()){
                    Convite convite = dataSnapshot.getValue(Convite.class);

                    if(convite.getConvidado() != null /* && convite.getStatus() == 2  */ && convite.getConvidado().getID().equals(usuarioLogado.getID())){
                        if(!listaDeConvitesArbitro.contains(convite)){
                            listaDeConvitesArbitro.add(convite);
                        }
                    }
                }

                for(DataSnapshot child: dataSnapshot.getChildren()){

                    if(child.exists()){
                        Convite convite1 = child.getValue(Convite.class);

                        if(convite1.getConvidado() != null /* && convite1.getStatus() == 2 */ && convite1.getConvidado().getID().equals(usuarioLogado.getID())){
                            if(!listaDeConvitesArbitro.contains(convite1)){
                                listaDeConvitesArbitro.add(convite1);
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
                    if(usuarioLogado.getTipoDeUsuario() == 1) {
                        PreencheListaDeConvitesUsuarioArbitro();
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onCustomClick(Object object) {

        String objeto = object.toString();
        Convite convite = null;

        if(objeto.contains("Model.Convite")){
            convite = (Convite) object;
        }

        /*
        if(convite.isAvaliado() && !convite.getConvidado().getID().equals(usuarioLogado.getID())) {

            String nomeArbitro = convite.getConvidado().getNome().substring(0,1).toUpperCase() + convite.getConvidado().getNome().substring(1);
            String sobreNomeArbitro = convite.getConvidado().getSobreNome().substring(0,1).toUpperCase() + convite.getConvidado().getSobreNome().substring(1);
            Toast.makeText(ConvitesArbitroActivity.this, nomeArbitro + " " +sobreNomeArbitro + " já foi avaliada(o) por esta partida.", Toast.LENGTH_LONG).show();

        }*/
    }
}
