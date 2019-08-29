package souza.edenilson.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Usuario implements Serializable {

   private String ID;
   private String Senha;
   private String Email;
   private String Nome;
   private String SobreNome;
   private String UserName;
   private Date DataDeNascimento;
   private String UrlFoto;
   private String Genero;
   private int TipoDeUsuario; /*tipo:{1 - arbitro, 2 - jogador, 3 - clubes}*/
   private int Endereco;
   private String DataDeCadastro;

    public Usuario() {
    }

    public Usuario(String id, String email){
        ID = id;
        Email = email;
    }

    public Usuario(String id, String email, String senha, int tipoDeUsuario){
        ID = id;
        Email = email;
        Senha = senha;
        TipoDeUsuario = tipoDeUsuario;
    }

    public Usuario(String id, String email, String nome,String sobrenome,String senha, int tipoDeUsuario, String dataDeCadastro){
        ID = id;
        Email = email;
        Senha = senha;
        Nome = nome;
        SobreNome = sobrenome;
        TipoDeUsuario = tipoDeUsuario;
        DataDeCadastro = dataDeCadastro;
    }

    public Usuario(String id, String email, String senha, int tipoDeUsuario, String urlFoto){
        ID = id;
        Email = email;
        Senha = senha;
        TipoDeUsuario = tipoDeUsuario;
        UrlFoto = urlFoto;
    }

    public Usuario(String senha, String email, String nome, String sobreNome, String userName, Date dataDeNascimento, String genero, int tipoDeUsuario, int endereco, String dataDeCadastro) {
        Senha = senha;
        Email = email;
        Nome = nome;
        SobreNome = sobreNome;
        UserName = userName;
        DataDeNascimento = dataDeNascimento;
        Genero = genero;
        TipoDeUsuario = tipoDeUsuario;
        Endereco = endereco;
        DataDeCadastro = dataDeCadastro;
    }

    // este metodo faz o mapeamento estre o objeto e o firebase.
    @Exclude
    public Map<String, Object> UsuarioToMap(Usuario user) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Email", user.getEmail());
        result.put("Senha", user.getSenha());
        result.put("Nome", user.getNome());
        result.put("SobreNome", user.getSobreNome());
        result.put("UserName", user.getUserName());
        result.put("DataDeNascimento", user.getDataDeNascimento());
        result.put("Genero", user.getGenero());
        result.put("Endereco", user.getEndereco());
        result.put("DataDeCadastro", user.getDataDeCadastro());
        result.put("TipoDeUsuario", user.getTipoDeUsuario());
        return result;
    }

    public DatabaseReference AdicionaUsuario(){
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference("usuarios");
        usuariosRef.setValue(this);
        return usuariosRef;
    }

    public void AtualizaUsuario(){
        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference("usuarios");

        Map objeto = new HashMap();
        objeto.put("/usuarios/" + getNome() + "/nome", getNome() );
        objeto.put("/usuarios/" + getEmail() + "/email", getEmail() );
        objeto.put("/usuarios/" + getSenha() + "/senha", getSenha() );
        objeto.put("/usuarios/" + getSobreNome() + "/sobrenome", getSobreNome() );
        objeto.put("/usuarios/" + getUserName() + "/username", getUserName() );
        objeto.put("/usuarios/" + getDataDeNascimento() + "/datadenascimento", getDataDeNascimento() );
        objeto.put("/usuarios/" + getGenero() + "/genero", getGenero() );
        objeto.put("/usuarios/" + getTipoDeUsuario() + "/tipodeusuario", getTipoDeUsuario() );
        objeto.put("/usuarios/" + getEndereco() + "/endereco", getEndereco() );
        objeto.put("/usuarios/" + getDataDeCadastro() + "/datadecadastro", getDataDeCadastro() );

        firebaseRef.updateChildren( objeto );
    }

    public void RemoveUsuario(String id){
        DatabaseReference uR = FirebaseDatabase.getInstance().getReference("usuarios").child(id);
        uR.removeValue();
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

    public Date getDataDeNascimento() {
        return DataDeNascimento;
    }

    public void setDataDeNascimento(Date dataDeNascimento) {
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

    public int getEndereco() {
        return Endereco;
    }

    public void setEndereco(int endereco) {
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
}
