package souza.edenilson.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Usuario implements Serializable {

   private String ID;
   private String Nome;
   private String SobreNome;
   private String Email;
   private String Senha;
   private String UserName;
   private String DataDeNascimento;
   private String UrlFoto;
   private String Genero;
   private String DataDeCadastro;
   private int TipoDeUsuario; /*tipo:{1 - arbitro, 2 - jogador, 3 - clubes}*/
   private Endereco Endereco;
   private int DistanciaDisponivel;
   private boolean Disponivel;
   private int QuantidadePartidas;
   private double AvaliacaoGeral;
   private List<Esporte> ListaDeEsportes;
    String Token;

    public Usuario() {
    }

    public Usuario(String id, String email) {
        ID = id;
        Email = email;
    }

    public Usuario(String id, String nome, String sobreNome, String email, String senha, String userName,
                   String dataDeNascimento, String urlFoto, String genero, String dataDeCadastro,
                   int tipoDeUsuario, Endereco endereco, int distanciaDisponivel, boolean disponivel, int quantidadePartidas, double avaliacaoGeral, List<Esporte> listaDeEsportes, String token ) {
        ID = id;
        Nome = nome;
        SobreNome = sobreNome;
        Email = email;
        Senha = senha;
        UserName = userName;
        DataDeNascimento = dataDeNascimento;
        UrlFoto = urlFoto;
        Genero = genero;
        DataDeCadastro = dataDeCadastro;
        TipoDeUsuario = tipoDeUsuario;
        Endereco = endereco;
        DistanciaDisponivel = distanciaDisponivel;
        Disponivel = disponivel;
        QuantidadePartidas = quantidadePartidas;
        AvaliacaoGeral = avaliacaoGeral;
        ListaDeEsportes = listaDeEsportes;
        String Token = token;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSenha() {
        return Senha;
    }

    public void setSenha(String senha) {
        Senha = senha;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public String getSobreNome() {
        return SobreNome;
    }

    public void setSobreNome(String sobreNome) {
        SobreNome = sobreNome;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getDataDeNascimento() {
        return DataDeNascimento;
    }

    public void setDataDeNascimento(String dataDeNascimento) {
        DataDeNascimento = dataDeNascimento;
    }

    public String getGenero() {
        return Genero;
    }

    public void setGenero(String genero) {
        Genero = genero;
    }

    public int getTipoDeUsuario() {
        return TipoDeUsuario;
    }

    public void setTipoDeUsuario(int tipoDeUsuario) {
        TipoDeUsuario = tipoDeUsuario;
    }

    public Endereco getEndereco() {
        return Endereco;
    }

    public void setEndereco(Endereco endereco) {
        Endereco = endereco;
    }

    public String getDataDeCadastro() {
        return DataDeCadastro;
    }

    public void setDataDeCadastro(String dataDeCadastro) {
        DataDeCadastro = dataDeCadastro;
    }

    public String getUrlFoto() {
        return UrlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        UrlFoto = urlFoto;
    }

    public int getDistanciaDisponivel() {
        return DistanciaDisponivel;
    }

    public void setDistanciaDisponivel(int distanciaDisponivel) {
        DistanciaDisponivel = distanciaDisponivel;
    }

    public boolean isDisponivel() {
        return Disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        Disponivel = disponivel;
    }

    public int getQuantidadePartidas() {
        return QuantidadePartidas;
    }

    public void setQuantidadePartidas(int quantidadePartidas) {
        QuantidadePartidas = quantidadePartidas;
    }

    public double getAvaliacaoGeral() {
        return AvaliacaoGeral;
    }

    public void setAvaliacaoGeral(double avaliacaoGeral) {
        AvaliacaoGeral = avaliacaoGeral;
    }

    public List<Esporte> getListaDeEsportes() {
        return ListaDeEsportes;
    }

    public void setListaDeEsportes(List<Esporte> listaDeEsportes) {
        ListaDeEsportes = listaDeEsportes;
    }

    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
