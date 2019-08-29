package souza.edenilson.queroapitar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.Calendar;

public class PartidaActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static String TITULO_ORIGINAL = "Cadastro de Partida";

    //CAMPOS DA TELA
    DatePickerDialog picker;
    EditText txtCampoData;
    Button btnGet;
    TextView tvw;
    private TextView displayData;
    private TextView displayDataHidden;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


    // MAPA
    // MAPA
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    RelativeLayout relativeLayoutMapa;

    // DIALOG de permissoes
    //private MaterialDialog mMaterialDialog;
    public static final int REQUEST_PERMISSIONS_CODE = 128;

    private static final int PERMISSION_REQUEST_CODE = 7001;
    private static final int PLAY_SERVICE_REQUEST = 7002;

    private static final int UPDATE_INTERVAL = 7000;
    private static final int FASTEST_INTERVAL = 3000;
    private static final int DISPLACEMENT = 10;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    private PlaceAutocompleteFragment placeAutocompleteFragment;

    Marker marker;

    Place place;

    LatLng latLong;
    LatLng meuEnderecoLatLong;

    EditText txtEndereco;
    // FECHA MAPA


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partida);

        App.setContext(this);

        InicializaCampos();
        ConfiguraCampoDataPartida();


        txtCampoData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(PartidaActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                txtCampoData.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        btnGet=(Button)findViewById(R.id.button1);
        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvw.setText("Selected Date: "+ txtCampoData.getText());
            }
        });

    }

    private void InicializaCampos(){
        tvw=(TextView)findViewById(R.id.textView1);
        txtCampoData=(EditText) findViewById(R.id.txtDate);
        txtCampoData.setInputType(InputType.TYPE_NULL);

        displayData = (TextView) findViewById(R.id.txtData);
        displayDataHidden = (TextView) findViewById(R.id.txtDataHidden);
    }

    private void ConfiguraCampoDataPartida(){
        displayData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int year = 0;
                int month = 0;
                int day = 0;

              /*  if (operacao != null) {

                    String[] data = operacao.getData().split("/");
                    year = Integer.parseInt(data[2]);
                    month = Integer.parseInt(data[1]) - 1;
                    day = Integer.parseInt(data[0]);
*/
              //  } else {
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);
               // }

                DatePickerDialog dialog = new DatePickerDialog(
                        PartidaActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day
                );

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        // aqui eu pego o valor atribuo o retorno no campo displayData
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                displayData.setText(date);
                // exibe a label acima da data selecionada
                displayDataHidden.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}
