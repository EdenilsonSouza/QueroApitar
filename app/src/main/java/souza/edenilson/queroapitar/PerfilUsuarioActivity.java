package souza.edenilson.queroapitar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.common.base.MoreObjects;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.compiler.PluginProtos;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import souza.edenilson.DataBase.UsuarioDataBase;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.MetodosPublicos.PermissoesAcesso;
import souza.edenilson.Model.Endereco;
import souza.edenilson.Model.Usuario;

public class PerfilUsuarioActivity extends AppCompatActivity implements
        com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static String TITULO = "Perfil";
    private static String TITULO_ALTERAR = "Editar Partida";
    public static final int GET_FROM_GALLERY = 3;
    //MAPA
    int AUTOCOMPLETE_REQUEST_CODE = 001;
    private static final int UPDATE_INTERVAL = 60000;
    private static final int FASTEST_INTERVAL = 10000;
    private static final int DISPLACEMENT = 10;

    private static final int PERMISSION_REQUEST_CODE = 7001;
    private static final int PLAY_SERVICE_REQUEST = 7002;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    // CAMPOS DA TELA
    EditText editPesquisaEndereco;
    TextView txtNomeUsuario;
    TextView txtEmailUsuario;
    TextView txtTipoUsuario;
    TextView txtDataNascimento;
    TextView txtGenero;
    TextView txtEndereco;
    TextView txtRangeDistancia;
    SeekBar simpleSeekBar;
    ImageView imgFotoUsuario;
    Toolbar toolbarCadastro;
    private RadioGroup radioGroupGenero;
    private RadioButton radioMasculino, radioFeminino;
    private int year, month, day, hour, minute, segundos;


    // OBJETOS
    Usuario usuarioLogado;
    Endereco endereco;
    UsuarioDataBase usuarioDataBase;
    MetodosPublicos metodosPublicos;
    PermissoesAcesso permissoesAcesso;

    //MAPA
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;

    //FIREBASE
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        App.setContext(this);
        toolbarCadastro = findViewById(R.id.toolbarPerfilActivity);
        toolbarCadastro.setTitle(TITULO);
        setSupportActionBar(toolbarCadastro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão

        metodosPublicos = new MetodosPublicos();
        metodosPublicos.TemInternet(App.getContext(), PerfilUsuarioActivity.this, LoginActivity.class);

        Intent intent = getIntent();
        usuarioLogado =  (Usuario)intent.getSerializableExtra("usuarioLogado");

        if(usuarioLogado == null){
            Intent i = new Intent(PerfilUsuarioActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        InicializaObjetos();
       // permissoesAcesso.PermissaoGaleria(this);
        InicializaCampos();
        InicializaFirebase();
       // ConfiguraSeekBarDistancia();
        ConfiguraRadioGroup();
        ConfiguraMapa();

        if(usuarioLogado != null){
            PreencheCamposLoad();
        }

        if (checkPlayServices()) {

            String API_KEY = metodosPublicos.GetChaveGoogle();

            if(API_KEY != null){
                // Initialize Places.
                Places.initialize(getApplicationContext(), API_KEY);
            }


            InicializaLocationServices();
          //  buildGoogleApiClient();
            createLocationRequest();
       }
    }

    @Override
    protected void onStart() {
        metodosPublicos.TemInternet(App.getContext(), PerfilUsuarioActivity.this, LoginActivity.class );
        super.onStart();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onResume() {
        super.onResume();
        metodosPublicos.TemInternet(App.getContext(), PerfilUsuarioActivity.this, LoginActivity.class );
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        int itemId = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("usuarioLogado", usuarioLogado);
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
      metodosPublicos.AbreModalSairAPP(this);
    }

    private void InicializaFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("usuario");
    }

    private void InicializaCampos(){

        imgFotoUsuario = (ImageView) findViewById(R.id.imgFotoUsuario);
        imgFotoUsuario.setVisibility(View.VISIBLE);
        txtEmailUsuario = (TextView) findViewById(R.id.txtEmailUsuario);
        txtNomeUsuario = (TextView) findViewById(R.id.txtNomeUsuario);
        txtTipoUsuario = (TextView) findViewById(R.id.txtTipoPerfilUsuario);
        txtDataNascimento = (TextView) findViewById(R.id.txtDataNascimento);
        txtGenero = (TextView) findViewById(R.id.txtDisplayGenero);
        editPesquisaEndereco = (EditText) findViewById(R.id.places_autocomplete_search_input);
        txtEndereco = (TextView) findViewById(R.id.txt_endereco_perfil);
        //txtRangeDistancia = (TextView)findViewById(R.id.txt_range_distancia_partidas);
      //  simpleSeekBar = (SeekBar) findViewById(R.id.seekBar_distancia_partidas); // initiate the Seek bar
        radioGroupGenero = (RadioGroup) findViewById(R.id.radioGroupGenero);
        radioMasculino = (RadioButton) findViewById(R.id.radioMasculino);
        radioFeminino = (RadioButton) findViewById(R.id.radioFeminino);
        radioMasculino.setChecked(false);
        radioFeminino.setChecked(false);
    }

    private void ConfiguraRadioGroup(){

        radioGroupGenero.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radioMasculino) {
                    radioMasculino.setChecked(true);
                    usuarioLogado.setGenero("m");
                } else if(checkedId == R.id.radioFeminino) {
                    radioFeminino.setChecked(true);
                    usuarioLogado.setGenero("f");
                }
                usuarioDataBase.Atualizar(usuarioLogado);
            }

        });
    }

    private void InicializaObjetos(){
        endereco = new Endereco();
        usuarioDataBase = new UsuarioDataBase();
        permissoesAcesso = new PermissoesAcesso();
    }

    private void PreencheCamposLoad(){

        String tipo = "";
        if(usuarioLogado.getTipoDeUsuario() == 1){
            tipo = "Árbitro";
        }else{
            tipo = "Jogador";
        }

        txtTipoUsuario.setText(tipo);
        txtNomeUsuario.setText(usuarioLogado.getNome() + " "+ usuarioLogado.getSobreNome());
        txtEmailUsuario.setText(usuarioLogado.getEmail());

        if(usuarioLogado.getDataDeNascimento() == null || usuarioLogado.getDataDeNascimento() ==  ""){
            txtDataNascimento.setText("Não informada");
        }else{
            txtDataNascimento.setText(metodosPublicos.FormataData(usuarioLogado.getDataDeNascimento()));
        }

        if(usuarioLogado.getGenero().equals("m")){
            radioMasculino.setChecked(true);
        }else if(usuarioLogado.getGenero().equals("f")){
            radioFeminino.setChecked(true);
        }

        if(usuarioLogado.getEndereco() == null){
            txtEndereco.setText("Endereço: não informado");
        }else{
            String enderecoCompleto = usuarioLogado.getEndereco().getLogradouro() + "," + usuarioLogado.getEndereco().getNumero() + " - " + usuarioLogado.getEndereco().getBairro() +
                    " - " + usuarioLogado.getEndereco().getCidade() + "/" + metodosPublicos.GetSiglaEstado(usuarioLogado.getEndereco().getEstado());
            txtEndereco.setText("Endereço: " + enderecoCompleto);
        }

        /*
        if(usuarioLogado.getDistanciaDisponivel() > 0){
            txtRangeDistancia.setText("Distância escolhida: " + String.valueOf(usuarioLogado.getDistanciaDisponivel()) + " km");
            simpleSeekBar.setProgress(usuarioLogado.getDistanciaDisponivel());
        }else{
            txtRangeDistancia.setText("Distância não informada: 0");
            simpleSeekBar.setProgress(0);
        }
*/
        metodosPublicos.CarregaImageView(imgFotoUsuario,usuarioLogado.getUrlFoto());
    }

    public void UploadFotoPerfil(View view) {

        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    private void SalvarImagemFirebase(Bitmap bitmap, String nome_arquivo){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageUsuarioRef = storage.getReference("imagem/"+nome_arquivo);

        //bitmap = ((BitmapDrawable) imgFotoUsuario.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageUsuarioRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(PerfilUsuarioActivity.this, "Falha no upload " + exception.getMessage(), Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // While the file names are the same, the references point to different files
                //storageUsuarioRef.getName().equals(mountainImagesRef.getName());
               // Toast.makeText(PerfilUsuarioActivity.this, "Upload FEITO", Toast.LENGTH_LONG).show();
                Intent i = new Intent(PerfilUsuarioActivity.this, PerfilUsuarioActivity.class);

                return;
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            String picturePath = cursor.getString(columnIndex);

            cursor.close();

            String [] imageParts = picturePath.split("/");
            String nome_arquivo = null;
            if(imageParts[4].contains(".jpg") || imageParts[4].contains(".png")){

                nome_arquivo = imageParts[4];

            }else if(imageParts[5].contains(".jpg") || imageParts[5].contains(".png")){

                nome_arquivo = imageParts[5];

            }else if(imageParts[7].contains(".jpg") || imageParts[7].contains(".png")){

                nome_arquivo = imageParts[7];

            }

            String orientacao = null;
            try {
                ExifInterface exif = new ExifInterface(picturePath);
                orientacao = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imgFotoUsuario.setMaxWidth(300);
            imgFotoUsuario.setMaxHeight(150);
            imgFotoUsuario.setMinimumWidth(300);
            imgFotoUsuario.setMinimumHeight(150);
            imgFotoUsuario.setRotation(metodosPublicos.RotacaoImagem(orientacao));

            Bitmap bitmap = BitmapFactory.decodeFile(new File(picturePath).getPath());
            if(bitmap != null){

                SalvarImagemFirebase(bitmap, usuarioLogado.getID() + "_" + nome_arquivo);
            }

            imgFotoUsuario.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            usuarioLogado.setUrlFoto(picturePath);

            usuarioDataBase.Atualizar(usuarioLogado);

        }

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                if (place != null) {
                    mLocation = new Location(place.getAddress());

                    double lat = place.getLatLng().latitude;
                    double lng = place.getLatLng().longitude;

                    List<Address> addresses = null;

                    Geocoder geocoder = new Geocoder(PerfilUsuarioActivity.this, Locale.getDefault());

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
                    String idEndereco =  String.valueOf(new Date().getTime()) +  endereco.getCEP()  + endereco.getNumero();
                    endereco.setID(idEndereco);

                    enderecoCompleto = enderecoCompleto.replace("null,", "");

                    txtEndereco.setText(enderecoCompleto);

                    usuarioLogado.setEndereco(endereco);
                    usuarioDataBase.Atualizar(usuarioLogado);
                  //  CriaCirculoMapa(lat, lng);

                  //  String displayEndereco = enderecoCompleto == null || enderecoCompleto == "" ? "Posição atual" : enderecoCompleto;
                 //   CriarMarker(displayEndereco, lat, lng, true);

                }

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);

                Toast.makeText(PerfilUsuarioActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void AdicionaDataNascimento_click(View view) {
        AdicionaDataDeNascimento();
    }

    private void AdicionaDataDeNascimento() {
        InicializaData();
        Calendar cdDefault = Calendar.getInstance();
        cdDefault.set(year, month, day);

        com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                this,
                cdDefault.get(Calendar.YEAR),
                cdDefault.get(Calendar.MONTH),
                cdDefault.get(Calendar.DAY_OF_MONTH)
        );
      //  datePickerDialog.setMinDate(cdDefault);
        datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
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

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int month = monthOfYear + 1;
        String dataSelecionada = dayOfMonth + "/" + month + "/" + year;
        String dataBancoDeDados = dayOfMonth + "-" + month + "-" + year;

        String dia = Integer.toString(dayOfMonth);
        String mes = metodosPublicos.GetAbreviaturaMes(month).toUpperCase();
        String ano = Integer.toString(year);

        txtDataNascimento.setText(dataSelecionada);

        usuarioLogado.setDataDeNascimento(dataBancoDeDados);
        usuarioDataBase.Atualizar(usuarioLogado);
    }

    // MAPA
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

    private void ConfiguraMapa() {
/*
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        if (editPesquisaEndereco != null) {
            editPesquisaEndereco.setHint("Informe o endereço");
            editPesquisaEndereco.setTextSize(12);
        }

        // FAZ ABRIR O CAMPO DE PESQUISA DO MAPA
        editPesquisaEndereco.setOnClickListener(new View.OnClickListener() {
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

    private void createLocationRequest() {
        // mLocationRequest = new LocationRequest();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
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
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
