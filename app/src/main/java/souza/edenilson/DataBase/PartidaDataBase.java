package souza.edenilson.DataBase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import souza.edenilson.Model.Endereco;
import souza.edenilson.Model.Partida;

public class PartidaDataBase {

    private String ID;
    private String Local;
    FirebaseDatabase firebaseDatabase;

    public PartidaDataBase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void Salvar(Partida partida){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("partida");
        // Dono da Partida + data da partida + hora da partida
        String idDaPartida = partida.getUsuarioDonoDaPartida() + "_" + partida.getDataDaPartida() + partida.getHoraDaPartida();
        databaseReference.child(idDaPartida).setValue(partida);
    }

    public void Remover(String id){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("partida");

        databaseReference.child(id).removeValue();
    }

    @Exclude
    public String getID() {
        return ID;
    }

    public void setID() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("partida");
        this.ID = databaseReference.push().getKey();
    }

    public void Atualiza(Partida partida){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("partida").child(partida.getID());

        Map<String,Object> result = new HashMap<String,Object>();
        result.put("id", partida.getID());
        result.put("usuarioDonoDaPartida", partida.getUsuarioDonoDaPartida());
        result.put("arbitro", partida.getArbitro());
        result.put("dataDaPartida", partida.getDataDaPartida());
        result.put("horaDaPartida", partida.getHoraDaPartida());
        result.put("avaliacaoArbitro", partida.getAvaliacaoArbitro());
        result.put("status", partida.isStatus());
        result.put("endereco", partida.getEndereco());
        result.put("statusConvite", partida.getStatusConvite());
        result.put("esporte", partida.getEsporte());

        databaseReference.updateChildren(result);
    }

}
