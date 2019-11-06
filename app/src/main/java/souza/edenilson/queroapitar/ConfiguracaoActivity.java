package souza.edenilson.queroapitar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import souza.edenilson.DataBase.UsuarioDataBase;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.MetodosPublicos.MoneyTextWatcher;
import souza.edenilson.MetodosPublicos.PermissoesAcesso;
import souza.edenilson.Model.Esporte;
import souza.edenilson.Model.Usuario;

public class ConfiguracaoActivity extends AppCompatActivity {

    private static String TITULO = "Configurações";

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 2;
    String numeroTelefone = "+555199854-6334";
    String textoSMS = "Boa tarde Edenilson, parabéns você recebeu um convite para apitar o jogo.";

    //objetos
    Usuario usuarioLogado;
    PermissoesAcesso permissoesAcesso;
    MetodosPublicos metodosPublicos;
    UsuarioDataBase usuarioDataBase;

    // COMPONENTES DE TELA
    Toolbar toolbar;
    SeekBar seekbarDistancia;
    TextView txtTipoUsuarioConfig;
    TextView txtNomeUsuarioConfig;
    TextView txtDistanciaEscolida;
    EditText editTextPrimeiroValor;
    EditText editTextSegundoValor;
    EditText editTextTerceiroValor;
    Button btnSalvarEsporte;

    Spinner spinnerPrimeiroEsporte;
    Spinner spinnerSegundoEsporte;
    Spinner spinnerTerceiroEsporte;

    //FIREBASE
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceEsportes;

    List<Esporte> listaDeEsportes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        //Titulo da TELA
        App.setContext(this);
        toolbar = findViewById(R.id.toolbarConfiguracaoActivity);
        toolbar.setTitle(TITULO);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão

        metodosPublicos = new MetodosPublicos();
        metodosPublicos.TemInternet(App.getContext(), ConfiguracaoActivity.this, LoginActivity.class );

        // vem das telas de Login e Cadastro de Usuario
        Intent intent = getIntent();
        usuarioLogado =  (Usuario)intent.getSerializableExtra("usuarioLogado");

