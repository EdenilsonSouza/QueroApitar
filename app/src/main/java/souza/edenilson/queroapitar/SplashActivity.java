package souza.edenilson.queroapitar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.Model.Usuario;
import souza.edenilson.Receiver.InternetReceiver;
import souza.edenilson.Services.AccessInternetService;

public class SplashActivity extends AppCompatActivity {

    MetodosPublicos metodosPublicos;

    private final int SPLASH_DISPLAY_LENGTH = 2100;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    InternetReceiver internetReceiver;
    Usuario usuario;
    String email,uid,senha,tipoDeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        App.setContext(this);
        metodosPublicos = new MetodosPublicos();

        // se esta conectado retorna TRUE
        metodosPublicos.TemInternet(App.getContext(), SplashActivity.this, LoginActivity.class);

        InicializaFirebase();

        if(mAuth != null)
            currentUser = mAuth.getCurrentUser();

        if(currentUser == null){

            Intent criarContaIntent = new Intent(SplashActivity.this,CriarContaActivity.class);
            startActivity(criarContaIntent);
            finish();

        }else{
            email = currentUser.getEmail();
            if(validaLogin(email)){
                uid = currentUser.getUid();
                usuario = new Usuario(uid, email);

                BuscaUsuario(usuario);
            }else{
                Intent criarContaIntent = new Intent(SplashActivity.this,CriarContaActivity.class);
                startActivity(criarContaIntent);
            }
        }

    }

    private boolean validaLogin(String email){

        if (email.equals(null) || email.equals("")) {
            return false;
        }

        if (!validaEmail(email)) {
            return false;
        }

        return true;
    }

    private boolean validaEmail(String email){

        boolean isEmailIdValid = false;
        if (email != null && email.length() > 0) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailIdValid = true;
            }
        }
        return isEmailIdValid;
    }

    private void BuscaUsuario(Usuario user){

        final String userID = user.getID();
        final String userEmail = user.getEmail();

        databaseReference =  firebaseDatabase.getReference("usuario");
        databaseReference.child(userID).addValueEventListener(new ValueEventListener() {
            String email = "";
            String senha ="";
            String nome = "";
            String sobrenome = "";
            int tipo;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String key = dataSnapshot.getKey(); // pega a chave do OBJETO Usuario
                if(dataSnapshot.exists()){
                    Usuario usuarioBanco = dataSnapshot.getValue(Usuario.class);
                    String mail = usuarioBanco.getEmail();
                    if(dataSnapshot.exists() && key.equals(userID)&& userEmail.equals(mail)){

                        email = dataSnapshot.getValue(Usuario.class).getEmail();
                        tipo = dataSnapshot.getValue(Usuario.class).getTipoDeUsuario();
                        senha = dataSnapshot.getValue(Usuario.class).getSenha();
                        nome = dataSnapshot.getValue(Usuario.class).getNome();
                        sobrenome = dataSnapshot.getValue(Usuario.class).getSobreNome();


                        usuarioBanco = dataSnapshot.getValue(Usuario.class);
                        usuarioBanco.setID(key);
                        usuarioBanco.setEmail(email);
                        usuarioBanco.setTipoDeUsuario(tipo);
                        usuarioBanco.setSenha(senha);
                        usuarioBanco.setNome(nome);
                        usuarioBanco.setSobreNome(sobrenome);

                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        intent.putExtra("usuarioLogado", usuarioBanco);
                        startActivity(intent);
                    }
                }else{
                    Intent intent = new Intent(SplashActivity.this, CriarContaActivity.class);
                    startActivity(intent);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SplashActivity.this, "Usuário não localizado. " + metodosPublicos.GetMensagemDeErro(databaseError.getMessage()), Toast.LENGTH_LONG).show();
                usuario = null;
            }
        });

        //return usuario;
    }



    private void InicializaFirebase(){
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("usuario");
    }

}
