package souza.edenilson.queroapitar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.model.PlusCode;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.google.android.gms.location.LocationListener;

import java.io.IOException;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.google.android.gms.maps.*;

import souza.edenilson.DataBase.EnderecoDataBase;
import souza.edenilson.DataBase.PartidaDataBase;
import souza.edenilson.DataBase.UsuarioDataBase;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.Model.Endereco;
import souza.edenilson.Model.Esporte;
import souza.edenilson.Model.Partida;
import souza.edenilson.Model.Usuario;

import static androidx.constraintlayout.solver.widgets.WidgetContainer.getBounds;
import static com.google.android.gms.stats.CodePackage.DRIVE;
import static com.google.android.libraries.places.api.model.TypeFilter.CITIES;
import static com.google.android.libraries.places.api.model.TypeFilter.GEOCODE;

public class PartidaActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener,
        OnMapReadyCallback,
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        DialogInterface.OnCancelListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static String TITULO = "Cadastro de Partida";
    private static String TITULO_ALTERAR = "Editar Partida";
    int AUTOCOMPLETE_REQUEST_CODE = 001;


    //FIREBASE
    private FirebaseAuth mAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser currentUser;
    DatabaseReference databaseReferenceEndereco;
    DatabaseReference databaseReferencePartida;
    DatabaseReference databaseReferenceUsuario;
    FirebaseDatabase firebaseDatabase;

    //CAMPOS DA TELA
    private int year, month, day, hour, minute, segundos;
    Button btnAbreDatePicker;
    TextView txtDiaDataPartida;
    TextView txtMesDataPartida;
    TextView txtAnoDataPartida;
    TextView txtDisplayHoraPartida;
    TextView txtDisplayUsuarioDonoPartida;

    Spinner spinnerEsporte;


    // MAPA
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    RelativeLayout relativeLayoutMapa;

    // DIALOG de permissoes
    //private MaterialDialog mMaterialDialog;
    public static final int REQUEST_PERMISSIONS_CODE = 128;

    private static final int PERMISSION_REQUEST_CODE = 7001;
    private static final int PLAY_SERVICE_REQUEST = 7002;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final int UPDATE_INTERVAL = 60000;
    private static final int FASTEST_INTERVAL = 10000;
    private static final int DISPLACEMENT = 10;

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    EditText txtEndereco;
    // FECHA MAPA

    // OBJETOS de Classes
    Usuario usuario;
    Partida partida;
    Endereco endereco;

    UsuarioDataBase usuarioDataBase;
    EnderecoDataBase enderecoDataBase;
    PartidaDataBase partidaDataBase;
    MetodosPublicos metodosPublicos;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setContentView(R.layout.activity_cadastro_partida);

        App.setContext(this);
        Toolbar toolbarCadastro = findViewById(R.id.toolbarSalvar);
        toolbarCadastro.setTitle(TITULO);
        setSupportActionBar(toolbarCadastro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle("Cadastro de Partida");

        metodosPublicos = new MetodosPublicos();
        metodosPublicos.TemInternet(App.getContext(), PartidaActivity.this, LoginActivity.class);

        // vem das telas de Login e Cadastro de Usuario
        Intent intent = getIntent();
        usuario = (Usuario) intent.getSerializableExtra("usuarioLogado");

        InicializaFirebase();
        InicializaObjetos();
        InicializaCampos();
        LoadPreencheCampos();

        if (checkPlayServices()) {

            String API_KEY = metodosPublicos.GetChaveGoogle();

            // Initialize Places.
            Places.initialize(getApplicationContext(), API_KEY);

            InicializaLocationServices();
            buildGoogleApiClient();
            createLocationRequest();
        }

        // CONFIGURA o MAPA
        ConfiguraMapa();
    }


    @Override
    protected void onStart() {
        metodosPublicos.TemInternet(App.getContext(), PartidaActivity.this, LoginActivity.class );
        super.onStart();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onResume() {
        super.onResume();
        metodosPublicos.TemInternet(App.getContext(), PartidaActivity.this, LoginActivity.class );
        // CONFIGURA OS CAMPOS DE DATA E HORA
        ConfigureDateTimePicker();

    }

    private void InicializaObjetos(){
        metodosPublicos = new MetodosPublicos();
        endereco = new Endereco();
        partida = new Partida();
        usuarioDataBase = new UsuarioDataBase();
        enderecoDataBase = new EnderecoDataBase();
        partidaDataBase = new PartidaDataBase();
    }

    private void InicializaFirebase(){
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceEndereco = firebaseDatabase.getReference("endereco");
        databaseReferencePartida = firebaseDatabase.getReference("partida");
        databaseReferenceUsuario = firebaseDatabase.getReference("usuario");

    }

    private void InicializaCampos() {

        btnAbreDatePicker = (Button)findViewById(R.id.btnAbreDatePicker);

        txtDiaDataPartida = (TextView) findViewById(R.id.txtDiaDataPartida);
        txtMesDataPartida = (TextView) findViewById(R.id.txtMesDataPartida);
        txtAnoDataPartida = (TextView) findViewById(R.id.txtAnoDataPartida);

        txtDisplayHoraPartida = (TextView) findViewById(R.id.txtDisplayHoraPartida);
        txtDisplayUsuarioDonoPartida = (TextView) findViewById(R.id.txtDisplayUsuarioDonoPartida);

        txtEndereco = (EditText) findViewById(R.id.places_autocomplete_search_input);

        spinnerEsporte = (Spinner) findViewById(R.id.spinner_esporte);

    }

    private void LoadPreencheCampos() {

        if (txtDisplayUsuarioDonoPartida != null) {
            txtDisplayUsuarioDonoPartida.setText(usuario.getNome().toUpperCase() + " " + usuario.getSobreNome().toUpperCase());
        }

        PreencheComboboxEsportes(metodosPublicos.GetEsporteColetivo());
        CarregaDataAtual();
    }

    private void InicializaLocationServices() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Instantiating the GoogleApiClient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    private void CarregaDataAtual(){

        Calendar cal = Calendar.getInstance();
        Calendar c = Calendar.getInstance();

        String year = Integer.toString(c.get(Calendar.YEAR));
        int month =  c.get(Calendar.MONTH);
        String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        String minute = Integer.toString(c.get(Calendar.MINUTE));

        cal.set(Calendar.DAY_OF_MONTH, (int) (c.getTimeInMillis() - 1));

        String mes = Integer.toString(month + 1);
        txtDiaDataPartida.setText(day);
        txtMesDataPartida.setText(metodosPublicos.GetAbreviaturaMes(Integer.parseInt(mes)));
        txtAnoDataPartida.setText(year);

        String hora = hour + ":" + minute;
        txtDisplayHoraPartida.setText(hora);

    }

    private void ConfiguraMapa() {

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (txtEndereco != null) {
            txtEndereco.setHint("Informe o endereço");
            txtEndereco.setTextSize(12);
        }

        // FAZ ABRIR O CAMPO DE PESQUISA DO MAPA
        txtEndereco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // faz a chamada do FILTRO somente quando clica no campo de pesquisa
                List<Place.Field> placeFields =
                        Arrays.asList(Place.Field.ID,
                                Place.Field.NAME,
                                Place.Field.PLUS_CODE,
                                Place.Field.LAT_LNG,
                                Place.Field.ADDRESS,
                                Place.Field.ADDRESS_COMPONENTS,
                                Place.Field.PRICE_LEVEL,
                                Place.Field.RATING,
                                Place.Field.ID);

                Intent intent = new Autocomplete.IntentBuilder(
                        AutocompleteActivityMode.OVERLAY, placeFields)
                        .setCountry("BR")
                        .setHint("Pesquise o endereço.")
                        .setTypeFilter(TypeFilter.ADDRESS)
                       /* .setTypeFilter(TypeFilter.REGIONS)
                        .setTypeFilter(TypeFilter.CITIES)
                        .setTypeFilter(TypeFilter.GEOCODE)
                        .setTypeFilter(TypeFilter.ESTABLISHMENT)*/
                        .build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                if (place != null) {
                    mLocation = new Location(place.getAddress());

                    double lat = place.getLatLng().latitude;
                    double lng = place.getLatLng().longitude;

                    List<Address> addresses = null;

                    Geocoder geocoder = new Geocoder(PartidaActivity.this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String enderecoCompleto = null;

                    String numero = addresses.get(0).getFeatureName();
                    String rua = addresses.get(0).getThoroughfare();
                    String bairro = addresses.get(0).getSubLocality();
                    String cidade = addresses.get(0).getLocality() == null ? addresses.get(0).getSubAdminArea() : addresses.get(0).getLocality();
                    String estado = addresses.get(0).getAdminArea();
                    String pais = addresses.get(0).getCountryName();
                    String cep = addresses.get(0).getPostalCode();
                    enderecoCompleto = rua + ", " + numero + " - " + bairro + ", " + cidade + " - " + metodosPublicos.GetSiglaEstado(estado) + ", " + pais;

                    //Seta os valores para o Objeto ENDERECO
                    endereco.setNumero(numero);
                    endereco.setLogradouro(rua);
                    endereco.setCidade(cidade);
                    endereco.setEstado(estado);
                    endereco.setBairro(bairro);
                    endereco.setCEP(cep);
                    endereco.setLatitude(lat);
                    endereco.setLongitude(lng);

                    enderecoCompleto = enderecoCompleto.replace("null,", "");
                    CriaCirculoMapa(lat, lng);

                    String displayEndereco = enderecoCompleto == null || enderecoCompleto == "" ? "Posição atual" : enderecoCompleto;
                    CriarMarker(displayEndereco, lat, lng, true);
                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);

                Toast.makeText(PartidaActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (checkPlayServices()) {
                        InicializaLocationServices();
                        buildGoogleApiClient();
                        createLocationRequest();
                        GetLastLocation();
                    }

                } else {

                    // readMyCurrentCoordinates();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void createLocationRequest() {
       // mLocationRequest = new LocationRequest();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void buildGoogleApiClient() {

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            mGoogleApiClient.connect();
    }

    private boolean checkPlayServices() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();

        int resultCode = googleAPI.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {

            if (googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode, PLAY_SERVICE_REQUEST).show();
                googleAPI.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(this, "O dispositivo não tem permissão para acesso ao maps.", Toast.LENGTH_LONG).show();
                finish();
            }

            return false;
        }

        return true;
    }

    private void GetEnderecoUsuarioLogado(Usuario usuario){

        double latitude = usuario.getEndereco().getLatitude();
        double longitude = usuario.getEndereco().getLongitude();

        List<Address> addresses = null;

        Geocoder geocoder = new Geocoder(PartidaActivity.this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String enderecoCompleto = null;

        String numero = addresses.get(0).getFeatureName();
        String rua = addresses.get(0).getThoroughfare();
        String bairro = addresses.get(0).getSubLocality();
        String cidade = addresses.get(0).getLocality() == null ? addresses.get(0).getSubAdminArea() : addresses.get(0).getLocality();
        String estado = addresses.get(0).getAdminArea();
        String pais = addresses.get(0).getCountryName();
        String cep = addresses.get(0).getPostalCode();
        enderecoCompleto = rua + ", " + numero + " - " + bairro + ", " + cidade + " - " + metodosPublicos.GetSiglaEstado(estado) + ", " + pais;

        //Seta os valores para o Objeto ENDERECO
        endereco.setNumero(numero);
        endereco.setLogradouro(rua);
        endereco.setCidade(cidade);
        endereco.setEstado(estado);
        endereco.setBairro(bairro);
        endereco.setCEP(cep);
        endereco.setLatitude(latitude);
        endereco.setLongitude(longitude);

        enderecoCompleto = enderecoCompleto.replace("null,", "");
        CriaCirculoMapa(latitude, longitude);

        String displayEndereco = enderecoCompleto == null || enderecoCompleto == "" ? "Posição atual" : enderecoCompleto;
        CriarMarker(displayEndereco, latitude, longitude, true);
    }

    private void GetLastLocation() {

        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSIONS_CODE);

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSIONS_CODE);
            }
        } else {
            // Permission has already been granted
        }


        Task<Location> task = mFusedLocationClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                        mLocation = location;

                        if (mLocation != null) {

                            double lat = mLocation.getLatitude();
                            double lng = mLocation.getLongitude();

                            List<Address> addresses = null;

                            Geocoder geocoder = new Geocoder(PartidaActivity.this, Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(lat, lng, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            String enderecoCompleto = null;

                            String numero = addresses.get(0).getFeatureName();
                            String rua = addresses.get(0).getThoroughfare();
                            String bairro = addresses.get(0).getSubLocality();
                            String cidade = addresses.get(0).getLocality() == null ? addresses.get(0).getSubAdminArea() : addresses.get(0).getLocality();
                            String estado = addresses.get(0).getAdminArea();
                            String pais = addresses.get(0).getCountryName();
                            String cep = addresses.get(0).getPostalCode();
                            enderecoCompleto = rua + ", " + numero + " - " + bairro + ", " + cidade + " - " + metodosPublicos.GetSiglaEstado(estado) + ", " + pais;

                            //Seta os valores para o Objeto ENDERECO
                            endereco.setNumero(numero);
                            endereco.setLogradouro(rua);
                            endereco.setCidade(cidade);
                            endereco.setEstado(estado);
                            endereco.setBairro(bairro);
                            endereco.setCEP(cep);
                            endereco.setLatitude(lat);
                            endereco.setLongitude(lng);

                            enderecoCompleto = enderecoCompleto.replace("null,", "");
                            CriaCirculoMapa(lat, lng);

                            String displayEndereco = enderecoCompleto == null || enderecoCompleto == "" ? "Posição atual" : enderecoCompleto;
                            CriarMarker(displayEndereco, lat, lng, true);

                        }
                    }
            }
        });

    }


    // Configuração do MAPA
    public void CriaCirculoMapa(Double latitude, Double longitude) {

        Circle mCircle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(150)
                .strokeWidth(1)
                .strokeColor(Color.parseColor("#87CEFA"))
                .fillColor(Color.parseColor("#2271cce7")));

    }

    private void CriarMarker(String descricaoEndereco, double latitude, double longitude, boolean mostrarMarcador) {
        String descricao;
        if (descricaoEndereco == null || descricaoEndereco == "") {
            descricao = "Meu endereço.";
        } else {
            descricao = descricaoEndereco;
        }

        Marker newMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(descricao).visible(true));
        newMarker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 16.0f));
        if (mostrarMarcador) newMarker.showInfoWindow();
    }


    private boolean ValidaCampos(){

        if(spinnerEsporte.getSelectedItemId() == 0){
            Toast.makeText(this, "Campo Esporte é obrigatório.", Toast.LENGTH_LONG).show();
            spinnerEsporte.requestFocus();
            return false;
        }
        return true;
    }

    // AQUI CONFIGURA OS CAMPOS DE DATA E HORA
    @SuppressLint("ResourceAsColor")
    private void ConfigureDateTimePicker() {

        //Configura DATA
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog datepicker = (com.wdullaer.materialdatetimepicker.date.DatePickerDialog) getSupportFragmentManager().findFragmentByTag("Datepickerdialog");

        if (datepicker != null) {

            datepicker.setThemeDark(true);
            datepicker.setVersion(com.wdullaer.materialdatetimepicker.date.DatePickerDialog.Version.VERSION_2);
            datepicker.setAccentColor(R.color.mdtp_accent_color);
            datepicker.setOnDateSetListener(this);
        }

        // Configura HORA
        TimePickerDialog timepicker = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag("TimepickerDialog");

        if (timepicker != null) {
            timepicker.setVersion(TimePickerDialog.Version.VERSION_2);
            TimePicker tpHourMin = (TimePicker) findViewById(timepicker.getId());
            tpHourMin.setIs24HourView(true);
            timepicker.setOnTimeSetListener(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // callDialog( null, new String[]{Manifest.permission.ACCESS_FINE_LOCATION} );
                ActivityCompat.requestPermissions(PartidaActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_CODE);
            }
            //return;
        }

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap = googleMap;

        // PEGA O ENDEREÇO ATUAL, SE NAO ESTIVER CADASTRADO NO BANCO DE DADOS
        GetLastLocation();


  }


    public void AdicionaDataPartida_click(View view) {
        AdicionaDataDaPartida();
    }

    public void AdicionaHoraPartida_click(View view) {
        AdicionaHoraDaPartida();
    }

    private void AdicionaDataDaPartida() {
        InicializaData();
        Calendar cdDefault = Calendar.getInstance();
        cdDefault.set(year, month, day);

        com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                this,
                cdDefault.get(Calendar.YEAR),
                cdDefault.get(Calendar.MONTH),
                cdDefault.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.setMinDate(cdDefault);
        datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
    }

    private void AdicionaHoraDaPartida() {
        InicializaHora();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hour, minute, segundos);

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                this,
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND),
                true
        );

        timePickerDialog.show(getSupportFragmentManager(), "TimepickerDialog");
    }


    private void InicializaData() {
        if (year == 0) {
            Calendar cal = Calendar.getInstance();
            Calendar c = Calendar.getInstance();

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

            cal.set(Calendar.DAY_OF_MONTH, (int) (c.getTimeInMillis() - 1));
        }
    }

    private void InicializaHora() {
        if (year == 0) {
            Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
            segundos = c.get(Calendar.SECOND);
        }
    }


    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }

    @Override
    public void onDateSet(com.wdullaer.materialdatetimepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        int month = monthOfYear + 1;
        String dataSelecionada = dayOfMonth + "/" + month + "/" + year;

        String dia = Integer.toString(dayOfMonth);
        String mes = metodosPublicos.GetAbreviaturaMes(month).toUpperCase();
        String ano = Integer.toString(year);

        txtDiaDataPartida.setText(dia);
        txtMesDataPartida.setText(mes);
        txtAnoDataPartida.setText(ano);
    }


    @Override
    public void onTimeSet(com.wdullaer.materialdatetimepicker.time.TimePickerDialog view, int hourOfDay, int minute, int second) {

        String min = Integer.toString(minute);
        if (minute < 10) {
            min = "0" + min;
        }

        String horaSelecionada = hourOfDay + ":" + min;
        txtDisplayHoraPartida.setText(horaSelecionada);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        int itemId = item.getItemId();
        switch (item.getItemId()) {
            case R.id.toolbar_btn_salvar:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                Toast.makeText(PartidaActivity.this, "Clicou no botao SALVAR", Toast.LENGTH_LONG).show();
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            case android.R.id.home:
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("usuarioLogado", usuario);
                startActivity(i);
                finishAffinity();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() { //Botão BACK padrão do android

        Intent intent = new Intent(PartidaActivity.this, MainActivity.class);
        intent.putExtra("usuarioLogado", usuario);
        startActivity(intent);
        finishAffinity();
    }

    // Para criar o MENU com ITENS tem que instanciar o getMenuInflater dentro deste metodo,
    // chamando o LAYOUT do MENU_TOOLBAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    public void SalvarPartidaClick(View view) {

        if(ValidaCampos()){
            String dataDaPartida = txtDiaDataPartida.getText().toString() +"-"+ metodosPublicos.ConverteNumeroMES(txtMesDataPartida.getText().toString().toLowerCase())+"-"+txtAnoDataPartida.getText().toString();
            String horaDaPartida = txtDisplayHoraPartida.getText().toString();

            String hora = txtDisplayHoraPartida.getText().toString();
            String[] h = hora.split(":");
            if(h[0].equals("0")){
                horaDaPartida = "0" + txtDisplayHoraPartida.getText().toString();
            }

            databaseReferencePartida = firebaseDatabase.getReference("partida");
            databaseReferenceEndereco = firebaseDatabase.getReference().child("endereco");

            // SALVAO ENDERECO ANTES DE CRIAR A PARTIDA
            if(endereco != null){
                String idEndereco =  String.valueOf(new Date().getTime()) +  endereco.getCEP()  + endereco.getNumero();
                endereco.setID(idEndereco);
                databaseReferenceEndereco.child(idEndereco).setValue(endereco);
            }

            // SALVA A PARTIDA , COM O USUARIO E O ENDERECO CORRETO.
            String idEsporte = String.valueOf(spinnerEsporte.getSelectedItemId());
            String nomeEsporte = spinnerEsporte.getSelectedItem().toString();
            Esporte esporte = new Esporte(idEsporte, nomeEsporte, "0", "");
            boolean partidaAtiva = true;

            Partida partida = new Partida(null, endereco, usuario, esporte, null, dataDaPartida, horaDaPartida, 0, partidaAtiva, 0);
            String idDaPartida = partida.getUsuarioDonoDaPartida().getID() + "_" + partida.getDataDaPartida() + partida.getHoraDaPartida();
            partida.setID(idDaPartida);

            databaseReferencePartida.child(idDaPartida).setValue(partida);

            databaseReferencePartida.child(idDaPartida).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.exists()){
                        Partida partida1 = dataSnapshot.getValue(Partida.class);

                        if(partida.getID().equals(partida1.getID())){

                            Toast.makeText(PartidaActivity.this, "Partida criada com sucesso.", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(PartidaActivity.this, MainActivity.class);
                            intent.putExtra("usuarioLogado", usuario);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(PartidaActivity.this, "Falha ao criar a Partida. " + metodosPublicos.GetMensagemDeErro(databaseError.getMessage()), Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        createLocationRequest();
     //   displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Entrou no evento onConnectionFailed." + metodosPublicos.GetMensagemDeErro(connectionResult.getErrorMessage()), Toast.LENGTH_LONG).show();
    }

    private void PreencheComboboxEsportes(Map<Integer, String> esportes){

        List<String> listaDeEsportes = new ArrayList<>();

        // Adiciona os items no Combo
        listaDeEsportes = new ArrayList<String>();
        listaDeEsportes.add(0,"Selecione um Esporte");

        for (Map.Entry<Integer, String> pair: esportes.entrySet()){
                listaDeEsportes.add(pair.getKey(), pair.getValue());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaDeEsportes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEsporte.setAdapter(dataAdapter);
        spinnerEsporte.requestFocus();

    }
}
