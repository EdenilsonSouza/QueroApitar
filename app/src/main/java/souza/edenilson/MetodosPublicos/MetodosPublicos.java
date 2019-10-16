package souza.edenilson.MetodosPublicos;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import souza.edenilson.Model.Endereco;
import souza.edenilson.Model.Usuario;
import souza.edenilson.Receiver.InternetReceiver;
import souza.edenilson.Services.AccessInternetService;
import souza.edenilson.queroapitar.App;
import souza.edenilson.queroapitar.LoginActivity;
import souza.edenilson.queroapitar.R;
import souza.edenilson.queroapitar.SplashActivity;

public class MetodosPublicos {

    public static final int REQUEST_PERMISSIONS_CODE = 128;
    private static final int PERMISSION_REQUEST_CODE = 7001;
    private static final int PLAY_SERVICE_REQUEST = 7002;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

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

    public String GetStatusConvite(int status){

        String statusConvite = null;
        switch (status){
            case 0: statusConvite = "Iniciado";
                break;
            case 1: statusConvite = "Aceito/Árbitro";
                break;
            case 2: statusConvite = "Aguardando/Árbitro";
                break;
            case 3: statusConvite = "Não aceito/Árbitro";
                break;
            case 4: statusConvite = "Não aceito/Dono da Partida";
                break;
            case 5: statusConvite = "Aceito/Dono da Partida";
                break;
            case 6: statusConvite = "Aguardando/Dono da Partida";
                break;
            case 7: statusConvite = "Partida finalizada";
                break;
        }
        return statusConvite;
    }

    public String GetChaveGoogle(){
        ApplicationInfo app = App.getContext().getApplicationInfo();
        Bundle bundle = app.metaData;

        if(bundle == null){
            return "AIzaSyCkcZQnmrb6nIMjV8fvnO0DIbOBcmI29-I";
        }else{
            return bundle.getString("com.google.android.geo.API_KEY");
        }
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
        mapEsportes.put(14,"Vôlei de Quadra");

        return mapEsportes;
    }

    public int GetIdEsporteSelecionado(String id){

        int idEsporte = 0;

        switch (id){
            case "1": idEsporte = 1;
                break;
            case "2": idEsporte = 2;
                break;
            case "3": idEsporte = 3;
                break;
            case "4": idEsporte = 4;
                break;
            case "5": idEsporte = 5;
                break;
            case "6": idEsporte = 6;
                break;
            case "7": idEsporte = 7;
                break;
            case "8": idEsporte = 8;
                break;
            case "9": idEsporte = 9;
                break;
            case "10": idEsporte =10;
                break;
            case "11": idEsporte = 11;
                break;
            case "12": idEsporte = 12;
                break;
            case "13": idEsporte = 13;
                break;
            case "14": idEsporte = 14;
                break;
        }
        return idEsporte;
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
            case "12": mes = "dezembro"; break;
        }

