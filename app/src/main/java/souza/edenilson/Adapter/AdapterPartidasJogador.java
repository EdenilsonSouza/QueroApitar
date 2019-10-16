package souza.edenilson.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

import souza.edenilson.DataBase.PartidaDataBase;
import souza.edenilson.Interface.PartidaRecyclerView_Interface;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.Model.Partida;
import souza.edenilson.queroapitar.R;

public class AdapterPartidasJogador extends RecyclerView.Adapter<AdapterPartidasJogador.PartidaViewHolder> {

    PartidaDataBase partidaDataBase;
    public static PartidaRecyclerView_Interface clickPartidasRecyclerViewInterface;
    Context mctx;
    public List<Partida> mListaDePartidas;

    public AdapterPartidasJogador(Context ctx, List<Partida> listDePartidas, PartidaRecyclerView_Interface clickPartidasRecyclerViewInterface) {
        this.mctx = ctx;
        this.mListaDePartidas = listDePartidas;
        this.clickPartidasRecyclerViewInterface = clickPartidasRecyclerViewInterface;
        partidaDataBase = new PartidaDataBase();
    }

    @NonNull
    @Override
    public PartidaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_de_partidas, viewGroup, false);

        // adiciona a cor na Recyclerview
        itemView.setBackgroundColor(Color.parseColor("#F0FFFF"));

        return new PartidaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PartidaViewHolder holder, int position) {
        Partida partida = mListaDePartidas.get(position);
        MetodosPublicos metodosPublicos = new MetodosPublicos();

        LinearLayout container_partidas = (LinearLayout)holder.itemView.findViewById(R.id.container_partidas);

        if (partida != null) {

             Date dataAtual = new Date();
             Date dataDaPartida = metodosPublicos.ConvertStringParaData(partida.getDataDaPartida());

             int dataPassada = dataDaPartida.compareTo(dataAtual);

             // se a data ainda não passou exibe a partida
             if(dataPassada > 0){

                 if (partida.getDataDaPartida() != null) {
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

                     holder.viewDonoDaPartida.setText(nomeDonoPartida + " " + sobreNomeDonoPartida);
                     holder.viewTipoDeEsporte.setText(partida.getEsporte().getNome());
                     holder.viewDataDaPartida.setText(dataFormatada + " às " + partida.getHoraDaPartida());
                     holder.viewEnderecoDaPartida.setText(endereco);
                 }
             }else{
                 container_partidas.setVisibility(View.GONE);
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


    protected class PartidaViewHolder extends RecyclerView.ViewHolder {

        // CAMPOS DA TELA PRINCIPAL - LISTA DE PARTIDAS
        protected TextView viewDonoDaPartida;
        protected TextView viewDataDaPartida;
        protected TextView viewEnderecoDaPartida;
        protected TextView viewTipoDeEsporte;

        public PartidaViewHolder(@NonNull View itemView) {
            super(itemView);

            viewDonoDaPartida = (TextView) itemView.findViewById(R.id.txt_dono_da_partida);
            viewDataDaPartida = (TextView) itemView.findViewById(R.id.txt_data_partida);
            viewEnderecoDaPartida = (TextView) itemView.findViewById(R.id.txt_endereco_partida);
            viewTipoDeEsporte = (TextView) itemView.findViewById(R.id.txt_esporte);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    clickPartidasRecyclerViewInterface.onCustomClick(mListaDePartidas.get(getLayoutPosition()));

                }
            });
        }
    }

}