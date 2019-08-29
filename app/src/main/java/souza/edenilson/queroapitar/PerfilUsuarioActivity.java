package souza.edenilson.queroapitar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import souza.edenilson.Model.Usuario;

public class PerfilUsuarioActivity extends AppCompatActivity {

    TextView txtEmailUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        App.setContext(this);
        txtEmailUsuario = (TextView) findViewById(R.id.txtEmailUsuario);

        Intent intent = getIntent();
        Usuario usuarioLogado =  (Usuario)intent.getSerializableExtra("usuarioLogado");

        if(usuarioLogado != null){
            txtEmailUsuario.setText(usuarioLogado.getEmail() + " - "+ usuarioLogado.getTipoDeUsuario());
        }

    }
}
