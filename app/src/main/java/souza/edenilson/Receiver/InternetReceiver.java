package souza.edenilson.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetReceiver extends BroadcastReceiver {

    public InternetReceiver(){}

    @Override
    public void onReceive(Context context, Intent intent) {
        verificaInternet(context);
    }

    public boolean verificaInternet(Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isInternetConectado()) {
            return true;
        } else {
            return false;
        }
    }

    public class ServiceManager {

        Context context;

        public ServiceManager(Context base) {
            context = base;
        }

        public boolean isInternetConectado() {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable();
        }

    }
}
