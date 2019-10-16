package souza.edenilson.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;
import java.util.List;

import souza.edenilson.DataBase.ConviteDataBase;
import souza.edenilson.DataBase.PartidaDataBase;
import souza.edenilson.Interface.ConviteJogadorRecyclerView_Interface;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.MetodosPublicos.PermissoesAcesso;
import souza.edenilson.Model.Convite;
import souza.edenilson.Model.Usuario;
import souza.edenilson.queroapitar.ConvitesArbitroActivity;
import souza.edenilson.queroapitar.ConvitesJogadorActivity;
import souza.edenilson.queroapitar.R;

public class AdapterConvitesJogador extends RecyclerView.Adapter<AdapterConvitesJogador.ConviteJogadorViewHolder> {

    Convite convite;
    MetodosPublicos metodosPublicos;
    PartidaDataBase partidaDataBase;
    ConviteDataBase conviteDataBase;
    Usuario usuarioLogado;
    public static ConviteJogadorRecyclerView_Interface clickConvitesJogadorRecyclerViewInterface;
    Context contexto;
    public List<Convite> mListaDeConvites;
    Activity convitesJogadorActivity;

    public AdapterConvitesJogador(Context ctx, List<Convite> listDeConvites, ConviteJogadorRecyclerView_Interface clickConvitesJogadorRecyclerViewInterface, Usuario usuariologado, Activity activity) {
        this.contexto = ctx;
        this.mListaDeConvites = listDeConvites;
        this.clickConvitesJogadorRecyclerViewInterface = clickConvitesJogadorRecyclerViewInterface;
        partidaDataBase = new PartidaDataBase();
        usuarioLogado = usuariologado;
        metodosPublicos = new MetodosPublicos();
        convitesJogadorActivity = activity;
        conviteDataBase = new ConviteDataBase();
    }

