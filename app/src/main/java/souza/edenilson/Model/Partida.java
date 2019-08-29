package souza.edenilson.Model;

import com.google.firebase.database.Exclude;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Partida {

    /*
    id, endereco,data,arbitro, donoDoPartida{ jogador, clube}	avaliacaoArbitro
     */

    private int ID;
    private int Endereco;
    private int DonoDaPartida; // jogador ou clube
    private int Arbitro;
    private Date DataDaPartida;
    private int AvaliacaoArbitro;

    public Partida(int ID, int endereco, int donoDaPartida, Date dataDaPartida) {
        this.ID = ID;
        Endereco = endereco;
        DonoDaPartida = donoDaPartida;
        DataDaPartida = dataDaPartida;
    }

    public Partida(int ID, int endereco, int donoDaPartida, int arbitro, Date dataDaPartida, int avaliacaoArbitro) {
        this.ID = ID;
        Endereco = endereco;
        DonoDaPartida = donoDaPartida;
        Arbitro = arbitro;
        DataDaPartida = dataDaPartida;
        AvaliacaoArbitro = avaliacaoArbitro;
    }

    @Exclude
    public Map<String, Object> PartidaToMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("ID", ID);
        result.put("DonoDaPartida", DonoDaPartida);
        result.put("Endereco", Endereco);
        result.put("Arbitro", Arbitro);
        result.put("DataDaPartida", DataDaPartida);
        result.put("AvaliacaoArbitro", AvaliacaoArbitro);
        return result;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getEndereco() {
        return Endereco;
    }

    public void setEndereco(int endereco) {
        Endereco = endereco;
    }

    public int getDonoDaPartida() {
        return DonoDaPartida;
    }

    public void setDonoDaPartida(int donoDaPartida) {
        DonoDaPartida = donoDaPartida;
    }

    public int getArbitro() {
        return Arbitro;
    }

    public void setArbitro(int arbitro) {
        Arbitro = arbitro;
    }

    public Date getDataDaPartida() {
        return DataDaPartida;
    }

    public void setDataDaPartida(Date dataDaPartida) {
        DataDaPartida = dataDaPartida;
    }

    public int getAvaliacaoArbitro() {
        return AvaliacaoArbitro;
    }

    public void setAvaliacaoArbitro(int avaliacaoArbitro) {
        AvaliacaoArbitro = avaliacaoArbitro;
    }
}
