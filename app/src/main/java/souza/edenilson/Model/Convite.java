package souza.edenilson.Model;

import java.io.Serializable;

public class Convite implements Serializable {

    private String Id;
    private String DataDoConvite;
    private Usuario Remetente;
    private Usuario Convidado;
    private Partida Partida;
    private boolean Avaliado;
    private int Status;
    //  0 = iniciado  |  1 = aceito pelo Arbitro  |  2 = aguardando | 3 - não aceito pelo Arbitro |
    // 4 - não aceito pelo Dono da Partida | 5 - Aceito pelo Dono da Partida | 6 - oferta do Arbitro | 7 - Data Passada/ Finalizada
    private boolean Visualizado;

    public Convite() {
    }

    public Convite(String id, String dataDoConvite, Usuario remetente, Usuario convidado, Partida partida, int status, boolean avaliado, boolean visualizado) {
        Id = id;
        DataDoConvite = dataDoConvite;
        Remetente = remetente;
        Convidado = convidado;
        Partida = partida;
        Status = status;
        Avaliado = avaliado;
        Visualizado = visualizado;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getDataDoConvite() {
        return DataDoConvite;
    }

    public void setDataDoConvite(String dataDoConvite) {
        DataDoConvite = dataDoConvite;
    }

    public Usuario getRemetente() {
        return Remetente;
    }

    public void setRemetente(Usuario remetente) {
        Remetente = remetente;
    }

    public Usuario getConvidado() {
        return Convidado;
    }

    public void setConvidado(Usuario convidado) {
        Convidado = convidado;
    }

    public Partida getPartida() {
        return Partida;
    }

    public void setPartida(Partida partida) {
        Partida = partida;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public boolean isAvaliado() {
        return Avaliado;
    }

    public void setAvaliado(boolean avaliado) {
        Avaliado = avaliado;
    }

    public boolean isVisualizado() {
        return Visualizado;
    }

    public void setVisualizado(boolean visualizado) {
        Visualizado = visualizado;
    }
}
