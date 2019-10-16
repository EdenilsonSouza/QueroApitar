package souza.edenilson.DataBase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.MoreObjects;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import souza.edenilson.Model.Usuario;
import souza.edenilson.queroapitar.PartidaActivity;

public class UsuarioDataBase {

    private String ID;
    private String Local;
    FirebaseUser currentUser;
    FirebaseAuth mAuth;

    public UsuarioDataBase() {
    }

    public void Salvar(Usuario usuario){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("usuario");
        databaseReference.child(usuario.getID()).setValue(usuario);
    }

    public void Remover(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("usuario");
        String id = getID();
        databaseReference.child("usuario").child(getID()).removeValue();
    }

    public void Atualizar(Usuario usuarioLogado){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("usuario").child(usuarioLogado.getID());

        Map<String,Object> result = new HashMap<String,Object>();
        result.put("id", usuarioLogado.getID());
        result.put("nome", usuarioLogado.getNome());
        result.put("senha", usuarioLogado.getSenha());
        result.put("email", usuarioLogado.getEmail());
        result.put("sobreNome", usuarioLogado.getSobreNome());
        result.put("userName", usuarioLogado.getUserName());
        result.put("dataDeCadastro", usuarioLogado.getDataDeCadastro());
        result.put("endereco", usuarioLogado.getEndereco());
        result.put("dataDeNascimento", usuarioLogado.getDataDeNascimento());
        result.put("tipoDeUsuario", usuarioLogado.getTipoDeUsuario());
        result.put("distanciaDisponivel", usuarioLogado.getDistanciaDisponivel());
        result.put("disponivel", usuarioLogado.isDisponivel());
        result.put("urlFoto", usuarioLogado.getUrlFoto());
        result.put("genero", usuarioLogado.getGenero());
        result.put("quantidadePartidas", usuarioLogado.getQuantidadePartidas());
        result.put("avaliacaoGeral", usuarioLogado.getAvaliacaoGeral());
        result.put("listaDeEsportes", usuarioLogado.getListaDeEsportes());
        databaseReference.updateChildren(result);

        /*
        databaseReference.updateChildren(taskMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if(databaseError == null){

                }
            }
        });
        */
    }

    @Exclude
    public String getID() {
        return ID;
    }

    public void setID() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("usuario");
        this.ID = databaseReference.push().getKey();
    }

    public Usuario GetUsuario(Usuario usuario){
        return usuario;
    }

}
