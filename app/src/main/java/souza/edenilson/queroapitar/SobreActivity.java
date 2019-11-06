package souza.edenilson.queroapitar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class SobreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        TextView txtVersaoApp = (TextView) findViewById(R.id.txtVersaoApp);
        TextView txtLogoLicensa = (TextView) findViewById(R.id.txtLogoLicensa);
        TextView txtTextoLicensa = (TextView) findViewById(R.id.txtTextoLicensa);

        String versao = "Versão 004.19";
        String anoLicensa =  " 2019";
        String textoLicensa =  " -  Desenvolvido por Edenilson Souza";

        if(txtVersaoApp != null){
            txtVersaoApp.setText(versao);
        }

        if(txtLogoLicensa != null){
            txtLogoLicensa.setText(anoLicensa);
        }

        if(txtTextoLicensa != null){
            txtTextoLicensa.setText(textoLicensa);
        }



    }
}
