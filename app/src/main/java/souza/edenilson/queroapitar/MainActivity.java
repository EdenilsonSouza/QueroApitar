package souza.edenilson.queroapitar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import souza.edenilson.Adapter.AdapterPartidasArbitros;
import souza.edenilson.Adapter.AdapterPartidasJogador;
import souza.edenilson.Adapter.AdapterPartidasModal;
import souza.edenilson.DataBase.ConviteDataBase;
import souza.edenilson.DataBase.PartidaDataBase;
import souza.edenilson.Interface.ArbitrosRecyclerView_Interface;
import souza.edenilson.Interface.HistoricoPartidasJogadorRecyclerView_Interface;
import souza.edenilson.Interface.PartidaModalRecyclerViewModal;
import souza.edenilson.Interface.PartidaRecyclerView_Interface;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.MetodosPublicos.PermissoesAcesso;
import souza.edenilson.Model.Convite;
import souza.edenilson.Model.Endereco;
import souza.edenilson.Model.Esporte;
import souza.edenilson.Model.Partida;
import souza.edenilson.Model.PartidaTemp;
import souza.edenilson.Model.Usuario;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PartidaRecyclerView_Interface,
        ArbitrosRecyclerView_Interface,
        PartidaModalRecyclerViewModal {

    private static String TITULO_JOGADOR = "Lista de Partidas";
    private static String TITULO_ARBITRO = "Lista de Árbitros";


    FirebaseDatabase firebaseDatabase;
    //FirebaseFirestore firebaseFirestore;
    DatabaseReference databaseReferenceUsuario;
    DatabaseReference databaseReferencePartida;
    DatabaseReference databaseReferenceConvite;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    // OBJETOS
    Usuario usuario;
    Convite novo_convite;
    MetodosPublicos metodosPublicos;
    PermissoesAcesso permissoesAcesso;
    PartidaTemp partidaTemp;
    PartidaDataBase partidaDataBase;
    ConviteDataBase conviteDataBase;

    // RECYCLERVIEW
    private RecyclerView recyclerViewListaPartidasModal;
    private RecyclerView recyclerViewListas;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.LayoutManager mLayoutManagerModal;

    AdapterPartidasJogador adapterPartidasJogador;
    private List<Partida> listaDePartidas = new ArrayList<>();

    AdapterPartidasModal adapterPartidasModal;
    private List<Partida> listaDePartidasModal = new ArrayList<>();

    AdapterPartidasArbitros adapterPartidasArbitros;
    private List<Usuario> listaDeArbitros = new ArrayList<>();

    private List<Convite> convitesUsuarioLogado = new ArrayList<>();

    // ELEMENTOS DE TELA
    FloatingActionButton fab;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private AlertDialog alerta;
    private AlertDialog alertaSair;
    NavigationView navigationView;
    TextView txt_contador_convites;
    TextView txt_sem_registro;
    Button btn_minhas_partidas;
    Button btn_meus_convites;
    LinearLayout linear_sem_resgitros_main;

    boolean existeListaDePartidas = false;
    boolean existeListaDePartidasModal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        //Titulo da TELA
        App.setContext(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        metodosPublicos = new MetodosPublicos();
        metodosPublicos.TemInternet(App.getContext(), MainActivity.this, LoginActivity.class );

        // vem das telas de Login e Cadastro de Usuario
        Intent intent = getIntent();
        usuario =  (Usuario)intent.getSerializableExtra("usuarioLogado");

        novo_convite =  (Convite)getIntent().getSerializableExtra("convite");

        if(usuario == null){
            Intent i = new Intent(MainActivity.this, CriarContaActivity.class);
            startActivity(i);
        }

        InicializaObjetos();

        permissoesAcesso.PermissaoMapa(this);
        permissoesAcesso.PermissaoGaleria(this);


        InicializaCampos();
        InicializaFirebase();
        InicializaRecyclerView();
        ConfiguraSwipeRefreshLayout();
        ConsultaConvitesDoArbitro(usuario);
        ConsultaConvitesDoUsuarioLogado(usuario);

        // exibe o FAB de adicionar partida somente se o usuário for JOGADOR
        if(usuario != null && usuario.getTipoDeUsuario() == 2){  // ENTRA AQUI SE FOR JOGADOR
            this.setTitle(TITULO_ARBITRO);

            // mostra a lista de arbitros para o jogador
            PreencheListaDeArbitros();

            fab.bringToFront();
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(MainActivity.this, PartidaActivity.class);
                    intent.putExtra("usuarioLogado", usuario);
                    startActivity(intent);

                }
            });
        }else{
             // ENTRA AQUI SE FOR ARBITRO
            fab.hide();
            this.setTitle(TITULO_JOGADOR);

            //mostra a lista de partidas para o arbitro se candidatar
            PreencheListaDePartidas();
        }

        // CONFIGURA O REFRESH DA RECYCLERVIEW
        ConfiguraSwipeRecyclerView();


        // AQUI CRIA O MENU SANDUICHE
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // OCULTA O MENU CONFIGURACAO QUANDO FOR USUARIO JOGADOR
        OcultaItemMenu();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        permissoesAcesso.PermissaoGaleria(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        permissoesAcesso.PermissaoGaleria(this);
    }

    private void InicializaCampos(){
        btn_meus_convites = (Button) findViewById(R.id.btn_meus_convites);
        btn_minhas_partidas = (Button) findViewById(R.id.btn_minhas_partidas);
        txt_sem_registro = (TextView) findViewById(R.id.txt_sem_registro_main);

        ConfiguraBotoes();

        txt_contador_convites = (TextView) findViewById(R.id.txt_contador_convites);
    }

    private void ConfiguraBotoes(){

        btn_meus_convites.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    view.setTooltipText("Meus convites.");
                }

                // Toast.makeText(MainActivity.this, "Meus Convites.", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        btn_minhas_partidas.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    view.setTooltipText("Histórico de Partidas.");
                }

                return false;
            }
        });
    }

    private void ConsultaConvitesDoUsuarioLogado(Usuario arbitro){

        databaseReferenceConvite = firebaseDatabase.getReference("convite");
        databaseReferenceConvite.addValueEventListener(new ValueEventListener() {
            int contador_respostas_convites = 0;
            int contador_convites_arbitros = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    Convite convite =  dataSnapshot.getValue(Convite.class);
                   // convitesUsuarioLogado
                    if(convite.getConvidado() != null && convite.getConvidado().getID().equals(usuario.getID())){

                        convitesUsuarioLogado.add(convite);
                    }

                    if(convite.getRemetente() != null && convite.getRemetente().getID().equals(usuario.getID())){

                        convitesUsuarioLogado.add(convite);
                    }
                }

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    if(child.exists()){
                        Convite convite =  child.getValue(Convite.class);

                        if(convite.getConvidado() != null && convite.getConvidado().getID().equals(usuario.getID())){

                            convitesUsuarioLogado.add(convite);

                        }

                        if(convite.getRemetente() != null && convite.getRemetente().getID().equals(usuario.getID())){

                            convitesUsuarioLogado.add(convite);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void ConsultaConvitesDoArbitro(Usuario arbitro){

        databaseReferenceConvite = firebaseDatabase.getReference("convite");
        databaseReferenceConvite.addValueEventListener(new ValueEventListener() {
            int contador_respostas_convites = 0;
            int contador_convites_arbitros = 0;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    Convite convite =  dataSnapshot.getValue(Convite.class);

                    if(convite.getConvidado() != null && convite.getConvidado().getID() != null){
                        if(convite.getConvidado().getID().equals(usuario.getID()) &&  convite.getStatus() == 2 && !convite.isVisualizado()){  // se for aguardando
                            contador_convites_arbitros++;
                        }

                        if(convite.getRemetente().getID().equals(usuario.getID()) && convite.getStatus() == 1 && !convite.isVisualizado()
                                || convite.getRemetente().getID().equals(usuario.getID()) && convite.getStatus() == 3 && !convite.isVisualizado()){
                            contador_respostas_convites++;
                        }
                    }
                }

                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    if(child.exists()){
                        Convite convite =  child.getValue(Convite.class);

                        if(convite.getConvidado() != null && convite.getConvidado().getID() != null){

                            if(convite.getConvidado().getID().equals(usuario.getID()) &&  convite.getStatus() == 2 && !convite.isVisualizado()){  // se for aguardando
                                contador_convites_arbitros++;
                            }

                            if(convite.getRemetente().getID().equals(usuario.getID()) && convite.getStatus() == 1 && !convite.isVisualizado()
                               || convite.getRemetente().getID().equals(usuario.getID()) && convite.getStatus() == 3 && !convite.isVisualizado()){
                                contador_respostas_convites++;
                            }
                        }
                    }
                }

                if(contador_convites_arbitros > 0){
                    txt_contador_convites.setText(String.valueOf(contador_convites_arbitros));
                }

                if(contador_respostas_convites > 0){
                    txt_contador_convites.setText(String.valueOf(contador_respostas_convites));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void OcultaItemMenu(){
        Menu menu = navigationView.getMenu();

        // SE FOR JOGADOR OCULTA
        if(usuario != null && usuario.getTipoDeUsuario() == 2){
           // menu.findItem(R.id.nav_configuracao).setTitle(getString(R.string.menu_configuracao_sem_titulo));
            menu.findItem(R.id.nav_configuracao).setVisible(false);
        }
    }

    private void InicializaFirebase(){
        mAuth = FirebaseAuth.getInstance();
        // firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceUsuario = firebaseDatabase.getReference("usuario");
        databaseReferencePartida = firebaseDatabase.getReference("partida");
        databaseReferenceConvite = firebaseDatabase.getReference("convite");
    }

    private void InicializaObjetos(){
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeToRefresh);

        permissoesAcesso = new PermissoesAcesso();

        fab = findViewById(R.id.fabAddPartida);

        partidaTemp = new PartidaTemp();
        partidaDataBase = new PartidaDataBase();
        conviteDataBase = new ConviteDataBase();

        linear_sem_resgitros_main = (LinearLayout) findViewById(R.id.linear_sem_resgitros_main);
    }

    private void ConfiguraSwipeRefreshLayout(){
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

       mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {

               if(usuario != null){
                   if(usuario.getTipoDeUsuario() == 2) {   // se for jogador
                       PreencheListaDeArbitros();
                   }else if(usuario.getTipoDeUsuario() == 1){ // se for arbitro
                       PreencheListaDePartidas();
                   }
               }
               mSwipeRefreshLayout.setRefreshing(false);
           }
       });
    }

    private void PreencheListaDePartidasModal(){

        databaseReferencePartida = firebaseDatabase.getReference("partida");
        databaseReferencePartida.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaDePartidasModal.clear();
                if(dataSnapshot.exists()){
                    Partida partida = dataSnapshot.getValue(Partida.class);

                    if(partida != null){

                        if(partida.getDataDaPartida() != null){
                            Date dataAtual = new Date();
                            Date dataDaPartida = metodosPublicos.ConvertStringParaData(partida.getDataDaPartida());

                            int dataPassada = dataDaPartida.compareTo(dataAtual);

                            if(dataPassada > 0) {
                                // PRECISA ESTAR ATIVA E O CONVITE INICIADO = NAO PODE TER SIDO ACEITO AINDA.
                                if(partida.getUsuarioDonoDaPartida() != null && partida.getUsuarioDonoDaPartida().getID().equals(usuario.getID()) && partida.isStatus() && partida.getStatusConvite() < 1){
                                    if(!listaDePartidasModal.contains(partida)){
                                        listaDePartidasModal.add(partida);
                                    }
                                }
                            }
                        }
                    }
                }

                for(DataSnapshot child: dataSnapshot.getChildren()){

                    if(child.exists()){
                        Partida partida1 = child.getValue(Partida.class);

                        if(partida1 != null){

                            if(partida1.getDataDaPartida() != null){
                                Date dataAtual = new Date();
                                Date dataDaPartida = metodosPublicos.ConvertStringParaData(partida1.getDataDaPartida());

                                int dataPassada = dataDaPartida.compareTo(dataAtual);

                                if(dataPassada > 0) {
                                    if(partida1.getUsuarioDonoDaPartida() != null && partida1.getUsuarioDonoDaPartida().getID().equals(usuario.getID()) && partida1.isStatus() && partida1.getStatusConvite() < 1){
                                        if(!listaDePartidasModal.contains(partida1)){
                                            listaDePartidasModal.add(partida1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if(listaDePartidasModal.size() > 0){
                    existeListaDePartidasModal = true;
                }

                PopulaRecyclerViewPartidasModal(listaDePartidasModal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void PreencheListaDePartidas(){

        databaseReferencePartida = firebaseDatabase.getReference("partida");
        databaseReferencePartida.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaDePartidas.clear();
                if(dataSnapshot.exists()){
                    Partida partida = dataSnapshot.getValue(Partida.class);

                    // pega a distancia do usuario logado(arbitro) e a distancia do endereco da partida
                    int distancia = (int)Math.ceil(metodosPublicos.CalculaDistanciaDisponivel(usuario, partida.getEndereco(),"K"));

                    if(distancia <= usuario.getDistanciaDisponivel()){

                        if(partida.getUsuarioDonoDaPartida() != null && partida.getStatusConvite() <= 0){

                            if(!listaDePartidas.contains(partida)){
                                listaDePartidas.add(partida);
                            }
                        }
                    }
                }

                for(DataSnapshot child: dataSnapshot.getChildren()){

                    if(child.exists()){
                        Partida partida1 = child.getValue(Partida.class);

                        // pega a distancia do usuario logado(arbitro) e a distancia do endereco da partida
                        int distancia = (int)Math.ceil(metodosPublicos.CalculaDistanciaDisponivel(usuario, partida1.getEndereco(),"K"));

                        if(distancia <= usuario.getDistanciaDisponivel()) {

                            if(partida1.getUsuarioDonoDaPartida() != null && partida1.getStatusConvite() <= 0) {

                                if(!listaDePartidas.contains(partida1)){
                                    listaDePartidas.add(partida1);
                                }
                            }
                        }
                    }
                }

                if(listaDePartidas.size() > 0){
                    existeListaDePartidas = true;
                }

                PopulaRecyclerViewPartidas();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // mostra a lista de arbitros disponivel para o jogador;
    private void PreencheListaDeArbitros(){

        databaseReferenceUsuario = firebaseDatabase.getReference("usuario");
        databaseReferenceUsuario.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaDeArbitros.clear();
                if(dataSnapshot.exists()){

                    Usuario arbitro = dataSnapshot.getValue(Usuario.class);
                    //float distancia = 0;
                    int distancia = 0;

                    if(partidaTemp.getPartida() != null){
                        distancia = (int)Math.ceil(metodosPublicos.CalculaDistanciaDisponivel(usuario, partidaTemp.getPartida().getEndereco(),"K"));
                    }

                    if(arbitro.getID() != null && arbitro.getTipoDeUsuario() == 1){

                        // verifica se esta dentro da distancia escolida
                        if(distancia <= arbitro.getDistanciaDisponivel()){

                            if(!listaDeArbitros.contains(arbitro)){
                                listaDeArbitros.add(arbitro);
                            }
                        }
                    }
                }

                for(DataSnapshot child: dataSnapshot.getChildren()){

                    if(child.exists()){
                        Usuario arbitro1 = child.getValue(Usuario.class);
                        //float distancia = 0;
                        int distancia = 0;

                        if(partidaTemp.getPartida() != null){
                            distancia = (int)Math.ceil(metodosPublicos.CalculaDistanciaDisponivel(usuario, partidaTemp.getPartida().getEndereco(),"K"));
                        }

                        if(arbitro1 != null && arbitro1.getTipoDeUsuario() == 1){

                            // verifica se esta dentro da distancia escolida
                            if(distancia <= arbitro1.getDistanciaDisponivel()){

                                if(!listaDeArbitros.contains(arbitro1)){
                                    listaDeArbitros.add(arbitro1);
                                }
                            }
                        }
                    }
                }
                PopulaRecyclerViewArbitros();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //Aqui é instanciado o Recyclerview
    public void InicializaRecyclerView(){
        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewListas = (RecyclerView) findViewById(R.id.recycler_listas);
        recyclerViewListas.setLayoutManager(mLayoutManager);
    }

    private void InicializaRecyclerviewPartidasModal(View view){
        mLayoutManagerModal = new LinearLayoutManager(this);
        recyclerViewListaPartidasModal = (RecyclerView) view.findViewById(R.id.recycler_lista_partidas_modal);
        recyclerViewListaPartidasModal.setLayoutManager(mLayoutManagerModal);
    }

    private void PopulaRecyclerViewPartidasModal(List<Partida> _listaDePartidasModal){
        adapterPartidasModal = new AdapterPartidasModal(this, _listaDePartidasModal, this);
        recyclerViewListaPartidasModal.setAdapter(adapterPartidasModal);
    }

    private void PopulaRecyclerViewPartidas(){
        adapterPartidasJogador = new AdapterPartidasJogador(this, listaDePartidas, this);
        recyclerViewListas.setAdapter(adapterPartidasJogador);

        if(listaDePartidas.size() <= 0){
           // linear_sem_resgitros_main.setVisibility(View.VISIBLE);
            txt_sem_registro.setVisibility(View.VISIBLE);
        }else{
            txt_sem_registro.setVisibility(View.GONE);
        }
    }

    // preenche a lista de arbitros disponiveis.
    private void PopulaRecyclerViewArbitros(){
        adapterPartidasArbitros = new AdapterPartidasArbitros(this, listaDeArbitros, this);
        recyclerViewListas.setAdapter(adapterPartidasArbitros);

        if(listaDeArbitros.size() > 0){
           // txt_sem_registro.setText("Clique na linha para se candidar a partida.");
            txt_sem_registro.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        metodosPublicos.AbreModalSairAPP(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*
        if (id == R.id.minhas_partidas) {
            Intent intent = new Intent(MainActivity.this, MinhasPartidasActivity.class);
            intent.putExtra("usuarioLogado", usuario);
            startActivity(intent);
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_perfil) {

            Intent intent = new Intent(MainActivity.this, PerfilUsuarioActivity.class);
            intent.putExtra("usuarioLogado", usuario);
            startActivity(intent);

        }
       /* else if (id == R.id.nav_historico_partidas) {

            Intent intent = new Intent(MainActivity.this, MinhasPartidasActivity.class);
            intent.putExtra("usuarioLogado", usuario);
            startActivity(intent);

        } */
        else if (id == R.id.nav_configuracao) {

            Intent intent = new Intent(MainActivity.this, ConfiguracaoActivity.class);
            intent.putExtra("usuarioLogado", usuario);
            startActivity(intent);

        }
      /*  else if (id == R.id.nav_pagamento) {

        } */
        else if (id == R.id.nav_indicar) {

            startActivity(metodosPublicos.IndicarAmigos());

        } else if (id == R.id.nav_sobre) {

            Intent intent = new Intent(MainActivity.this, SobreActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_sair) {

            if(mAuth != null){
                mAuth.signOut();
                FirebaseAuth.getInstance().signOut();
            }
           // System.exit(0);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // AQUI FAZ O MOVIMENTO DE ARRASTAR O ITEM DA RECYCLERVIEW - DELETA E OU EDITA O ITEM
    private void ConfiguraSwipeRecyclerView() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();

                Object obj = recyclerViewListas.getAdapter();
                String objeto = obj.toString();

                if(objeto.contains("AdapterPartidasArbitros")){
                    adapterPartidasArbitros = (AdapterPartidasArbitros) recyclerViewListas.getAdapter();
                    adapterPartidasArbitros.remove(swipedPosition); //  AQUI REMOVE SOMENTE DA LISTA EM TEMPO DE EXEXUÇÃO
                }else if(objeto.contains("AdapterPatidas")){
                    adapterPartidasJogador = (AdapterPartidasJogador) recyclerViewListas.getAdapter();
                    adapterPartidasJogador.remove(swipedPosition); // AQUI REMOVE DA LISTA E TAMBEM REMOVE O OBJETO DO BANCO DE DADOS
                }

                recyclerViewListas.refreshDrawableState();
            }

        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewListas);
    }


    // AQUI PEGA O CLICK DO ITEM DA RECYCLERVIEW
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

            // Aqui verifica se a data ja passou
            Date dataAtual = new Date();
            Date dataDaPartida = metodosPublicos.ConvertStringParaData(partida.getDataDaPartida());
            int dataPassada = dataDaPartida.compareTo(dataAtual);

            if(dataPassada < 0) {
                //ja passou a data da partida
                partida.setStatusConvite(7);
                partidaDataBase.Atualiza(partida);
            }else{
                AbreModalArbitroSeOfereceParaPartida(partida);
            }

        }

        //  QUANDO FOR USUARIO TIPO JOGADOR
        if(arbitro != null){
            AbreCadastroConexaoUsuarioConvidaArbitro(arbitro);
        }

    }

    // MODAL DE CONFIRMAÇÃO DO CONVITE
    private void AbreModalArbitroSeOfereceParaPartida(Partida partida){
        permissoesAcesso.PermissaoGaleria(this);
        LayoutInflater li = getLayoutInflater();

        //inflamos o layout conecta_arbitro_partida.xml na view
        View view = li.inflate(R.layout.conecta_arbitro_partida, null);
        //definimos para o botão do layout um clickListener

        view.findViewById(R.id.btn_pedido).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // status 6 - arbitro se oferece
                partida.setStatusConvite(6);
                partida.setArbitro(usuario);
                partidaDataBase.Atualiza(partida);

                // CRIA O CONVITE PARA APITAR O JOGO
                String Id =  partida.getID();
                Convite convite = new Convite(Id,
                        metodosPublicos.GetData(new Date()),
                        partida.getUsuarioDonoDaPartida(),
                        partida.getArbitro(),
                        partida, 6, false, false);

                conviteDataBase.Salvar(usuario, convite, MainActivity.this, "arbitroSeOferece");

                Toast.makeText(MainActivity.this, "Pedido enviado, logo receberá a resposta.", Toast.LENGTH_LONG).show();
                //desfaz o alerta.
                alerta.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String dataDaPartida = metodosPublicos.FormataData(partida.getDataDaPartida());
        builder.setTitle("Quer apitar a partida de " + partida.getEsporte().getNome() + ", no dia " + dataDaPartida + " " + partida.getHoraDaPartida() + " ?");
        builder.setView(view);
        alerta = builder.create();
        alerta.show();

    }

    // AQUI VAI SER CARREGADO OS DADOS DO USUARIO ARBITRO SELECIONADO - Abre MODAL Convite Arbitro
    private void AbreCadastroConexaoUsuarioConvidaArbitro(Usuario arbitroSelecionado){
        permissoesAcesso.PermissaoGaleria(this);
        LayoutInflater li = getLayoutInflater();

        //inflamos o layout conecta_partida_arbitro.xml na view
         View view = li.inflate(R.layout.conecta_partida_arbitro, null);

        // INICIALIZA A RECYCLERVIEW QUANDO ABRE A MODAL
        InicializaRecyclerviewPartidasModal(view);
        PreencheListaDePartidasModal();

        // INICIA OS CAMPOS QUANDO ABRE A MODAL

        ImageView foto_arbitro = (ImageView) view.findViewById(R.id.foto_arbitro);
        TextView nome_arbitro = (TextView) view.findViewById(R.id.txt_nome_arbitro_modal);
        TextView avaliacao_arbitro = (TextView) view.findViewById(R.id.txt_avaliacao_arbitro_modal);
        TextView rating1_avaliacao = (TextView) view.findViewById(R.id.rating1_avaliacao_modal);
        TextView rating2_avaliacao = (TextView) view.findViewById(R.id.rating2_avaliacao_modal);
        TextView rating3_avaliacao = (TextView) view.findViewById(R.id.rating3_avaliacao_modal);
        TextView rating4_avaliacao = (TextView) view.findViewById(R.id.rating4_avaliacao_modal);
        TextView rating5_avaliacao = (TextView) view.findViewById(R.id.rating5_avaliacao_modal);
        TextView idade_arbitro = (TextView) view.findViewById(R.id.txt_idade_arbitro_modal);
        TextView numero_partidas = (TextView) view.findViewById(R.id.txt_numero_partidas_arbitro_modal);

        TextView primeiroEsporte = (TextView) view.findViewById(R.id.txt_primeiro_esporte_arbitro_modal);
        TextView primeiro_valor_cobrado = (TextView) view.findViewById(R.id.txt_primeiro_valor_arbitro_modal);

        TextView segundoEsporte = (TextView) view.findViewById(R.id.txt_segundo_esporte_arbitro_modal);
        TextView segundo_valor_cobrado = (TextView) view.findViewById(R.id.txt_segundo_valor_arbitro_modal);

        TextView terceiroEsporte = (TextView) view.findViewById(R.id.txt_terceiro_esporte_arbitro_modal);
        TextView terceiro_valor_cobrado = (TextView) view.findViewById(R.id.txt_terceiro_valor_arbitro_modal);

        LinearLayout linear_segundo_esporte_modal = (LinearLayout) view.findViewById(R.id.linear_segundo_esporte_modal);
        LinearLayout linear_terceiro_esporte_modal = (LinearLayout) view.findViewById(R.id.linear_terceiro_esporte_modal);

        if(arbitroSelecionado != null){

            if(arbitroSelecionado.getListaDeEsportes() != null) {

                for (Esporte user_esporte : arbitroSelecionado.getListaDeEsportes()) {

                    if (user_esporte != null && user_esporte.getIdentificadorEsporte() != null && user_esporte.getIdentificadorEsporte().equals("primeiroEsporte")) {

                        primeiroEsporte.setText(user_esporte.getNome());
                        primeiro_valor_cobrado.setText("Valor: " + user_esporte.getValor());
                    }

                    if (user_esporte != null && user_esporte.getIdentificadorEsporte() != null && user_esporte.getIdentificadorEsporte().equals("segundoEsporte")) {

                        linear_segundo_esporte_modal.setVisibility(View.VISIBLE);
                        segundoEsporte.setText(user_esporte.getNome());
                        segundo_valor_cobrado.setText("Valor: " + user_esporte.getValor());

                    }

                    if (user_esporte != null && user_esporte.getIdentificadorEsporte() != null && user_esporte.getIdentificadorEsporte().equals("terceiroEsporte")) {

                        linear_terceiro_esporte_modal.setVisibility(View.VISIBLE);
                        terceiroEsporte.setText(user_esporte.getNome());
                        terceiro_valor_cobrado.setText("Valor: " + user_esporte.getValor());

                    }
                }
            }

            // PREENCHE OS VALORES QUANDO ABRE A MODAL
            metodosPublicos.CarregaImageView(foto_arbitro,arbitroSelecionado.getUrlFoto());

            String nome = arbitroSelecionado.getNome().substring(0,1).toUpperCase() + arbitroSelecionado.getNome().substring(1);
            String sobrenome =  arbitroSelecionado.getSobreNome().substring(0,1).toUpperCase() + arbitroSelecionado.getSobreNome().substring(1);

            nome_arbitro.setText(nome + " " + sobrenome);
            double avaliacao = 0.0;
            if(arbitroSelecionado.getAvaliacaoGeral() > 5.0){
                avaliacao = 5.0;
            }else{
                avaliacao = arbitroSelecionado.getAvaliacaoGeral();
            }
            avaliacao_arbitro.setText(String.valueOf(avaliacao));  // PEGA O VALOR DA AVALIAÇÃO GERAL DO ARBITRO

            String dataNascimento = arbitroSelecionado.getDataDeNascimento();
            idade_arbitro.setText(String.valueOf(metodosPublicos.CalculaIdade(dataNascimento)) + " anos");
            numero_partidas.setText("Nº de partidas: " + arbitroSelecionado.getQuantidadePartidas());

            if(arbitroSelecionado.getAvaliacaoGeral() == 1){

                rating1_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating2_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_vazia_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating3_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_vazia_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating4_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_vazia_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating5_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_vazia_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

            }else if(arbitroSelecionado.getAvaliacaoGeral() == 2){

                rating1_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating2_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating3_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_vazia_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating4_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_vazia_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating5_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_vazia_24px, //left
                        0, //top
                        0, //right
                        0);//bottom
            }else if(arbitroSelecionado.getAvaliacaoGeral() == 3){

                rating1_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating2_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating3_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating4_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_vazia_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating5_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_vazia_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

            }else if(arbitroSelecionado.getAvaliacaoGeral() == 4){

                rating1_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating2_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating3_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating4_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating5_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_vazia_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

            }else if(arbitroSelecionado.getAvaliacaoGeral() >= 5){

                rating1_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating2_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating3_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating4_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

                rating5_avaliacao.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom

            }
        }

        view.findViewById(R.id.btn_convida_arbitro).setOnClickListener(new View.OnClickListener() {

            int count = listaDePartidasModal.size();

            // foto do usuario
            String nome_arbitro_selecionado = arbitroSelecionado.getNome();

            TextView id_partida_selecionada = (TextView) view.findViewById(R.id.txt_id_partida);
            TextView nome_arbitro = (TextView) view.findViewById(R.id.txt_nome_arbitro_modal);
            TextView avaliacao_arbitro = (TextView) view.findViewById(R.id.txt_avaliacao_arbitro_modal);
            TextView idade_arbitro = (TextView) view.findViewById(R.id.txt_idade_arbitro_modal);

            TextView primeiroEsporte = (TextView) view.findViewById(R.id.txt_primeiro_esporte_arbitro_modal);
            TextView primeiro_valor_cobrado = (TextView) view.findViewById(R.id.txt_primeiro_valor_arbitro_modal);

            TextView segundoEsporte = (TextView) view.findViewById(R.id.txt_segundo_esporte_arbitro_modal);
            TextView segundo_valor_cobrado = (TextView) view.findViewById(R.id.txt_segundo_valor_arbitro_modal);

            TextView terceiroEsporte = (TextView) view.findViewById(R.id.txt_terceiro_esporte_arbitro_modal);
            TextView terceiro_valor_cobrado = (TextView) view.findViewById(R.id.txt_terceiro_valor_arbitro_modal);

            TextView data_partida = (TextView) view.findViewById(R.id.txt_data_da_partida_modal);
            TextView endereco = (TextView) view.findViewById(R.id.txt_endereco_partida_modal);

            // AQUI PEGA OS VALORES DA MODAL QUANDO CLICAR NO BOTAO CONVIDAR
            public void onClick(View view_user) {

                if(partidaTemp.getPartida() != null){
                    partidaTemp.setArbitro(arbitroSelecionado); // AQUI ADICIONA O ARBITRO NA PARTIDA

                    Partida savePartidaTemp = partidaTemp.getPartida();
                    savePartidaTemp.setArbitro(arbitroSelecionado);

                    // ALTERA O STATUS DO CONVITE PARA AGUARDANDO
                    savePartidaTemp.setStatusConvite(2);

                    // aqui adiciona o ARBITRO na partida = POREM AINDA ESPERA A CONFIRMACAO DO ARBITRO
                    partidaDataBase.Atualiza(savePartidaTemp);

                    // CRIA O CONVITE PARA APITAR O JOGO
                    String Id =  savePartidaTemp.getID();
                    Convite convite = new Convite(Id, metodosPublicos.GetData(new Date()),
                                                  savePartidaTemp.getUsuarioDonoDaPartida(),
                                                  savePartidaTemp.getArbitro(),
                                                  savePartidaTemp, 2, false,false);

                    conviteDataBase.Salvar(usuario, convite, MainActivity.this, "jogadorConvidaArbitro");

                }else{
                    Toast.makeText(MainActivity.this, "Não há nenhuma Partida selecionada.", Toast.LENGTH_LONG).show();
                }

                //desfaz o alerta.
                alerta.dismiss();
            }


        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Convite");
        builder.setView(view);
        alerta = builder.create();
        alerta.setCancelable(true);
        alerta.show();

    }


    @Override
    public void onCustomClickModal(Object object, boolean partidachecada, int idPartidaSelecionada) {

        Partida partida = null;
        partida = (Partida) object;

        if (partida != null) {
            partidaTemp.setPartida(partida);
        }
    }


    public void AbreModalRemoverArbitro(Context contexto, String tipoObjeto, int swipedPosition){

        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);

        builder.setMessage("Deseja sair do APP?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                if(tipoObjeto.contains("AdapterPartidasArbitros")){
                    adapterPartidasArbitros = (AdapterPartidasArbitros) recyclerViewListas.getAdapter();
                    adapterPartidasArbitros.remove(swipedPosition); //  AQUI REMOVE SOMENTE DA LISTA EM TEMPO DE EXEXUÇÃO
                }else if(tipoObjeto.contains("AdapterPatidas")){
                    adapterPartidasJogador = (AdapterPartidasJogador) recyclerViewListas.getAdapter();
                    adapterPartidasJogador.remove(swipedPosition); // AQUI REMOVE DA LISTA E TAMBEM REMOVE O OBJETO DO BANCO DE DADOS
                }

            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                recyclerViewListas.refreshDrawableState();
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void AbreMinhasPartidas_click(View view) {

       if(usuario.getTipoDeUsuario() == 1){ // SE FOR ARBITRO
           Intent intent = new Intent(MainActivity.this, HistoricoPartidasArbitroActivity.class);
           intent.putExtra("usuarioLogado", usuario);
           startActivity(intent);
       }else if(usuario.getTipoDeUsuario() == 2){
           Intent intent = new Intent(MainActivity.this, HistoricoPartidasJogadorActivity.class);
           intent.putExtra("usuarioLogado", usuario);
           startActivity(intent);
       }

    }

    public void AbreMeusConvites_click(View view) {
        txt_contador_convites.setText("");

        if(convitesUsuarioLogado.size() > 0){

            for (Convite con: convitesUsuarioLogado) {

               if(!con.isVisualizado()){
                   con.setVisualizado(true);
                   conviteDataBase.Atualiza(con,usuario, this, "visualizado");
               }
            }
        }

        if(usuario.getTipoDeUsuario() == 1){ // SE FOR ARBITRO
            Intent intent = new Intent(MainActivity.this, ConvitesArbitroActivity.class);
            intent.putExtra("usuarioLogado", usuario);
            startActivity(intent);
        }else{
            Intent intent = new Intent(MainActivity.this, ConvitesJogadorActivity.class);
            intent.putExtra("usuarioLogado", usuario);
            startActivity(intent);
        }


    }
}

