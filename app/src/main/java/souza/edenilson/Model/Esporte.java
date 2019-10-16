package souza.edenilson.Model;

import java.io.Serializable;

public class Esporte implements Serializable {

    private String ID;
    private String Nome;
    private String Valor;
    private String IdentificadorEsporte;

    public Esporte() {
    }

    public Esporte(String ID, String nome, String valor, String identificadorEsporte) {
        this.ID = ID;
        Nome = nome;
        Valor = valor;
        IdentificadorEsporte = identificadorEsporte;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getValor() {
        return Valor;
    }

    public void setValor(String valor) {
        Valor = valor;
    }

    public String getIdentificadorEsporte() {
        return IdentificadorEsporte;
    }

    public void setIdentificadorEsporte(String identificadorEsporte) {
        IdentificadorEsporte = identificadorEsporte;
    }
}