    @NonNull
    @Override
    public ConviteJogadorViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_convites_jogador, viewGroup, false);

        // adiciona a cor na Recyclerview
        itemView.setBackgroundColor(Color.parseColor("#F0FFFF"));

        return new ConviteJogadorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ConviteJogadorViewHolder holder, int position) {
        convite = mListaDeConvites.get(position);
        metodosPublicos = new MetodosPublicos();

        Button btnAvaliarArbitro = (Button) holder.itemView.findViewById(R.id.btn_avaliar_arbitro);
        TextView txtAvaliarArbitro = (TextView) holder.itemView.findViewById(R.id.txt_avaliar_arbitro);

        TextView txtAceitarArbitro = (TextView) holder.itemView.findViewById(R.id.txt_aceitar_pedido_arbitro);
        Button btnAceitarArbitro = (Button) holder.itemView.findViewById(R.id.btn_aceitar_pedido_do_arbitro);
        Button btnNaoAceitarArbitro = (Button) holder.itemView.findViewById(R.id.btn_nao_aceitar_pedido_arbitro);
        TextView txtNaoAceitarArbitro = (TextView) holder.itemView.findViewById(R.id.txt_nao_aceitar_pedido_arbitro);

        if (convite != null) {

            btnAvaliarArbitro.setBackgroundColor(Color.parseColor("#F0FFFF"));
            txtAceitarArbitro.setBackgroundColor(Color.parseColor("#F0FFFF"));

            Date dataAtual = new Date();
            Date dataDaPartida = metodosPublicos.ConvertStringParaData(convite.getPartida().getDataDaPartida());

            int dataPassada = dataDaPartida.compareTo(dataAtual);

            if(dataPassada < 0){

                if(convite.getConvidado() != null && !convite.isAvaliado()){
                    btnAvaliarArbitro.setVisibility(View.VISIBLE);
                    txtAvaliarArbitro.setVisibility(View.VISIBLE);
                }
                //  ja passou a data da partida
                convite.setStatus(7);
                convite.getPartida().setStatusConvite(7);
                conviteDataBase.Atualiza(convite, usuarioLogado, convitesJogadorActivity,"dataDaPartidaPassada");
            }

            if(convite.getStatus() == 6){
                btnAvaliarArbitro.setVisibility(View.GONE);
                txtAvaliarArbitro.setVisibility(View.GONE);

                btnAceitarArbitro.setVisibility(View.VISIBLE);
                txtAceitarArbitro.setVisibility(View.VISIBLE);
                btnNaoAceitarArbitro.setVisibility(View.VISIBLE);
                txtNaoAceitarArbitro.setVisibility(View.VISIBLE);
            }

            if (convite.getPartida() != null) {
                String[] separaData = convite.getPartida().getDataDaPartida().split("-");
                String dia = separaData[0];
                String mes = separaData[1];
                String ano = separaData[2];
                String dataFormatada = dia + "/" + mes + "/" + ano;

                // CAMPOS DA TELA PRINCIPAL - LISTA DE PARTIDAS
                String endereco = convite.getPartida().getEndereco().getLogradouro() + ", " + convite.getPartida().getEndereco().getNumero() + " - " +
                        convite.getPartida().getEndereco().getBairro() + " - " + convite.getPartida().getEndereco().getCidade() + "/" + metodosPublicos.GetSiglaEstado(convite.getPartida().getEndereco().getEstado());

                String nomeDonoPartida = convite.getRemetente().getNome().substring(0,1).toUpperCase() + convite.getRemetente().getNome().substring(1);
                String sobreNomeDonoPartida = convite.getRemetente().getSobreNome().substring(0,1).toUpperCase() + convite.getRemetente().getSobreNome().substring(1);

                if(convite.getConvidado() != null){
                    String nomeConvidado = convite.getConvidado().getNome().substring(0,1).toUpperCase() + convite.getConvidado().getNome().substring(1);
                    String sobreNomeConvidado = convite.getConvidado().getSobreNome().substring(0,1).toUpperCase() + convite.getConvidado().getSobreNome().substring(1);
                    holder.viewConvidado.setText("Árbitro: " + nomeConvidado + " " + sobreNomeConvidado);
                }else{
                    holder.viewConvidado.setText("Árbitro: não há árbitro para esta partida.");
                }

                metodosPublicos.CarregaImageView(holder.viewFotoPerfil,convite.getRemetente().getUrlFoto());
                holder.viewRemetente.setText("Dono da Partida: " + nomeDonoPartida + " " + sobreNomeDonoPartida);

                holder.viewTipoDeEsporte.setText("Esporte: "+convite.getPartida().getEsporte().getNome());
                holder.viewDataDaPartida.setText("Data da Partida: " + dataFormatada + " às " + convite.getPartida().getHoraDaPartida());
                holder.viewEnderecoDaPartida.setText("Endereço: " + endereco);
                holder.viewStatusConvite.setText(metodosPublicos.GetStatusConvite(convite.getStatus()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mListaDeConvites.size();
    }

    public void remove(int position) {
        if (position < 0 || position >= mListaDeConvites.size()) {

            return;
        }

        Convite convite = mListaDeConvites.get(position);
        mListaDeConvites.remove(position);
        partidaDataBase.Remover(convite.getId());
        notifyItemRemoved(position);
    }


    protected class ConviteJogadorViewHolder extends RecyclerView.ViewHolder {

        // CAMPOS DA TELA PRINCIPAL - LISTA DE PARTIDAS
        protected ImageView viewFotoPerfil;
        protected TextView viewRemetente;
        protected TextView viewConvidado;
        protected TextView viewDataDaPartida;
        protected TextView viewEnderecoDaPartida;
        protected TextView viewTipoDeEsporte;
        protected Button viewBtnAvaliarArbitro;
        protected TextView viewTxtAvaliarArbitro;
        protected Button viewBtnAceitarPedidoArbitro;
        protected Button viewBtnNaoAceitarPedidoArbitro;
        protected TextView viewStatusConvite;

        public ConviteJogadorViewHolder(@NonNull View itemView) {
            super(itemView);

            viewFotoPerfil = (ImageView) itemView.findViewById(R.id.img_historico_convites_perfil);
            viewRemetente = (TextView) itemView.findViewById(R.id.txt_historico_nome_remetente);
            viewConvidado = (TextView) itemView.findViewById(R.id.txt_historico_nome_convidado);
            viewDataDaPartida = (TextView) itemView.findViewById(R.id.txt_historico_data_da_partida);
            viewEnderecoDaPartida = (TextView) itemView.findViewById(R.id.txt_historico_endereco_da_partida);
            viewTipoDeEsporte = (TextView) itemView.findViewById(R.id.txt_historico_esporte_convite);
            viewBtnAvaliarArbitro = (Button) itemView.findViewById(R.id.btn_avaliar_arbitro);
            viewTxtAvaliarArbitro = (TextView) itemView.findViewById(R.id.txt_avaliar_arbitro);
            viewBtnAceitarPedidoArbitro = (Button) itemView.findViewById(R.id.btn_aceitar_pedido_do_arbitro);
            viewBtnNaoAceitarPedidoArbitro = (Button) itemView.findViewById(R.id.btn_nao_aceitar_pedido_arbitro);
            viewStatusConvite = (TextView) itemView.findViewById(R.id.txt_status_convite_jogador);

            viewBtnAceitarPedidoArbitro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Convite conviteSelecionado = mListaDeConvites.get(getLayoutPosition());

                    Convite newConvite = convite;
                    if(conviteSelecionado != null){

                        ConvitesJogadorActivity activity = (ConvitesJogadorActivity) contexto;

                        conviteSelecionado.setStatus(5);

                        conviteSelecionado.getPartida().setStatusConvite(5);
                        partidaDataBase.Atualiza(conviteSelecionado.getPartida());

                        String nomeArbitro = conviteSelecionado.getConvidado().getNome().substring(0,1).toUpperCase() + conviteSelecionado.getConvidado().getNome().substring(1);
                        String sobreNomeArbitro = conviteSelecionado.getConvidado().getSobreNome().substring(0,1).toUpperCase() + conviteSelecionado.getConvidado().getSobreNome().substring(1);

                        conviteDataBase.Atualiza(conviteSelecionado, usuarioLogado, activity,"DonoDaPartidaAceitouArbitro");

                        Toast.makeText(AdapterConvitesJogador.this.contexto, "Você aceitou o árbitro " + nomeArbitro + " " +sobreNomeArbitro + " para apitar sua partida.", Toast.LENGTH_LONG).show();
                    }

                }
            });

            viewBtnAceitarPedidoArbitro.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        view.setTooltipText("Aceitar pedido do Árbitro.");
                    }

                    return false;
                }
            });

            viewBtnNaoAceitarPedidoArbitro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    convite = mListaDeConvites.get(getLayoutPosition());
                    if(convite != null){

                        convite.setStatus(5);
                        convite.getPartida().setStatusConvite(5);

                        String nomeArbitro = convite.getConvidado().getNome().substring(0,1).toUpperCase() + convite.getConvidado().getNome().substring(1);
                        String sobreNomeArbitro = convite.getConvidado().getSobreNome().substring(0,1).toUpperCase() + convite.getConvidado().getSobreNome().substring(1);

                        ConvitesJogadorActivity activity = (ConvitesJogadorActivity) contexto;
                        conviteDataBase.Atualiza(convite, usuarioLogado, activity,"DonoDaPartidaNaoAceitouArbitro");

                        Toast.makeText(AdapterConvitesJogador.this.contexto, "Você não aceitou o árbitro " + nomeArbitro + " " +sobreNomeArbitro + " para apitar sua partida.", Toast.LENGTH_LONG).show();
                    }

                }
            });

            viewBtnNaoAceitarPedidoArbitro.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View view) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        view.setTooltipText("Não aceitar pedido do Árbitro.");
                    }
                    return false;
                }

            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        clickConvitesJogadorRecyclerViewInterface.onCustomClick(mListaDeConvites.get(getLayoutPosition()));

                }
            });
        }

    }

}