        return mes;
    }

    public String GetAbreviaturaMes(int mes){

        String abreviatura = null;

        switch (mes){
            case 1: abreviatura = "Jan"; break;
            case 2: abreviatura = "Fev"; break;
            case 3: abreviatura = "Mar"; break;
            case 4: abreviatura = "Abr"; break;
            case 5: abreviatura = "Mai"; break;
            case 6: abreviatura = "Jun"; break;
            case 7: abreviatura = "Jul"; break;
            case 8: abreviatura = "Ago"; break;
            case 9: abreviatura = "Set"; break;
            case 10: abreviatura = "Out"; break;
            case 11: abreviatura = "Nov"; break;
            case 12: abreviatura = "Dez"; break;
        }

        return abreviatura;
    }

    public String ConverteNumeroMES(String mes){

        String numeroMes = null;

        switch (mes){
            case "jan": numeroMes = "1"; break;
            case "fev": numeroMes = "2"; break;
            case "mar": numeroMes = "3"; break;
            case "abr": numeroMes = "4"; break;
            case "mai": numeroMes = "5"; break;
            case "jun": numeroMes = "6"; break;
            case "jul": numeroMes = "7"; break;
            case "ago": numeroMes = "8"; break;
            case "set": numeroMes = "9"; break;
            case "out": numeroMes = "10"; break;
            case "nov": numeroMes = "11"; break;
            case "dez": numeroMes = "12"; break;
        }

        return numeroMes;
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

    public String Capitalize(String texto){
       return texto.substring(0,1).toUpperCase() + texto.substring(1);
    }


    public String GetData(Date date){

        String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
        String day          = (String) DateFormat.format("dd",   date); // 20
        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
        String monthNumber  = (String) DateFormat.format("MM",   date); // 06
        String year         = (String) DateFormat.format("yyyy", date); // 201

        String data = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        // 26-08-2019 22:31
        return data;
    }

    public String FormataData(String data){

        if(data == null || data == "") return null;

        String [] dataSplit = data.split("-");

        String dia = dataSplit[0];
        String mes = dataSplit[1];
        String ano = dataSplit[2];

        return dia + "/" + mes + "/" + ano;
    }

    public Date ConvertStringParaData(String data){

        Date dataFormatada = null;
        try{
            SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
            dataFormatada = formato.parse(data);
        }catch(Exception ex){

        }

        return dataFormatada;
    }

    public String GetSiglaEstado(String estado){
        String sigla = "";
        switch (estado){
            case "Acre": sigla = "AC"; break;
            case "Alagoas": sigla = "AL"; break;
            case "Amapá": sigla = "AP"; break;
            case "Amazonas": sigla = "AM"; break;
            case "Bahia": sigla = "BA"; break;
            case "Ceará": sigla = "CE"; break;
            case "Espirito Santo": sigla = "ES"; break;
            case "Goiás": sigla = "GO"; break;
            case "Maranhão": sigla = "MA"; break;
            case "Mato Grosso": sigla = "MT"; break;
            case "Mato Grosso do Sul": sigla = "MS"; break;
            case "Minas Gerais": sigla = "MG"; break;
            case "Pará": sigla = "PA"; break;
            case "Paraíba": sigla = "PB"; break;
            case "Paraná": sigla = "PR"; break;
            case "Pernambuco": sigla = "PE"; break;
            case "Piauí": sigla = "PI"; break;
            case "Rio de Janeiro": sigla = "RJ"; break;
            case "Rio Grande do Norte": sigla = "RN"; break;
            case "Rio Grande do Sul": sigla = "RS"; break;
            case "Rondônia": sigla = "RO"; break;
            case "Roraima": sigla = "RR"; break;
            case "Santa Catarina": sigla = "SC"; break;
            case "São Paulo": sigla = "SP"; break;
            case "Sergipe": sigla = "SE"; break;
            case "Tocantins": sigla = "TO"; break;
            case "Distrito Federal": sigla = "DF"; break;
        }
        return sigla;
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
        else if(erro.contains("An internal error has occurred.")){
            mensagem = "Ocorreu um erro interno, verifique a Internet.";
        }

        if(mensagem == null)
            return erro;
        else
            return mensagem;

    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    public double CalculaDistanciaDisponivel(Usuario arbitro, Endereco enderecoDaPartida, String unit) {

        if(enderecoDaPartida == null){
            return 0;
        }

        if(arbitro.getEndereco() == null) return 0;
        if(enderecoDaPartida == null) return 0;

        float arbitroLat = (float)arbitro.getEndereco().getLatitude();
        float arbitroLong = (float)arbitro.getEndereco().getLongitude();
        float enderecoDaPartidaLat = (float)enderecoDaPartida.getLatitude();
        float enderecoDaPartidaLong = (float)enderecoDaPartida.getLongitude();

        double theta = arbitroLong - enderecoDaPartidaLong;
        double dist = Math.sin(deg2rad(arbitroLat)) * Math.sin(deg2rad(enderecoDaPartidaLat)) + Math.cos(deg2rad(arbitroLat)) * Math.cos(deg2rad(enderecoDaPartidaLat)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "N") {
            dist = dist * 0.8684;
        }

        return (dist);
    }


    public int RotacaoImagem(String orientacao){

        int rotacao = 0;

        switch (Integer.parseInt(orientacao)) {
            case ExifInterface.ORIENTATION_NORMAL: // rotaciona 0 graus no sentido horário
                rotacao = 0;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90: // rotaciona 90 graus no sentido horário
                rotacao = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180: // rotaciona 180 graus no sentido horário
                rotacao = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270: // rotaciona 270 graus no sentido horário
                rotacao = 270;
                break;
        }
        return rotacao;
    }

    public void CarregaImageView(ImageView imageView, String caminho_imagem){

        if(caminho_imagem != null && !caminho_imagem.equals("")){

            String [] imageParts = caminho_imagem.split("/");

            String whatsAppPath = "storage/emulated/0/WhatsApp/Media/WhatsApp Images/";
            String restoredPath = "storage/emulated/0/RestoredPictures/";
            String cardPath = "sdcard/DCIM/Camera/";
            String phonePath = "storage/FBEC-14EE/DCIM/Camera/";

            File file = null;

            if(imageParts[4].contains(".jpg") || imageParts[4].contains(".png")){

                file = new File(cardPath + imageParts[4]);

            }else if(imageParts[5].contains(".jpg") || imageParts[5].contains(".png")){

                if(caminho_imagem.contains("RestoredPictures")){
                    file = new File(restoredPath + imageParts[5]);
                }else{
                    file = new File(phonePath + imageParts[5]);
                }
            } else if(imageParts[7].contains(".jpg") || imageParts[7].contains(".png")){

                file = new File(whatsAppPath + imageParts[7]);
            }

            caminho_imagem = file.getPath();

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;

            Bitmap myBitmap   = BitmapFactory.decodeFile(caminho_imagem,  options );

            if(myBitmap != null){

                //imageView = (ImageView) findViewById(R.id.imgFotoUsuario);

                String orientacao = null;
                try {
                    ExifInterface exif = new ExifInterface(caminho_imagem);
                    orientacao = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                } catch (IOException e) {
                    e.printStackTrace();
                }
/*
                imageView.setMaxWidth(300);
                imageView.setMaxHeight(150);

                imageView.setMinimumWidth(300);
                imageView.setMinimumHeight(150);
*/
                imageView.setRotation(this.RotacaoImagem(orientacao));

                //Acessando o LayoutParams de sua View
              //  imageView.getLayoutParams().width = 300;
             //   imageView.getLayoutParams().height = 150;

                imageView.setImageBitmap(myBitmap);
            }
        }
    }


    public int CalculaIdade(String nascimento){

        if(nascimento.equals("") || nascimento == null){
            return 0;
        }else{
            Date data_nascimento = null;
            try{
                SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
                data_nascimento = formato.parse(nascimento);
            }catch(Exception ex){

            }

            Calendar cData = Calendar.getInstance();
            Calendar cHoje= Calendar.getInstance();

            cData.setTime(data_nascimento);
            cData.set(Calendar.YEAR, cHoje.get(Calendar.YEAR));

            int idade = cData.after(cHoje) ? -1 : 0;
            cData.setTime(data_nascimento);

            idade += cHoje.get(Calendar.YEAR) - cData.get(Calendar.YEAR);

            return idade;
        }
    }

    /*
    public boolean verificaSinalInternet(Context contexto){
        InternetReceiver internetReceiver = new InternetReceiver();
        return internetReceiver.verificaInternet(contexto);
    }*/

    public boolean temInternet(Context contexto) {
        ConnectivityManager manager =  (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = manager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public void TemInternet(Context contexto, Activity atual, Class destino){
        ConnectivityManager manager =  (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = manager.getActiveNetworkInfo();
        boolean conectado = info != null && info.isConnected();

        if(!conectado){
            contexto.startService(new Intent(App.getContext(), AccessInternetService.class));

            Intent intent = new Intent(atual.getApplicationContext(),destino);
            atual.startActivity(intent);
            atual.finish();
            atual.finishAffinity();
        }
    }



    public void AbreModalSairAPP(Context contexto){

        AlertDialog.Builder builder = new AlertDialog.Builder(contexto);

        builder.setMessage("Deseja sair do APP?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(contexto,LoginActivity.class);
                contexto.startActivity(intent);
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void PopulaIconeAvaliação(List<TextView> ratings, double avaliacao){


        for(int x = 1; x < ratings.size(); x++){
            TextView rating = ratings.get(x);

            if(avaliacao == 1){
                rating.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom
            }
        }


        for (TextView rating : ratings) {

            if(avaliacao == 1){
                rating.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_total_24px, //left
                        0, //top
                        0, //right
                        0);//bottom
            }
            if(avaliacao == 2){
                rating.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_vazia_24px, //left
                        0, //top
                        0, //right
                        0);//bottom
            }
            if(avaliacao == 3){
                rating.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_metade_24px, //left
                        0, //top
                        0, //right
                        0);//bottom
            }
            if(avaliacao == 4){
                rating.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_metade_24px, //left
                        0, //top
                        0, //right
                        0);//bottom
            }
            if(avaliacao == 5){
                rating.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_avaliacao_metade_24px, //left
                        0, //top
                        0, //right
                        0);//bottom
            }

        }

    }



}
