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
import souza.edenilson.Interface.HistoricoPartidasJogadorRecyclerView_Interface;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.MetodosPublicos.PermissoesAcesso;
import souza.edenilson.Model.Convite;
import souza.edenilson.Model.Partida;
import souza.edenilson.Model.Usuario;
import souza.edenilson.queroapitar.R;

public class AdapterHistoricoDePartidasDoJogador extends RecyclerView.Adapter<AdapterHistoricoDePartidasDoJogador.HistoricoPartidasJogadorViewHolder> {

    PartidaDataBase partidaDataBase;
    Usuario usuarioLogado;
    public static HistoricoPartidasJogadorRecyclerView_Interface clickHistoricoPartidasJogadorRecyclerView_interface;
    Context contexto;
    public List<Partida> mListaDePartidas;

    public AdapterHistoricoDePartidasDoJogador(Context ctx, List<Partida> listDePartidas, HistoricoPartidasJogadorRecyclerView_Interface historicoPartidasJogadorRecyclerView_interface, Usuario usuariologado) {
        this.contexto = ctx;
        this.mListaDePartidas = listDePartidas;
        this.clickHistoricoPartidasJogadorRecyclerView_interface = historicoPartidasJogadorRecyclerView_interface;
        partidaDataBase = new PartidaDataBase();
        usuarioLogado = usuariologado;

    }

    @NonNull
    @Override
    public HistoricoPartidasJogadorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_historico_partidas_jogador, viewGroup, false);

        // adiciona a cor na Recyclerview
        itemView.setBackgroundColor(Color.parseColor("#F0FFFF"));

        return new HistoricoPartidasJogadorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoricoPartidasJogadorViewHolder holder, int position) {

        Partida partida = mListaDePartidas.get(position);
        MetodosPublicos metodosPublicos = new MetodosPublicos();

        if (partida != null) {

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

                holder.viewDonoDaPartida.setText("Dono da Partida: " + nomeDonoPartida + " " + sobreNomeDonoPartida);
                holder.viewTipoDeEsporte.setText(partida.getEsporte().getNome());
                holder.viewDataDaPartida.setText(dataFormatada + " às " + partida.getHoraDaPartida());
                holder.viewEnderecoDaPartida.setText(endereco);

                Date dataAtual = new Date();
                Date dataDaPartida = metodosPublicos.ConvertStringParaData(partida.getDataDaPartida());

                int dataPassada = dataDaPartida.compareTo(dataAtual);

                if(dataPassada < 0) {
                    partida.setStatusConvite(7); // data passada
                }

                holder.viewStatusPartida.setText(metodosPublicos.GetStatusConvite(partida.getStatusConvite()));
            }
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


    protected class HistoricoPartidasJogadorViewHolder extends RecyclerView.ViewHolder {

        // CAMPOS DA TELA PRINCIPAL - LISTA DE PARTIDAS

        protected TextView viewDonoDaPartida;
        protected TextView viewDataDaPartida;
        protected TextView viewEnderecoDaPartida;
        protected TextView viewTipoDeEsporte;
        protected TextView viewArbitro;
        protected TextView viewStatusPartida;

        public HistoricoPartidasJogadorViewHolder(@NonNull View itemView) {
            super(itemView);

            viewDonoDaPartida = (TextView) itemView.findViewById(R.id.txt_dono_da_partida);
            viewDataDaPartida = (TextView) itemView.findViewById(R.id.txt_data_partida);
            viewEnderecoDaPartida = (TextView) itemView.findViewById(R.id.txt_endereco_partida);
            viewTipoDeEsporte = (TextView) itemView.findViewById(R.id.txt_esporte);
            viewStatusPartida = (TextView) itemView.findViewById(R.id.txt_status_partida_jogador);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    clickHistoricoPartidasJogadorRecyclerView_interface.onCustomClick(mListaDePartidas.get(getLayoutPosition()));
                }
            });
        }

    }

}