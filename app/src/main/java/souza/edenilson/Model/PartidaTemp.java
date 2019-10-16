package souza.edenilson.Model;


public class PartidaTemp{

    Partida Partida;
    Usuario Arbitro;
    Usuario DonoDaPartida;

    public PartidaTemp(){}

    public PartidaTemp(Partida partida, Usuario arbitro, Usuario donoDaPartida) {
        Partida = partida;
        Arbitro = arbitro;
        DonoDaPartida = donoDaPartida;
    }

    public Partida getPartida() {
        return Partida;
    }

    public void setPartida(Partida partida) {
        Partida = partida;
    }

    public Usuario getArbitro() {
        return Arbitro;
    }

    public void setArbitro(Usuario arbitro) {
        Arbitro = arbitro;
    }

    public Usuario getDonoDaPartida() {
        return DonoDaPartida;
    }

    public void setDonoDaPartida(Usuario donoDaPartida) {
        DonoDaPartida = donoDaPartida;
    }
}