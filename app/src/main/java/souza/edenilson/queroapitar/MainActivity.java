package souza.edenilson.queroapitar;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.Model.Usuario;
import souza.edenilson.Services.AccessInternetService;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TITULO = "Principal";

    FirebaseDatabase firebaseDatabase;
    //FirebaseFirestore firebaseFirestore;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    Usuario usuario;
    Usuario usuarioLogado;
    MetodosPublicos metodosPublicos;

    Button btnCriarPartida;
    Button btnCriarUsuario;
    TextView txtNomeUsuario;
    TextView txtSenhaUsuario;
    TextView txtTipoUsuario;
    TextView txtChaveUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        //Titulo da TELA
        App.setContext(this);
        this.setTitle(TITULO);

        verificaSinalInternet();
        InicializaFirebase();
        InicializaCampos();

        metodosPublicos = new MetodosPublicos();

        // vem das telas de Login e Cadastro de Usuario
        Intent intent = getIntent();
        usuario =  (Usuario)intent.getSerializableExtra("usuarioLogado");

         if(usuario == null){
            Intent i = new Intent(MainActivity.this, CriarContaActivity.class);
            startActivity(i);
        }

        if(usuario != null){
            txtNomeUsuario.setText(usuario.getEmail());
        }else{
            Intent i = new Intent(MainActivity.this, CriarContaActivity.class);
            startActivity(i);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fabAddPartida);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            Intent intent = new Intent(MainActivity.this, PartidaActivity.class);
            startActivity(intent);

            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        btnCriarPartida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // CriarPartida();
            }
        });

    }
    private void InicializaFirebase(){
        mAuth = FirebaseAuth.getInstance();
        // firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("usuario");
    }

    private void InicializaCampos(){
        btnCriarPartida = (Button) findViewById(R.id.btnCriarPartida);
        btnCriarUsuario = (Button) findViewById(R.id.btnCriarUsuario);
        txtNomeUsuario = (TextView) findViewById(R.id.txtNomeUsuarioLogado);

         txtSenhaUsuario = (TextView) findViewById(R.id.txtSenhaUsuarioLogado);;
         txtTipoUsuario = (TextView) findViewById(R.id.txtTipoUsuarioLogado);;
         txtChaveUsuario = (TextView) findViewById(R.id.txtChaveUsuarioLogado);;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

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

        } else if (id == R.id.nav_historico_arbitros) {

        } else if (id == R.id.nav_historico_partidas) {

        } else if (id == R.id.nav_configuracao) {

            Intent intent = new Intent(MainActivity.this, ConfiguracaoActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_pagamento) {

        } else if (id == R.id.nav_indicar) {

            startActivity(metodosPublicos.IndicarAmigos());

        } else if (id == R.id.nav_sobre) {

            Intent intent = new Intent(MainActivity.this, SobreActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_sair) {

            if(mAuth != null){
                mAuth.signOut();
                FirebaseAuth.getInstance().signOut();
            }
            finish();
           // System.exit(0);
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    void verificaSinalInternet(){
        // chama o service que verifica se há Conexão com a Internet
       App.getContext().startService(new Intent(App.getContext(), AccessInternetService.class));
    }


}
