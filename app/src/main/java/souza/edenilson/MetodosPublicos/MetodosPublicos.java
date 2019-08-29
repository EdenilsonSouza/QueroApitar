package souza.edenilson.MetodosPublicos;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MetodosPublicos {

    public MetodosPublicos() { }

    public Intent IndicarAmigos(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        String texto = "Ola como vai? você recebeu uma indicação para conhecer o APP Quero Apitar," +
                " precisa de Árbitro para tua partida né? que tal baixar o APP e encontrar um bom árbitro para essa partida, e se preocupar somente em jogar, entra nesse link e boa partida " +
                " www.netflix.com/br/Login";
        sendIntent.putExtra(Intent.EXTRA_TEXT, texto);
        sendIntent.setType("text/plain");
        return sendIntent;
    }

    public Map<Integer, String> GetEsporteColetivo(){

        Map<Integer, String> mapEsportes = new HashMap<Integer, String>();

        mapEsportes.put(1,"Basquete");
        mapEsportes.put(2,"Beisebol");
        mapEsportes.put(3,"Bocha");
        mapEsportes.put(4,"Futebol Americano");
        mapEsportes.put(5,"Futebol de Areia");
        mapEsportes.put(6,"Futebol de Campo");
        mapEsportes.put(7,"Futêvolei");
        mapEsportes.put(8,"Futsal");
        mapEsportes.put(9,"Handebol");
        mapEsportes.put(10,"Rugby");
        mapEsportes.put(11,"Society / Fut7");
        mapEsportes.put(12,"Tênis");
        mapEsportes.put(13,"Vôlei de Praia");
        mapEsportes.put(15,"Vôlei de Quadra");

        return mapEsportes;
    }

    public Map<Integer, String> GetEsporteColetivo(int codigo){

        Map<Integer, String> mapEsportes = new HashMap<Integer, String>();

        for (Map.Entry<Integer,String> pair : GetEsporteColetivo().entrySet()){

            if(codigo == pair.getKey()){
                mapEsportes.put(pair.getKey(),pair.getValue());
            }
        }
        return mapEsportes;
    }

    public String GetDataPorExtenso(Date date){
        String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
        String day          = (String) DateFormat.format("dd",   date); // 20
        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        String monthNumber  = (String) DateFormat.format("MM",   date); // 06
        String year         = (String) DateFormat.format("yyyy", date); // 201

        // 26 de agosto de 2019
        return day + " de " + GetMes(monthNumber) + " de " + year;
    }

    private String GetMes(String intMes){

        String mes = null;

        switch (intMes){
            case "01": mes = "janeiro"; break;
            case "02": mes = "fevereiro"; break;
            case "03": mes = "março"; break;
            case "04": mes = "abril"; break;
            case "05": mes = "maio"; break;
            case "06": mes = "junho"; break;
            case "07": mes = "julho"; break;
            case "08": mes = "agosto"; break;
            case "09": mes = "setembro"; break;
            case "10": mes = "outubro"; break;
            case "11": mes = "novembro"; break;
            case "12": mes = "janeiro"; break;
        }

        return mes;
    }

    public String GetDataEHora(Date date){

        String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
        String day          = (String) DateFormat.format("dd",   date); // 20
        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        String monthNumber  = (String) DateFormat.format("MM",   date); // 06
        String year         = (String) DateFormat.format("yyyy", date); // 201

        String data = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // 26-08-2019 22:31
        return data + " " + hora;
    }

    public String GetMensagemDeErro(String erro){

        String mensagem = null;

        if(erro == "The password is invalid or the user does not have a password."){
            mensagem = "Senha incorreta, ou usuário não registrado.";
        }
        else if(erro == "There is no user record corresponding to this identifier. The user may have been deleted."){
            mensagem = "Não há registro de usuário correspondente a esse e-mail.";
        }
        else if(erro == "The email address is already in use by another account."){
            mensagem = "O endereço de e-mail já está sendo usado por outra conta.";
        }

        return mensagem;

    }

}
