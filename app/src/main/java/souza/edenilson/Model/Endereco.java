package souza.edenilson.Model;

import java.io.Serializable;

public class Endereco implements Serializable {
/*
    id,  logradouro,  numero,  bairro,  cidade,  estado , pais  */

    private String ID;
    private String Logradouro;
    private String Numero;
    private String Bairro;
    private String Cidade;
    private int CodigoCidade;
    private int CodigoEstado;
    private String Estado;
    private int Pais = 1;
    private String CEP;
    private double Latitude;
    private double Longitude;

    public Endereco() {}

    public Endereco(String logradouro, String numero, String bairro, String cidade, int codigoCidade,
                    int codigoEstado, String estado, int pais, String cep, double latitude, double longitude) {
        Logradouro = logradouro;
        Numero = numero;
        Bairro = bairro;
        Cidade = cidade;
        CodigoCidade = codigoCidade;
        CodigoEstado = codigoEstado;
        Estado = estado;
        Pais = pais;
        CEP = cep;
        Latitude = latitude;
        Longitude = longitude;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
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

    public String getCEP() {
        return CEP;
    }

    public void setCEP(String CEP) {
        this.CEP = CEP;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

}
