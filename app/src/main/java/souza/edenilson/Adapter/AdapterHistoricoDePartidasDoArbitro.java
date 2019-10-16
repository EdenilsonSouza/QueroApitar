package souza.edenilson.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

import souza.edenilson.DataBase.ConviteDataBase;
import souza.edenilson.DataBase.PartidaDataBase;
import souza.edenilson.Interface.ConviteJogadorRecyclerView_Interface;
import souza.edenilson.Interface.HistoricoPartidasArbitroRecyclerView_Interface;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.MetodosPublicos.PermissoesAcesso;
import souza.edenilson.Model.Convite;
import souza.edenilson.Model.Partida;
import souza.edenilson.Model.Usuario;
import souza.edenilson.queroapitar.R;

public class AdapterHistoricoDePartidasDoArbitro extends RecyclerView.Adapter<AdapterHistoricoDePartidasDoArbitro.HistoricoPartidasArbitrosViewHolder> {

    PartidaDataBase partidaDataBase;
    ConviteDataBase conviteDataBase;
    Usuario usuarioLogado;
    public static HistoricoPartidasArbitroRecyclerView_Interface clickHistoricoPartidasArbitroRecyclerViewInterface;
    Context contexto;
    public List<Partida> mListaDePartidas;
    private AlertDialog alerta;
    PermissoesAcesso permissoesAcesso = new PermissoesAcesso();


    public AdapterHistoricoDePartidasDoArbitro(Context ctx, List<Partida> listDePartidas, HistoricoPartidasArbitroRecyclerView_Interface clickHistoricoPartidasArbitroRecyclerViewInterface, Usuario usuariologado) {
        this.contexto = ctx;
        this.mListaDePartidas = listDePartidas;
        this.clickHistoricoPartidasArbitroRecyclerViewInterface = clickHistoricoPartidasArbitroRecyclerViewInterface;
        partidaDataBase = new PartidaDataBase();
        usuarioLogado = usuariologado;

    }

    @NonNull
    @Override
    public HistoricoPartidasArbitrosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_historico_partidas_arbitro, viewGroup, false);

        // adiciona a cor na Recyclerview
        itemView.setBackgroundColor(Color.parseColor("#F0FFFF"));

        return new HistoricoPartidasArbitrosViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoricoPartidasArbitrosViewHolder holder, int position) {

        Partida partida = mListaDePartidas.get(position);
        MetodosPublicos metodosPublicos = new MetodosPublicos();

        if (partida != null) {

            String[] separaData = partida.getDataDaPartida().split("-");
            String dia = separaData[0];
            String mes = separaData[1];
            String ano = separaData[2];
            String dataFormatada = dia + "/" + mes + "/" + ano;

            // CAMPOS DA TELA PRINCIPAL - LISTA DE PARTIDAS
            String endereco = partida.getEndereco().getLogradouro() + ", " + partida.getEndereco().getNumero() + " - " +
                    partida.getEndereco().getBairro() + " - " + partida.getEndereco().getCidade() + "/" + metodosPublicos.GetSiglaEstado(partida.getEndereco().getEstado());

            String nomeDonoPartida = partida.getUsuarioDonoDaPartida().getNome().substring(0,1).toUpperCase() + partida.getUsuarioDonoDaPartida().getNome().substring(1);
            String sobreNomeDonoPartida = partida.getUsuarioDonoDaPartida().getSobreNome().substring(0,1).toUpperCase() + partida.getUsuarioDonoDaPartida().getSobreNome().substring(1);

            String nomeArbitro = partida.getArbitro().getNome().substring(0,1).toUpperCase() + partida.getArbitro().getNome().substring(1);
            String sobreNomeArbitro = partida.getArbitro().getSobreNome().substring(0,1).toUpperCase() + partida.getArbitro().getSobreNome().substring(1);

           // metodosPublicos.CarregaImageView(holder.viewFotoPerfil,partida.getUsuarioDonoDaPartida().getUrlFoto());
            holder.viewDonoDaPartida.setText("Dono da Partida: " + nomeDonoPartida + " " + sobreNomeDonoPartida);
            holder.viewArbitro.setText("Árbitro: " + nomeArbitro + " " + sobreNomeArbitro);
            holder.viewTipoDeEsporte.setText(partida.getEsporte().getNome());
            holder.viewDataDaPartida.setText(dataFormatada + " às " + partida.getHoraDaPartida());
            holder.viewEnderecoDaPartida.setText(endereco);

            Date dataAtual = new Date();
            Date dataDaPartida = metodosPublicos.ConvertStringParaData(partida.getDataDaPartida());

            int dataPassada = dataDaPartida.compareTo(dataAtual);

            if(dataPassada < 0) {
                partida.setStatusConvite(7); // data passada
                partida.setStatus(false);
            }

            holder.viewStatusPartidaArbitro.setText(metodosPublicos.GetStatusConvite(partida.getStatusConvite()));

        }

    }

    @Override
    public int getItemCount() {
        return mListaDePartidas.size();
    }

    public void remove(int position) {
        if (position < 0 || position >= mListaDePartidas.size()) {
            return;
        }

        Partida partida = mListaDePartidas.get(position);
        mListaDePartidas.remove(position);
        partidaDataBase.Remover(partida.getID());
        notifyItemRemoved(position);
    }


    protected class HistoricoPartidasArbitrosViewHolder extends RecyclerView.ViewHolder {

        // CAMPOS DA TELA PRINCIPAL - LISTA DE PARTIDAS
       // protected ImageView viewFotoPerfil;
        protected TextView viewDonoDaPartida;
        protected TextView viewArbitro;
        protected TextView viewDataDaPartida;
        protected TextView viewEnderecoDaPartida;
        protected TextView viewTipoDeEsporte;
        protected TextView viewStatusPartidaArbitro;

        public HistoricoPartidasArbitrosViewHolder(@NonNull View itemView) {
            super(itemView);

            //viewFotoPerfil = (ImageView) itemView.findViewById(R.id.img_historico_convites_perfil);
            viewDonoDaPartida = (TextView) itemView.findViewById(R.id.txt_dono_da_partida_historico_arbitro);
            viewArbitro = (TextView) itemView.findViewById(R.id.txt_arbitro_da_partida_historico_arbitro);
            viewDataDaPartida = (TextView) itemView.findViewById(R.id.txt_data_partida_historico_arbitro);
            viewEnderecoDaPartida = (TextView) itemView.findViewById(R.id.txt_endereco_partida_historico_arbitro);
            viewTipoDeEsporte = (TextView) itemView.findViewById(R.id.txt_esporte_historico_arbitro);
            viewStatusPartidaArbitro = (TextView) itemView.findViewById(R.id.txt_status_partida_arbitro);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    clickHistoricoPartidasArbitroRecyclerViewInterface.onCustomClick(mListaDePartidas.get(getLayoutPosition()));
                }
            });
        }

    }

}