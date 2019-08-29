package souza.edenilson.queroapitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.Model.Usuario;
import souza.edenilson.Services.AccessInternetService;

public class LoginActivity extends AppCompatActivity {

    private static final String COLLECTION_NAME = "usuario";

    MetodosPublicos metodosPublicos;
    Usuario usuario;

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    //FirebaseFirestore firebaseFirestore;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;


    EditText editEmail;
    EditText editSenha;
    Button btnLogar;
    private RadioGroup radioGroupTipoUsuarioLogin;
    private RadioButton radioArbitroLogin, radioJogadorLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        App.setContext(this);
        metodosPublicos = new MetodosPublicos();

        verificaSinalInternet();
        InicializaFirebase();
        InicializaCampos();
        ConfiguraRadioGroup();
    }

    private void InicializaFirebase(){
        mAuth = FirebaseAuth.getInstance();
        //firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(COLLECTION_NAME);
        databaseReference.keepSynced(true);
    }

    private void InicializaCampos(){
        editEmail = (EditText) findViewById(R.id.edit_email_usuario);
        editSenha = (EditText) findViewById(R.id.edit_senha_usuario);
        btnLogar = (Button) findViewById(R.id.btn_logar);

        radioGroupTipoUsuarioLogin = (RadioGroup) findViewById(R.id.radioGroupTipoUsuarioLogin);
        radioArbitroLogin = (RadioButton) findViewById(R.id.radioArbitroLogin);
        radioJogadorLogin = (RadioButton) findViewById(R.id.radioJogadorLogin);
        radioArbitroLogin.setChecked(false);
        radioJogadorLogin.setChecked(false);
    }

    private void ConfiguraRadioGroup(){

        radioGroupTipoUsuarioLogin.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioArbitroLogin) {
                    radioArbitroLogin.setChecked(true);

                } else if(checkedId == R.id.radioJogadorLogin) {
                    radioJogadorLogin.setChecked(true);
                }
            }

        });

    }

    private int GetValueRadioButtonTipoUsuarioSelecionado(){

        if(radioArbitroLogin.isChecked()){
            return 1;
        }else if(radioJogadorLogin.isChecked()){
            return 2;
        }else{
            Toast.makeText(this, "Selecione um tipo de usuário.", Toast.LENGTH_SHORT).show();
            return 0;
        }
    }

    private boolean validaLogin(String email, String senha, int tipoUsuarioSelecionado){

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
            radioGroupTipoUsuarioLogin.requestFocus();
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

    public void SignIn(String email, final String senha, final String tipoDeUsuario){
        mAuth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            currentUser = mAuth.getCurrentUser();
                            String displayName = currentUser.getDisplayName();
                            final String email = currentUser.getEmail();
                            Uri photoUrl = currentUser.getPhotoUrl();
                            String phoneNumber = currentUser.getPhoneNumber();
                            String uid = currentUser.getUid();
                            String proveiderID = currentUser.getProviderId();
                            boolean isEmailVerified = currentUser.isEmailVerified();

                            databaseReference =  firebaseDatabase.getInstance().getReference(COLLECTION_NAME);
                            databaseReference.addValueEventListener(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot child : dataSnapshot.getChildren()){

                                        String mail = child.getValue(Usuario.class).getEmail();

                                        if(child.exists() && mail.equals(email) && Integer.parseInt(tipoDeUsuario) == child.getValue(Usuario.class).getTipoDeUsuario()){

                                            usuario = child.getValue(Usuario.class);
                                            if(usuario != null){
                                                LimparCampos();
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.putExtra("usuarioLogado", usuario);
                                                startActivity(intent);
                                            }
                                            break;
                                        }else {

                                            if(Integer.parseInt(tipoDeUsuario) != child.getValue(Usuario.class).getTipoDeUsuario()){
                                                Toast.makeText(LoginActivity.this, "Erro ao efetuar o login, usuário não localizado, verifique o tipo de usuário selecionado.", Toast.LENGTH_LONG).show();
                                            }
                                            //continue;
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }


                            });
                        }

                        if(task.isCanceled()){

                            Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, "Evento addOnFailureListener - Erro ao efetuar o login. " + metodosPublicos.GetMensagemDeErro(e.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    public void LogarClick(View view) {

        String email = editEmail.getText().toString();
        String senha = editSenha.getText().toString();
        int tipoUsuarioSelecionado = GetValueRadioButtonTipoUsuarioSelecionado();

        if(validaLogin(email, senha, tipoUsuarioSelecionado)){
            String tipoUsuario = Integer.toString(tipoUsuarioSelecionado);

            SignIn(email, senha, tipoUsuario);
        }else{
            Toast.makeText(this, "Login inválido.", Toast.LENGTH_LONG);
        }
    }

    private void LimparCampos(){
        editEmail.setText("");
        editSenha.setText("");
        radioArbitroLogin.setChecked(false);
        radioJogadorLogin.setChecked(false);
    }


    void verificaSinalInternet(){
        // chama o service que verifica se há Conexão com a Internet
        App.getContext().startService(new Intent(App.getContext(), AccessInternetService.class));
    }
}