        if(usuarioLogado == null){
            Intent i = new Intent(ConfiguracaoActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        InicializaFirebase();
        InicializaObjetos();
        InicializaCampos();
        PreencheCamposLoad();
        PreencheComboboxEsportes(metodosPublicos.GetEsporteColetivo());

            if(usuarioLogado.getListaDeEsportes() != null){

                for (Esporte esp: usuarioLogado.getListaDeEsportes()) {

                    if(spinnerPrimeiroEsporte.getSelectedItemId() == 0 && esp.getIdentificadorEsporte().equals("primeiroEsporte")){
                        spinnerPrimeiroEsporte.setSelection(metodosPublicos.GetIdEsporteSelecionado(esp.getID()));
                        editTextPrimeiroValor.setText(esp.getValor());
                    }

                    if(spinnerSegundoEsporte.getSelectedItemId() == 0 && esp.getIdentificadorEsporte().equals("segundoEsporte")){
                        spinnerSegundoEsporte.setSelection(metodosPublicos.GetIdEsporteSelecionado(esp.getID()));
                        editTextSegundoValor.setText(esp.getValor());
                    }

                    if(spinnerTerceiroEsporte.getSelectedItemId() == 0 && esp.getIdentificadorEsporte().equals("terceiroEsporte")){
                        spinnerTerceiroEsporte.setSelection(metodosPublicos.GetIdEsporteSelecionado(esp.getID()));
                        editTextTerceiroValor.setText(esp.getValor());
                    }
                }
            }

        ConfiguraSeekBarDistancia();
        ConfiguraCamposValor();
        ConfiguraBotaoSalvar();
    }

    @Override
    protected void onStart() {
        metodosPublicos.TemInternet(App.getContext(), ConfiguracaoActivity.this, LoginActivity.class );
        super.onStart();
    }

    @Override
    protected void onResume() {
        metodosPublicos.TemInternet(App.getContext(), ConfiguracaoActivity.this, LoginActivity.class );
        super.onResume();
    }

    @Override
    public void onBackPressed() { //Botão BACK padrão do android
        metodosPublicos.AbreModalSairAPP(this);
    }

    private void InicializaFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReferenceEsportes = firebaseDatabase.getReference("esporte");
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

    private void InicializaObjetos(){

        metodosPublicos = new MetodosPublicos();
        permissoesAcesso = new PermissoesAcesso();
        usuarioDataBase = new UsuarioDataBase();
    }

    private void InicializaCampos(){

        listaDeEsportes = new ArrayList<>();

        btnSalvarEsporte = findViewById(R.id.btnSalvarEsporte);

        // campos nome e tipo
        txtTipoUsuarioConfig = (TextView) findViewById(R.id.txt_tipo_usuario_config);
        txtNomeUsuarioConfig = (TextView) findViewById(R.id.txt_nome_usuario_config);

        // campos combobox
        spinnerPrimeiroEsporte = (Spinner) findViewById(R.id.spinner_primeiro_esporte);
        spinnerSegundoEsporte = (Spinner) findViewById(R.id.spinner_segundo_esporte);
        spinnerTerceiroEsporte = (Spinner) findViewById(R.id.spinner_terceiro_esporte);

        //campos de valores
        editTextPrimeiroValor = (EditText) findViewById(R.id.txt_valor_primeiro_esporte);
        editTextSegundoValor = (EditText) findViewById(R.id.txt_valor_segundo_esporte);
        editTextTerceiroValor = (EditText) findViewById(R.id.txt_valor_terceiro_esporte);

        // campo seekbar - distancia disponivel
        txtDistanciaEscolida = (TextView)findViewById(R.id.txt_distancia_escolhida);
        seekbarDistancia = (SeekBar) findViewById(R.id.seekBar_distancia_partidas_config); // initiate the Seek bar

        spinnerPrimeiroEsporte.setFocusable(true);
        spinnerPrimeiroEsporte.requestFocus();

    }

    private void PreencheCamposLoad(){

        if(usuarioLogado != null){

            txtTipoUsuarioConfig.setText("Árbitro");
            txtNomeUsuarioConfig.setText(usuarioLogado.getNome() + " " + usuarioLogado.getSobreNome());

            if(usuarioLogado.getDistanciaDisponivel() > 0){
                txtDistanciaEscolida.setText("Você escolheu esta distância: : " + String.valueOf(usuarioLogado.getDistanciaDisponivel()) + " km");
                seekbarDistancia.setProgress(usuarioLogado.getDistanciaDisponivel());
            }else{
                txtDistanciaEscolida.setText("Distância não informada: 0");
                seekbarDistancia.setProgress(0);
            }
        }
    }

    public void ConfiguraBotaoSalvar(){
        btnSalvarEsporte.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    view.setTooltipText("Salvar Configuração.");
                }
                return false;
            }
        });
    }

    public void SalvarConfiguracaoClick(View view) {

        String primeiroCombo = spinnerPrimeiroEsporte.getSelectedItem().toString();
        String segundoCombo = spinnerSegundoEsporte.getSelectedItem().toString();
        String terceiroCombo = spinnerTerceiroEsporte.getSelectedItem().toString();

        String primeiroValor = editTextPrimeiroValor.getText().toString();
        String segundoValor = editTextSegundoValor.getText().toString();
        String terceiroValor = editTextTerceiroValor.getText().toString();

        if(ValidaCampos()){
                listaDeEsportes.clear();
                // PRIMEIRO ESPORTE
                String nomePrimeiroEsporteSelecionado = spinnerPrimeiroEsporte.getSelectedItem().toString();
                String idPrimeiroEsporteSelecionado = String.valueOf(spinnerPrimeiroEsporte.getSelectedItemId());
                String valorPrimeiroEsporteSelecionado = editTextPrimeiroValor.getText().toString();

                int primeiroID = metodosPublicos.GetIdEsporteSelecionado(idPrimeiroEsporteSelecionado);

                Esporte primeiroEsporteSelecionado = new Esporte(idPrimeiroEsporteSelecionado, nomePrimeiroEsporteSelecionado, valorPrimeiroEsporteSelecionado, "primeiroEsporte");
                listaDeEsportes.add(primeiroEsporteSelecionado);

                // SEGUNDO ESPORTE
                String nomeSegundoEsporteSelecionado = spinnerSegundoEsporte.getSelectedItem().toString();
                String idSegundoEsporteSelecionado = String.valueOf(spinnerSegundoEsporte.getSelectedItemId());
                String valorSegundoEsporteSelecionado = editTextSegundoValor.getText().toString();

                int segundoID = metodosPublicos.GetIdEsporteSelecionado(idSegundoEsporteSelecionado);

                Esporte segundoEsporteSelecionado = new Esporte(idSegundoEsporteSelecionado, nomeSegundoEsporteSelecionado, valorSegundoEsporteSelecionado,"segundoEsporte");
                if(!idSegundoEsporteSelecionado.equals("0") && !nomeSegundoEsporteSelecionado.equals("Selecione um Esporte") && !valorSegundoEsporteSelecionado.equals("")){
                    listaDeEsportes.add(segundoEsporteSelecionado);
                }

                // TERCEIRO ESPORTE
                String nomeTerceiroEsporteSelecionado = spinnerTerceiroEsporte.getSelectedItem().toString();
                String idTerceiroEsporteSelecionado = String.valueOf(spinnerTerceiroEsporte.getSelectedItemId());
                String valorTerceiroEsporteSelecionado = editTextTerceiroValor.getText().toString();

                int terceiroID = metodosPublicos.GetIdEsporteSelecionado(idTerceiroEsporteSelecionado);

                Esporte terceiroEsporteSelecionado = new Esporte(idTerceiroEsporteSelecionado, nomeTerceiroEsporteSelecionado, valorTerceiroEsporteSelecionado,"terceiroEsporte");
                if(!idTerceiroEsporteSelecionado.equals("0") && !nomeTerceiroEsporteSelecionado.equals("Selecione um Esporte") && !valorTerceiroEsporteSelecionado.equals("")){
                    listaDeEsportes.add(terceiroEsporteSelecionado);
                }

                usuarioLogado.setListaDeEsportes(listaDeEsportes);
                usuarioDataBase.Atualizar(usuarioLogado, ConfiguracaoActivity.this);

            Toast.makeText(this, "Configuração salva com sucesso.", Toast.LENGTH_LONG).show();

        }

    }

    private void ConfiguraComboboxEsporte(){

        spinnerPrimeiroEsporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
              //  Toast.makeText(getApplicationContext(), "onNothingSelected", Toast.LENGTH_LONG).show();
            }
        });

        spinnerSegundoEsporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // aqui pega o item selecionado
                //Object item = adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerTerceiroEsporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                // aqui pega o item selecionado
                //Object item = adapterView.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void ConfiguraCamposValor(){
        editTextPrimeiroValor.addTextChangedListener(new MoneyTextWatcher(editTextPrimeiroValor));
        editTextSegundoValor.addTextChangedListener(new MoneyTextWatcher(editTextSegundoValor));
        editTextTerceiroValor.addTextChangedListener(new MoneyTextWatcher(editTextTerceiroValor));

        editTextPrimeiroValor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){

                   // Toast.makeText(getApplicationContext(), "Entrou no campo", Toast.LENGTH_LONG).show();
                }
            }
        });

        editTextSegundoValor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    //Toast.makeText(getApplicationContext(), "Entrou no campo Segundo Valor", Toast.LENGTH_LONG).show();
                }else{
                   // Toast.makeText(getApplicationContext(), "Saiu do campo Segundo Valor", Toast.LENGTH_LONG).show();
                }
            }
        });

        editTextTerceiroValor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus) {
                    //Toast.makeText(getApplicationContext(), "Entrou no campo Terceiro Valor", Toast.LENGTH_LONG).show();
                }else{
                  //  Toast.makeText(getApplicationContext(), "Saiu do campo Terceiro Valor", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    private void ConfiguraSeekBarDistancia(){

        int maxValue = seekbarDistancia.getMax(); // get maximum value of the Seek bar
        int seekBarValue= seekbarDistancia.getProgress();
        seekbarDistancia.setMax(50);

        seekbarDistancia.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progressChangedValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                usuarioLogado.setDistanciaDisponivel(progressChangedValue);
                usuarioDataBase.Atualizar(usuarioLogado, ConfiguracaoActivity.this);

                txtDistanciaEscolida.setText("Você escolheu esta distância: " + String.valueOf(progressChangedValue) + " km");

            }
        });

    }

    private void PreencheComboboxEsportes(Map<Integer, String> esportes){

        List<String> listaEsportes = new ArrayList<>();

        listaEsportes.add(0,"Selecione um Esporte");

        for (Map.Entry<Integer, String> pair: esportes.entrySet()){
            listaEsportes.add(pair.getKey(), pair.getValue());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaEsportes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrimeiroEsporte.setAdapter(dataAdapter);
        spinnerPrimeiroEsporte.requestFocus();

        spinnerSegundoEsporte.setAdapter(dataAdapter);
        spinnerSegundoEsporte.requestFocus();

        spinnerTerceiroEsporte.setAdapter(dataAdapter);
        spinnerTerceiroEsporte.requestFocus();

    }


    private void PreenchePrimeiroCombobox(Esporte esporte){

       // ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaEsportes);
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPrimeiroEsporte.setSelection(Integer.parseInt(esporte.getID()), true);
        //spinnerPrimeiroEsporte.setAdapter(dataAdapter);
    }

    private void PreencheSegundoCombobox(Esporte esporte){

        // ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaEsportes);
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerSegundoEsporte.setSelection(Integer.parseInt(esporte.getID()), true);
        //spinnerPrimeiroEsporte.setAdapter(dataAdapter);
    }

    private void PreencheTerceiroCombobox(Esporte esporte){

        // ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaEsportes);
        //dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTerceiroEsporte.setSelection(Integer.parseInt(esporte.getID()), true);
        //spinnerPrimeiroEsporte.setAdapter(dataAdapter);
    }

    private void PreencheComboboxLoad(Usuario usuarioLogado){

        List<String> listaEsportes = new ArrayList<>();

        // Adiciona os items no Combo
        listaEsportes = new ArrayList<String>();
        listaEsportes.add(0,"Selecione um Esporte");

        for (Esporte esp: usuarioLogado.getListaDeEsportes()){
            listaEsportes.add(esp.getNome());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listaEsportes);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPrimeiroEsporte.setAdapter(dataAdapter);
        spinnerPrimeiroEsporte.requestFocus();

        spinnerSegundoEsporte.setAdapter(dataAdapter);
        spinnerSegundoEsporte.requestFocus();

        spinnerTerceiroEsporte.setAdapter(dataAdapter);
        spinnerTerceiroEsporte.requestFocus();

    }


    private boolean ValidaCampos(){

        String primeiroCombo = spinnerPrimeiroEsporte.getSelectedItem().toString();
        String segundoCombo = spinnerSegundoEsporte.getSelectedItem().toString();
        String terceiroCombo = spinnerTerceiroEsporte.getSelectedItem().toString();

        String primeiroValor = editTextPrimeiroValor.getText().toString();
        String segundoValor = editTextSegundoValor.getText().toString();
        String terceiroValor = editTextTerceiroValor.getText().toString();

        if(spinnerPrimeiroEsporte.getSelectedItemId() == 0){
            Toast.makeText(this, "Campo Primeiro Esporte é obrigatório.", Toast.LENGTH_LONG).show();
            spinnerPrimeiroEsporte.requestFocus();
            return false;
        }

        if(editTextPrimeiroValor.getText().toString().equals("") || editTextPrimeiroValor.getText().toString().equals(null)){
            Toast.makeText(this, "Campo valor do Primeiro Esporte é obrigatório.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  //  sendSMSMessage();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==MY_PERMISSIONS_REQUEST_SEND_SMS && resultCode == Activity.RESULT_OK) {
           // sendSMSMessage();
        }
    }


    private void BuscaTodosEsportes(){

        databaseReferenceEsportes = firebaseDatabase.getReference("esporte");
        databaseReferenceEsportes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    Esporte esporte = dataSnapshot.getValue(Esporte.class);

                    if(!listaDeEsportes.contains(esporte)){
                        listaDeEsportes.add(esporte);
                    }
                }

                for(DataSnapshot child: dataSnapshot.getChildren()){

                    if(child.exists()){

                        Esporte esporte = child.getValue(Esporte.class);

                        if(!listaDeEsportes.contains(esporte)){
                            listaDeEsportes.add(esporte);
                        }
                    }
                }
               // PopulaRecyclerViewArbitros();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
