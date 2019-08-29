package souza.edenilson.Model;

public class Endereco {
/*
    id,  logradouro,  numero,  bairro,  cidade,  estado , pais  */

    private int ID;
    private String Logradouro;
    private String Numero;
    private String Bairro;
    private String Cidade;
    private int CodigoCidade;
    private int CodigoEstado;
    private String Estado;
    private int Pais = 1;

    public Endereco(int ID, String logradouro, String numero, String bairro, String cidade, int codigoCidade, int codigoEstado, String estado, int pais) {
        this.ID = ID;
        Logradouro = logradouro;
        Numero = numero;
        Bairro = bairro;
        Cidade = cidade;
        CodigoCidade = codigoCidade;
        CodigoEstado = codigoEstado;
        Estado = estado;
        Pais = pais;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLogradouro() {
        return Logradouro;
    }

    public void setLogradouro(String logradouro) {
        Logradouro = logradouro;
    }

    public String getNumero() {
        return Numero;
    }

    public void setNumero(String numero) {
        Numero = numero;
    }

    public String getBairro() {
        return Bairro;
    }

    public void setBairro(String bairro) {
        Bairro = bairro;
    }

    public String getCidade() {
        return Cidade;
    }

    public void setCidade(String cidade) {
        Cidade = cidade;
    }

    public int getCodigoCidade() {
        return CodigoCidade;
    }

    public void setCodigoCidade(int codigoCidade) {
        CodigoCidade = codigoCidade;
    }

    public int getCodigoEstado() {
        return CodigoEstado;
    }

    public void setCodigoEstado(int codigoEstado) {
        CodigoEstado = codigoEstado;
    }

    public String getEstado() {
        return Estado;
    }

    public void setEstado(String estado) {
        Estado = estado;
    }

    public int getPais() {
        return Pais;
    }

    public void setPais(int pais) {
        Pais = pais;
    }
}
