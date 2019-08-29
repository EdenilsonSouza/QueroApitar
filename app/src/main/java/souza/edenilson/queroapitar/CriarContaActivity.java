package souza.edenilson.queroapitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.Model.Usuario;
import souza.edenilson.Receiver.InternetReceiver;
import souza.edenilson.Services.AccessInternetService;

public class CriarContaActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser currentUser;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    Usuario usuario;
    InternetReceiver internetReceiver;

    private EditText editEmail;
    private EditText editNome;
    private EditText editSobrenome;
    private EditText editSenha;


    private Button btnRegistrarUsuario;
    private RadioGroup radioGroupTipoUsuario;
    private RadioButton radioArbitro, radioJogador;

    MetodosPublicos metodosPublicos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta);

        App.setContext(this);

        verificaSinalInternetLoad();
        InicializaFirebase();
        InicializaCampos();
        ConfiguraRadioButton();
        metodosPublicos = new MetodosPublicos();
    }

    private void ConfiguraRadioButton(){

        radioArbitro.setChecked(false);
        radioJogador.setChecked(false);

        radioGroupTipoUsuario.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioArbitro) {
                    radioArbitro.setChecked(true);
                  /*  Toast.makeText(getApplicationContext(), "Árbitro Selecionado",
                            Toast.LENGTH_SHORT).show();*/
                } else if(checkedId == R.id.radioJogador) {
                    radioJogador.setChecked(true);
                   /* Toast.makeText(getApplicationContext(), "Jogador Selecionado",
                            Toast.LENGTH_SHORT).show();*/
                }
            }

        });
    }

    private void InicializaCampos(){
        editEmail = (EditText) findViewById(R.id.editEmail);
        editNome = (EditText) findViewById(R.id.editNome);
        editSobrenome = (EditText) findViewById(R.id.editSobrenome);
        editSenha = (EditText) findViewById(R.id.editSenha);
        btnRegistrarUsuario = (Button) findViewById(R.id.btnRegistrar);

        radioGroupTipoUsuario = (RadioGroup) findViewById(R.id.radioGroupTipoUsuario);
        radioArbitro = (RadioButton) findViewById(R.id.radioArbitro);
        radioJogador = (RadioButton) findViewById(R.id.radioJogador);
    }


    private void InicializaFirebase(){
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("usuario");
    }

    private void LimparCampos(){
        editEmail.setText("");
        editSenha.setText("");
        editNome.setText("");
        editSobrenome.setText("");
        radioJogador.setChecked(false);
        radioArbitro.setChecked(false);
    }

    public void AbrirTelaLogin(View view) {
        Intent intent = new Intent(CriarContaActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void CriarContaClick(View view) {
        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();
        String nome = editNome.getText().toString();
        String sobrenome = editSobrenome.getText().toString();
        int tipoUsuarioSelecionado = GetValueRadioButtonTipoUsuarioSelecionado();

        if(validaCampos(email,nome,sobrenome, senha, tipoUsuarioSelecionado)){
            CriarConta();
        }else{
            Toast.makeText(this, "Não foi possível criar sua conta.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validaCampos(String email, String nome,String sobrenome,String senha, int tipoUsuarioSelecionado){

        if (email.equals(null) || email.equals("")) {
            Toast.makeText(this, "Campo e-mail é obrigatório.", Toast.LENGTH_SHORT).show();
            editEmail.requestFocus();
            return false;
        }

        if (!validaEmail(email)) {
            Toast.makeText(this, "Campo e-mail incorreto.", Toast.LENGTH_SHORT).show();
            editEmail.requestFocus();
            return false;
        }

        if (nome.equals(null) || nome.equals("")) {
            Toast.makeText(this, "Campo nome é obrigatório.", Toast.LENGTH_SHORT).show();
            editNome.requestFocus();
            return false;
        }

        if (sobrenome.equals(null) || sobrenome.equals("")) {
            Toast.makeText(this, "Campo sobrenome é obrigatório.", Toast.LENGTH_SHORT).show();
            editSobrenome.requestFocus();
            return false;
        }

        if (senha.equals(null) || senha.equals("")) {
            Toast.makeText(this, "Campo senha é obrigatório.", Toast.LENGTH_SHORT).show();
            editSenha.requestFocus();
            return false;
        }

        if (senha.length() < 6) {
            Toast.makeText(this, "Campo senha não pode ter menos que 6 letras/números.", Toast.LENGTH_SHORT).show();
            editSenha.requestFocus();
            return false;
        }

        if(tipoUsuarioSelecionado == 0){
            Toast.makeText(this, "Campo tipo de Usuário é obrigatório.", Toast.LENGTH_SHORT).show();
            radioGroupTipoUsuario.requestFocus();
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

    private int GetValueRadioButtonTipoUsuarioSelecionado(){

        if(radioArbitro.isChecked()){
            return 1;
        }else if(radioJogador.isChecked()){
            return 2;
        }else{
            Toast.makeText(this, "Selecione um tipo de usuário.", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }


    public boolean CriarConta(){
         boolean logado = false;

        String email = editEmail.getText().toString();
        final String senha = editSenha.getText().toString();
        final String nome = editNome.getText().toString();
        final String sobrenome = editSobrenome.getText().toString();
        final int tipoDeUsuarioLogado = GetValueRadioButtonTipoUsuarioSelecionado();

        mAuth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            currentUser = mAuth.getCurrentUser();
                            String displayName = currentUser.getDisplayName();
                            String emailUser = currentUser.getEmail();
                            Uri photoUrl = currentUser.getPhotoUrl();
                            String phoneNumber = currentUser.getPhoneNumber();
                            String uid = currentUser.getUid();
                            String proveiderID = currentUser.getProviderId();
                            boolean isEmailVerified = currentUser.isEmailVerified();
                            String dataComHora = metodosPublicos.GetDataEHora(new Date());

                            usuario = new Usuario(uid, emailUser, nome, sobrenome, senha, tipoDeUsuarioLogado, dataComHora);

                            databaseReference.child(currentUser.getUid()).setValue(usuario);

                            LimparCampos();
                            Intent intent = new Intent(CriarContaActivity.this, MainActivity.class);
                            intent.putExtra("usuarioLogado", usuario);
                            startActivity(intent);

                        } else {

                            boolean semInternet = verificaSinalInternet();
                            String msg = "";
                            if(semInternet){
                                msg = GetMensagemDeErro(task.getException().getMessage());
                            }
                            else{
                                msg = "Sem sinal de internet, conecte-se novamente.";
                            }


                            Toast.makeText(CriarContaActivity.this, "Falha na autenticação. " + msg,
                                    Toast.LENGTH_LONG).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });

        return logado;
    }

    private String GetMensagemDeErro(String erro){

        String mensagem = null;

        if(erro == "The password is invalid or the user does not have a password."){
            mensagem = "Senha incorreta, ou usuário não registrado.";
        }
        else if(erro == "There is no user record corresponding to this identifier. The user may have been deleted."){
            mensagem = "Não há registro de usuário correspondente a esse e-mail.";
        }
        else if(erro == "The email address is already in use by another account."){
            mensagem = "O endereço de e-mail já está sendo usado por outra conta.";
        }

        return mensagem;

    }

    private boolean verificaSinalInternet(){
        internetReceiver = new InternetReceiver();
        return internetReceiver.verificaInternet(App.getContext());
        // chama o service que verifica se há Conexão com a Internet
        //App.getContext().startService(new Intent(App.getContext(), AccessInternetService.class));
    }

    private void verificaSinalInternetLoad(){
        App.getContext().startService(new Intent(App.getContext(), AccessInternetService.class));
    }

}
