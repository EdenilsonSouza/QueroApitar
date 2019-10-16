package souza.edenilson.DataBase;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.Model.Convite;
import souza.edenilson.Model.Usuario;
import souza.edenilson.queroapitar.App;
import souza.edenilson.queroapitar.ConvitesArbitroActivity;
import souza.edenilson.queroapitar.ConvitesJogadorActivity;
import souza.edenilson.queroapitar.R;

import static android.content.Context.NOTIFICATION_SERVICE;

public class ConviteDataBase {

    private String ID;
    private String Local;
    FirebaseDatabase firebaseDatabase;
    MetodosPublicos metodosPublicos;

    // TIPOS DE AÇÕES
    //  jogadorConvidaArbitro  -
    //  arbitroSeOferece  -
    //  arbitroRecebeAvaliacao  -
    //  arbitroRespondeJogador  -

    public ConviteDataBase() {

        firebaseDatabase = FirebaseDatabase.getInstance();
        metodosPublicos = new MetodosPublicos();
    }

    public void Salvar(Usuario usuarioLogado, Convite convite, Activity activity, String acao){

        App.setContext(activity);
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("convite");
        //databaseReference.child(convite.getId()).setValue(convite);

        databaseReference.child(convite.getId()).setValue(convite).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(App.getContext(), "Convite enviado com sucesso.", Toast.LENGTH_LONG).show();
                }

            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                if(acao.equals("jogadorConvidaArbitro")){

                    if(usuarioLogado.getID().equals(convite.getConvidado().getID())){
                        NotificacaoJogadorConvidaArbitro(convite,activity);
                    }


                }else if(acao.equals("arbitroSeOferece")){

                    if(usuarioLogado.getID().equals(convite.getRemetente().getID())) {
                        NotificacaoArbitroSeOferece(convite, activity);
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(App.getContext(), "Falha ao enviar o convite." + metodosPublicos.GetMensagemDeErro(e.getMessage()), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void Atualiza(Convite convite, Usuario usuarioLogado,  Activity activity, String acao){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("convite").child(convite.getId());

        Map<String,Object> result = new HashMap<String,Object>();
        result.put("id", convite.getId());
        result.put("dataDoConvite", convite.getDataDoConvite());
        result.put("remetente", convite.getRemetente());
        result.put("convidado", convite.getConvidado());
        result.put("partida", convite.getPartida());
        result.put("status", convite.getStatus());
        result.put("avaliado", convite.isAvaliado());
        result.put("visualizado", convite.isVisualizado());
        //databaseReference.updateChildren(result);

        databaseReference.updateChildren(result, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                if(databaseError == null){

                    if(acao.equals("arbitroSeOferece")){

                        if(convite.getRemetente().getID().equals(usuarioLogado.getID())) {
                            NotificacaoArbitroSeOferece(convite, activity);
                        }
                    }
                    else if(acao.equals("arbitroRespondeJogador")){

                        if(convite.getRemetente().getID().equals(usuarioLogado.getID())) {
                            NotificacaoRespostaArbitroParaJogador(convite, activity);
                        }
                    }
                    else if(acao.equals("arbitroRecebeAvaliacao")){

                        if(convite.getConvidado().getID().equals(usuarioLogado.getID())){
                            NotificacaoArbitroRecebeAvaliacao(convite, activity);
                        }
                    }
                    else if(acao.equals("DonoDaPartidaAceitouArbitro")){

                        if(convite.getConvidado().getID().equals(usuarioLogado.getID())){
                            NotificacaoRespostaDoJogadorParaArbitro(convite, activity);
                        }
                    }
                    else if(acao.equals("DonoDaPartidaNaoAceitouArbitro")){

                        if(convite.getConvidado().getID().equals(usuarioLogado.getID())){
                            NotificacaoRespostaNegativaDoJogadorParaArbitro(convite, activity);
                        }
                    }
                }
            }
        });
    }

    public void Remover(String id){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("convite");

        databaseReference.child(id).removeValue();
    }

    @Exclude
    public String getID() {
        return ID;
    }

    public void setID() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("partida");
        this.ID = databaseReference.push().getKey();
    }


