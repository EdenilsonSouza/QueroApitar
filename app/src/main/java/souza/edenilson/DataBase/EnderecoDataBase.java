package souza.edenilson.DataBase;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import souza.edenilson.Model.Endereco;
import souza.edenilson.Model.Usuario;

public class EnderecoDataBase {


    private String ID;
    private String Local;
    FirebaseDatabase firebaseDatabase;

    public EnderecoDataBase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void Salva(Endereco endereco){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("endereco");
        // idEndereco = CEP da rua + numero da casa + data do cadastro do endereco
        String idEndereco = endereco.getCEP() + endereco.getNumero() + String.valueOf(new Date().getTime());
        databaseReference.child(idEndereco).setValue(endereco);
    }

    public void Remover(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("endereco");
        String idEndereco = getID();
        databaseReference.child("endereco").child(idEndereco).removeValue();
    }

    @Exclude
    public String getID() {
        return ID;
    }

    public void setID() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("endereco");
        this.ID = databaseReference.push().getKey();
    }

    public void Atualiza(Endereco endereco){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("endereco").child(endereco.getID());

        Map<String,Object> result = new HashMap<String,Object>();
        result.put("id", endereco.getID());
        result.put("logradouro", endereco.getLogradouro());
        result.put("numero", endereco.getNumero());
        result.put("bairro", endereco.getBairro());
        result.put("cidade", endereco.getCidade());
        result.put("estado", endereco.getEstado());
        result.put("pais", endereco.getPais());
        result.put("cep", endereco.getCEP());
        result.put("latitude", endereco.getLatitude());
        result.put("longitude", endereco.getLongitude());
        result.put("codigoCidade", endereco.getCodigoCidade());
        result.put("codigoEstado", endereco.getCodigoEstado());

        databaseReference.updateChildren(result);
    }
}
