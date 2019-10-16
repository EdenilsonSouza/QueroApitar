package souza.edenilson.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import souza.edenilson.Receiver.InternetReceiver;
import souza.edenilson.queroapitar.App;
import souza.edenilson.queroapitar.R;

public class AccessInternetService extends Service implements Runnable{

    InternetReceiver receiverInternet;
    View view;
    private Handler handler = new Handler();
    long TIMER = 1500;
    String MENSAGEM = "Sem conexão de internet, verifique o sinal.";

    public AccessInternetService(){}

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        App.setContext(this);

        LayoutInflater inflater = (LayoutInflater) App.getContext().getSystemService(App.getContext().LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_tost_message, null);

        receiverInternet = new InternetReceiver();
        handler.postDelayed(this, TIMER);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i("ServiceInternet","Inicio o serviço do Sinal de Internet");
        handler.postDelayed(this, TIMER);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void isConectado(){
        if(!receiverInternet.verificaInternet(App.getContext())){

            TextView texto = (TextView) view.findViewById(R.id.msg_erro);
            Toast toast = Toast.makeText(this, MENSAGEM, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP , 0, 0);
            texto.setText(MENSAGEM);
            toast.setView(view);
            toast.show();
        }
    }

    public boolean Conectado(){
        if(!receiverInternet.verificaInternet(App.getContext())){

            TextView texto = (TextView) view.findViewById(R.id.msg_erro);
            Toast toast = Toast.makeText(this, MENSAGEM, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP , 0, 0);
            texto.setText(MENSAGEM);
            toast.setView(view);
            toast.show();
            return false;
        }
        return true;
    }


    @Override
    public void run() {
        isConectado();
    }
}
