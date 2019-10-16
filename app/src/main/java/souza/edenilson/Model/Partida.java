package souza.edenilson.Model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Partida implements Serializable {

    /*
    id, endereco,data,arbitro, donoDoPartida{ jogador, clube}	avaliacaoArbitro
     */

    private String ID; // Dono da Partida + data da partida + hora da partida
    private Endereco Endereco;
    private Usuario UsuarioDonoDaPartida; // jogador ou clube
    private Esporte Esporte;
    private Usuario Arbitro;
    private String DataDaPartida;
    private String HoraDaPartida;
    private int AvaliacaoArbitro;
    private int StatusConvite; //  0 = iniciado  |  1 = aceito pelo Arbitro  |  2 = aguardando | 3 - não aceito pelo Arbitro | 4 - não aceito pelo Dono da Partida | 5 - Aceito pelo Dono da Partida
    private boolean Status;  // ativo e inativo / inativo depois que a partida foi executada

    public Partida(){}

    public Partida(String id, Endereco endereco, Usuario donoDaPartida, String dataDaPartida) {
        ID = id;
        Endereco = endereco;
        UsuarioDonoDaPartida = donoDaPartida;
        DataDaPartida = dataDaPartida;
    }

    public Partida(String id, souza.edenilson.Model.Endereco endereco, Usuario usuarioDonoDaPartida,
                   Esporte esporte, Usuario arbitro, String dataDaPartida, String horaDaPartida, int avaliacaoArbitro, boolean status, int statusConvite) {
        ID = id;
        Endereco = endereco;
        UsuarioDonoDaPartida = usuarioDonoDaPartida;
        Esporte = esporte;
        Arbitro = arbitro;
        DataDaPartida = dataDaPartida;
        HoraDaPartida = horaDaPartida;
        AvaliacaoArbitro = avaliacaoArbitro;
        Status = status;
        StatusConvite = statusConvite;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public souza.edenilson.Model.Endereco getEndereco() {
        return Endereco;
    }

    public void setEndereco(souza.edenilson.Model.Endereco endereco) {
        Endereco = endereco;
    }

    public Usuario getUsuarioDonoDaPartida() {
        return UsuarioDonoDaPartida;
    }

    public void setUsuarioDonoDaPartida(Usuario usuarioDonoDaPartida) {
        UsuarioDonoDaPartida = usuarioDonoDaPartida;
    }

    public Esporte getEsporte() {
        return Esporte;
    }

    public void setEsporte(Esporte esporte) {
        Esporte = esporte;
    }

    public Usuario getArbitro() {
        return Arbitro;
    }

    public void setArbitro(Usuario arbitro) {
        Arbitro = arbitro;
    }

    public String getDataDaPartida() {
        return DataDaPartida;
    }

    public void setDataDaPartida(String dataDaPartida) {
        DataDaPartida = dataDaPartida;
    }

    public String getHoraDaPartida() {
        return HoraDaPartida;
    }

    public void setHoraDaPartida(String horaDaPartida) {
        HoraDaPartida = horaDaPartida;
    }

    public int getAvaliacaoArbitro() {
        return AvaliacaoArbitro;
    }

    public void setAvaliacaoArbitro(int avaliacaoArbitro) {
        AvaliacaoArbitro = avaliacaoArbitro;
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public int getStatusConvite() {
        return StatusConvite;
    }

    public void setStatusConvite(int statusConvite) {
        StatusConvite = statusConvite;
    }
}