    private void NotificacaoJogadorConvidaArbitro(Convite convite, Activity activity){

        App.setContext(activity);
        final String NOTIFICATION_CHANNEL_ID = "10001";

        String esporte = convite.getPartida().getEsporte().getNome();
        String data = metodosPublicos.FormataData(convite.getPartida().getDataDaPartida()) + " " + convite.getPartida().getHoraDaPartida();
        String endereco = convite.getPartida().getEndereco().getLogradouro() +"," +
                convite.getPartida().getEndereco().getNumero() + " - " +
                convite.getPartida().getEndereco().getBairro() + " - " +
                convite.getPartida().getEndereco().getCidade() + " / " +
                metodosPublicos.GetSiglaEstado(convite.getPartida().getEndereco().getEstado());

        int id = 1;
        String TITULO = "Convite - Quero Apitar";

        String nome_remetente =  convite.getRemetente().getNome().substring(0,1).toUpperCase() + convite.getRemetente().getNome().substring(1);
        String sobrenome_remetente =  convite.getRemetente().getSobreNome().substring(0,1).toUpperCase() + convite.getRemetente().getSobreNome().substring(1);

        String DESCRICAO =  nome_remetente + " " + sobrenome_remetente +
                " convidou você para apitar a partida de " + esporte +
                ", na data de: " + data +
                ", no endereço: " + endereco;

        int SMALL_ICONE = R.drawable.logo4;
        int LARGE_ICONE = R.drawable.logo4;

        Intent intent = new Intent(activity, ConvitesArbitroActivity.class);
        intent.putExtra("convite",convite);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(activity,App.getContext().getString(R.string.msg_notificacao));
        notificacao.setContentText(DESCRICAO);
        notificacao.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(DESCRICAO));
        notificacao.setContentTitle(TITULO);
        notificacao.setSmallIcon(SMALL_ICONE);
        notificacao.setLargeIcon(BitmapFactory.decodeResource(App.getContext().getResources(), LARGE_ICONE) );
        notificacao.setContentIntent(pendingIntent);
        notificacao.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificacao.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) App.getContext().getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificacao.setChannelId(NOTIFICATION_CHANNEL_ID);

            nm.createNotificationChannel(notificationChannel);
        }


        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP );


        nm.notify(id, notificacao.build());
    }

    private void NotificacaoRespostaNegativaDoJogadorParaArbitro(Convite convite, Activity activity){

        App.setContext(activity);
        final String NOTIFICATION_CHANNEL_ID = "10001";

        String esporte = convite.getPartida().getEsporte().getNome();
        String data = metodosPublicos.FormataData(convite.getPartida().getDataDaPartida()) + " " + convite.getPartida().getHoraDaPartida();
        String endereco = convite.getPartida().getEndereco().getLogradouro() +"," +
                convite.getPartida().getEndereco().getNumero() + " - " +
                convite.getPartida().getEndereco().getBairro() + " - " +
                convite.getPartida().getEndereco().getCidade() + " / " +
                metodosPublicos.GetSiglaEstado(convite.getPartida().getEndereco().getEstado());

        int id = 1;
        String TITULO = "Resposta do Convite Quero Apitar";

        String nome_convidado =  convite.getConvidado().getNome().substring(0,1).toUpperCase() + convite.getConvidado().getNome().substring(1);
        String sobrenome_convidado =  convite.getConvidado().getSobreNome().substring(0,1).toUpperCase() + convite.getConvidado().getSobreNome().substring(1);

        String convite_negado = nome_convidado + " " + sobrenome_convidado + ", não foi desta vez, foi escolhido outro árbitro para apitar a partida de " + esporte +
                ", na data de: " + data +
                ", no endereço: " + endereco;

        String DESCRICAO =  convite_negado;

        int SMALL_ICONE = R.drawable.logo4;
        int LARGE_ICONE = R.drawable.logo4;

        Intent intent = new Intent(activity, ConvitesJogadorActivity.class);
        intent.putExtra("convite",convite);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(activity,App.getContext().getString(R.string.msg_notificacao));
        notificacao.setContentText(DESCRICAO);
        notificacao.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(DESCRICAO));
        notificacao.setContentTitle(TITULO);
        notificacao.setSmallIcon(SMALL_ICONE);
        notificacao.setLargeIcon(BitmapFactory.decodeResource(App.getContext().getResources(), LARGE_ICONE) );
        notificacao.setContentIntent(pendingIntent);
        notificacao.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificacao.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) App.getContext().getSystemService(activity.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificacao.setChannelId(NOTIFICATION_CHANNEL_ID);

            nm.createNotificationChannel(notificationChannel);
        }


        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP );


        nm.notify(id, notificacao.build());
    }

    private void NotificacaoRespostaDoJogadorParaArbitro(Convite convite, Activity activity){

        App.setContext(activity);
        final String NOTIFICATION_CHANNEL_ID = "10001";

        String esporte = convite.getPartida().getEsporte().getNome();
        String data = metodosPublicos.FormataData(convite.getPartida().getDataDaPartida()) + " " + convite.getPartida().getHoraDaPartida();
        String endereco = convite.getPartida().getEndereco().getLogradouro() +"," +
                convite.getPartida().getEndereco().getNumero() + " - " +
                convite.getPartida().getEndereco().getBairro() + " - " +
                convite.getPartida().getEndereco().getCidade() + " / " +
                metodosPublicos.GetSiglaEstado(convite.getPartida().getEndereco().getEstado());

        int id = 1;
        String TITULO = "Resposta do Convite Quero Apitar";

        String nome_convidado =  convite.getConvidado().getNome().substring(0,1).toUpperCase() + convite.getConvidado().getNome().substring(1);
        String sobrenome_convidado =  convite.getConvidado().getSobreNome().substring(0,1).toUpperCase() + convite.getConvidado().getSobreNome().substring(1);

        String convite_aceito = nome_convidado + " " + sobrenome_convidado + ", a partida é sua, agradeço o interesse em apitar a partida de " + esporte +
                ", na data de: " + data +
                ", no endereço: " + endereco;

        String DESCRICAO =  convite_aceito;

        int SMALL_ICONE = R.drawable.logo4;
        int LARGE_ICONE = R.drawable.logo4;

        Intent intent = new Intent(activity, ConvitesJogadorActivity.class);
        intent.putExtra("convite",convite);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(activity,App.getContext().getString(R.string.msg_notificacao));
        notificacao.setContentText(DESCRICAO);
        notificacao.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(DESCRICAO));
        notificacao.setContentTitle(TITULO);
        notificacao.setSmallIcon(SMALL_ICONE);
        notificacao.setLargeIcon(BitmapFactory.decodeResource(App.getContext().getResources(), LARGE_ICONE) );
        notificacao.setContentIntent(pendingIntent);
        notificacao.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificacao.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) App.getContext().getSystemService(activity.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificacao.setChannelId(NOTIFICATION_CHANNEL_ID);

            nm.createNotificationChannel(notificationChannel);
        }


        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP );


        nm.notify(id, notificacao.build());
    }

    private void NotificacaoRespostaArbitroParaJogador(Convite convite, Activity activity){

        App.setContext(activity);
        final String NOTIFICATION_CHANNEL_ID = "10001";

        String esporte = convite.getPartida().getEsporte().getNome();
        String data = metodosPublicos.FormataData(convite.getPartida().getDataDaPartida()) + " " + convite.getPartida().getHoraDaPartida();
        String endereco = convite.getPartida().getEndereco().getLogradouro() +"," +
                convite.getPartida().getEndereco().getNumero() + " - " +
                convite.getPartida().getEndereco().getBairro() + " - " +
                convite.getPartida().getEndereco().getCidade() + " / " +
                metodosPublicos.GetSiglaEstado(convite.getPartida().getEndereco().getEstado());

        int id = 1;
        String TITULO = "Resposta do Convite Quero Apitar";

        String nome_convidado =  convite.getConvidado().getNome().substring(0,1).toUpperCase() + convite.getConvidado().getNome().substring(1);
        String sobrenome_convidado =  convite.getConvidado().getSobreNome().substring(0,1).toUpperCase() + convite.getConvidado().getSobreNome().substring(1);

        String convite_aceito = nome_convidado + " " + sobrenome_convidado + " aceitou o convite para apitar a partida de " + esporte +
                ", na data de: " + data +
                ", no endereço: " + endereco;

        String convite_nao_aceito = nome_convidado + " " + sobrenome_convidado + " não aceitou seu convite para apitar a partida de "  + esporte +
                ", na data de: " + data +
                ", no endereço: " + endereco;

        String DESCRICAO =  "";

        if(convite.getStatus() == 1){
            DESCRICAO = convite_aceito;
        }else if(convite.getStatus() == 3){
            DESCRICAO = convite_nao_aceito;
        }

        int SMALL_ICONE = R.drawable.logo4;
        int LARGE_ICONE = R.drawable.logo4;

        Intent intent = new Intent(activity, ConvitesJogadorActivity.class);
        intent.putExtra("convite",convite);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(activity,App.getContext().getString(R.string.msg_notificacao));
        notificacao.setContentText(DESCRICAO);
        notificacao.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(DESCRICAO));
        notificacao.setContentTitle(TITULO);
        notificacao.setSmallIcon(SMALL_ICONE);
        notificacao.setLargeIcon(BitmapFactory.decodeResource(App.getContext().getResources(), LARGE_ICONE) );
        notificacao.setContentIntent(pendingIntent);
        notificacao.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificacao.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) App.getContext().getSystemService(activity.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificacao.setChannelId(NOTIFICATION_CHANNEL_ID);

            nm.createNotificationChannel(notificationChannel);
        }


        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP );


        nm.notify(id, notificacao.build());
    }


    private void NotificacaoArbitroSeOferece(Convite convite, Activity activity){

        App.setContext(activity);
        final String NOTIFICATION_CHANNEL_ID = "10001";

        String esporte = convite.getPartida().getEsporte().getNome();
        String data = metodosPublicos.FormataData(convite.getPartida().getDataDaPartida()) + " " + convite.getPartida().getHoraDaPartida();
        String endereco = convite.getPartida().getEndereco().getLogradouro() +"," +
                convite.getPartida().getEndereco().getNumero() + " - " +
                convite.getPartida().getEndereco().getBairro() + " - " +
                convite.getPartida().getEndereco().getCidade() + " / " +
                metodosPublicos.GetSiglaEstado(convite.getPartida().getEndereco().getEstado());

        int id = 1;
        String TITULO = "Quero Apitar";

        String nome_convidado =  convite.getConvidado().getNome().substring(0,1).toUpperCase() + convite.getConvidado().getNome().substring(1);
        String sobrenome_convidado =  convite.getConvidado().getSobreNome().substring(0,1).toUpperCase() + convite.getConvidado().getSobreNome().substring(1);

        String DESCRICAO =  nome_convidado + " " + sobrenome_convidado + ", quero apitar estar partida." + esporte +
                ", na data de: " + data +
                ", no endereço: " + endereco;

        int SMALL_ICONE = R.drawable.logo4;
        int LARGE_ICONE = R.drawable.logo4;

        Intent intent = new Intent(activity, ConvitesJogadorActivity.class);
        intent.putExtra("convite",convite);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(activity,App.getContext().getString(R.string.msg_notificacao));
        notificacao.setContentText(DESCRICAO);
        notificacao.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(DESCRICAO));
        notificacao.setContentTitle(TITULO);
        notificacao.setSmallIcon(SMALL_ICONE);
        notificacao.setLargeIcon(BitmapFactory.decodeResource(App.getContext().getResources(), LARGE_ICONE) );
        notificacao.setContentIntent(pendingIntent);
        notificacao.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificacao.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) App.getContext().getSystemService(activity.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificacao.setChannelId(NOTIFICATION_CHANNEL_ID);

            nm.createNotificationChannel(notificationChannel);
        }


        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP );


        nm.notify(id, notificacao.build());
    }

    private void NotificacaoArbitroRecebeAvaliacao(Convite convite, Activity activity){

        App.setContext(activity);
        final String NOTIFICATION_CHANNEL_ID = "10001";

        String esporte = convite.getPartida().getEsporte().getNome();
        String data = metodosPublicos.FormataData(convite.getPartida().getDataDaPartida()) + " " + convite.getPartida().getHoraDaPartida();
        String endereco = convite.getPartida().getEndereco().getLogradouro() +"," +
                convite.getPartida().getEndereco().getNumero() + " - " +
                convite.getPartida().getEndereco().getBairro() + " - " +
                convite.getPartida().getEndereco().getCidade() + " / " +
                metodosPublicos.GetSiglaEstado(convite.getPartida().getEndereco().getEstado());

        int id = 1;
        String TITULO = "Avaliação - Quero Apitar";

        String nome_convidado =  convite.getConvidado().getNome().substring(0,1).toUpperCase() + convite.getConvidado().getNome().substring(1);
        String sobrenome_convidado =  convite.getConvidado().getSobreNome().substring(0,1).toUpperCase() + convite.getConvidado().getSobreNome().substring(1);

        String DESCRICAO =  nome_convidado + " " + sobrenome_convidado + ", sua Avaliação da partida de " + esporte +
                ", na data de: " + data +
                ", no endereço: " + endereco +
                ", foi: " + convite.getPartida().getAvaliacaoArbitro();

        int SMALL_ICONE = R.drawable.logo4;
        int LARGE_ICONE = R.drawable.logo4;

        Intent intent = new Intent(activity, ConvitesArbitroActivity.class);
        intent.putExtra("convite",convite);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(activity,App.getContext().getString(R.string.msg_notificacao));
        notificacao.setContentText(DESCRICAO);
        notificacao.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(DESCRICAO));
        notificacao.setContentTitle(TITULO);
        notificacao.setSmallIcon(SMALL_ICONE);
        notificacao.setLargeIcon(BitmapFactory.decodeResource(App.getContext().getResources(), LARGE_ICONE) );
        notificacao.setContentIntent(pendingIntent);
        notificacao.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificacao.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) App.getContext().getSystemService(activity.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificacao.setChannelId(NOTIFICATION_CHANNEL_ID);

            nm.createNotificationChannel(notificationChannel);
        }


        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP );


        nm.notify(id, notificacao.build());
    }

}
