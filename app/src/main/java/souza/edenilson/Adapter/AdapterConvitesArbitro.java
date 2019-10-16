package souza.edenilson.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import souza.edenilson.DataBase.ConviteDataBase;
import souza.edenilson.DataBase.PartidaDataBase;
import souza.edenilson.DataBase.UsuarioDataBase;
import souza.edenilson.Interface.ConviteArbitroRecyclerView_Interface;
import souza.edenilson.Interface.ItemTouchHelperAdapter;
import souza.edenilson.Interface.PartidaRecyclerView_Interface;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.Model.Convite;
import souza.edenilson.Model.Partida;
import souza.edenilson.Model.Usuario;
import souza.edenilson.queroapitar.App;
import souza.edenilson.queroapitar.ConvitesArbitroActivity;
import souza.edenilson.queroapitar.R;

public class AdapterConvitesArbitro extends RecyclerView.Adapter<AdapterConvitesArbitro.ConviteViewHolder> {

    ConviteDataBase conviteDataBase;
    UsuarioDataBase usuarioDataBase;
    Convite convite;
    public static ConviteArbitroRecyclerView_Interface clickConviteArbitroRecyclerViewInterface;
    Context context;
    public List<Convite> mListaDeConvites;
    Usuario usuarioLogado;
    Activity convitesArbitroActivity;

    public AdapterConvitesArbitro(Context ctx, List<Convite> listaDeConvites, ConviteArbitroRecyclerView_Interface _clickConviteRecyclerViewInterface, Usuario _usuarioLogado, Activity activity) {
        this.context = ctx;
        this.mListaDeConvites = listaDeConvites;
        this.clickConviteArbitroRecyclerViewInterface = _clickConviteRecyclerViewInterface;
        conviteDataBase = new ConviteDataBase();
        usuarioLogado = _usuarioLogado;
        convitesArbitroActivity = activity;
        usuarioDataBase = new UsuarioDataBase();
    }

    @NonNull
    @Override
    public ConviteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_convites_arbitro, viewGroup, false);

        itemView.setBackgroundColor(Color.parseColor("#F0FFFF"));
        return new ConviteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ConviteViewHolder holder, int position) {
        convite = mListaDeConvites.get(position);
        MetodosPublicos metodosPublicos = new MetodosPublicos();

        if (convite != null) {

            if(convite.getStatus() != 2){
                Button viewBotaNaoAceitar = (Button) holder.itemView.findViewById(R.id.btn_nao_aceitar_convite);
                Button viewBotaoAceitar = (Button) holder.itemView.findViewById(R.id.btn_aceitar_convite);

                viewBotaNaoAceitar.setVisibility(View.GONE);
                viewBotaoAceitar.setVisibility(View.GONE);
            }

           // if (convite.getStatus() == 2) {
                String[] separaData = convite.getPartida().getDataDaPartida().split("-");
                String dia = separaData[0];
                String mes = separaData[1];
                String ano = separaData[2];
                String dataFormatada = dia + "/" + mes + "/" + ano;

                // CAMPOS DA TELA PRINCIPAL - LISTA DE PARTIDAS
                String endereco = convite.getPartida().getEndereco().getLogradouro() + ", " + convite.getPartida().getEndereco().getNumero() + " - " +
                        convite.getPartida().getEndereco().getBairro() + " - " + convite.getPartida().getEndereco().getCidade() + "/" + metodosPublicos.GetSiglaEstado(convite.getPartida().getEndereco().getEstado());

                String nomeRemetente = convite.getPartida().getUsuarioDonoDaPartida().getNome().substring(0,1).toUpperCase() + convite.getPartida().getUsuarioDonoDaPartida().getNome().substring(1);
                String sobreNomeRemetente = convite.getPartida().getUsuarioDonoDaPartida().getSobreNome().substring(0,1).toUpperCase() + convite.getPartida().getUsuarioDonoDaPartida().getSobreNome().substring(1);

                metodosPublicos.CarregaImageView(holder.viewFotoPerfil,convite.getRemetente().getUrlFoto());
                holder.viewRemetente.setText("Dono da Partida: " + nomeRemetente + " " + sobreNomeRemetente);
                holder.viewTipoDeEsporte.setText("Esporte: " + convite.getPartida().getEsporte().getNome());
                holder.viewDataDaPartida.setText("Data da Partida: " + dataFormatada + " às " + convite.getPartida().getHoraDaPartida());
                holder.viewEnderecoDaPartida.setText("Endereço: " + endereco);
                holder.viewStatusConvite.setText(metodosPublicos.GetStatusConvite(convite.getStatus()));
          //  }
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
        conviteDataBase.Remover(convite.getId());
        notifyItemRemoved(position);
    }


    protected class ConviteViewHolder extends RecyclerView.ViewHolder {

        // CAMPOS DA TELA PRINCIPAL - LISTA DE PARTIDAS
        protected ImageView viewFotoPerfil;
        protected TextView viewRemetente;
        protected TextView viewDataDaPartida;
        protected TextView viewEnderecoDaPartida;
        protected TextView viewTipoDeEsporte;
        protected Button viewBotaoAceitar;
        protected Button viewBotaNaoAceitar;
        protected TextView viewStatusConvite;

        public ConviteViewHolder(@NonNull View itemView) {
            super(itemView);

            viewBotaNaoAceitar = (Button) itemView.findViewById(R.id.btn_nao_aceitar_convite);
            viewBotaoAceitar = (Button) itemView.findViewById(R.id.btn_aceitar_convite);
            viewFotoPerfil = (ImageView) itemView.findViewById(R.id.imagem_perfil);
            viewRemetente = (TextView) itemView.findViewById(R.id.txt_nome_remetente);
            viewDataDaPartida = (TextView) itemView.findViewById(R.id.txt_data_da_partida);
            viewEnderecoDaPartida = (TextView) itemView.findViewById(R.id.txt_endereco_da_partida);
            viewTipoDeEsporte = (TextView) itemView.findViewById(R.id.txt_esporte_convite);
            viewStatusConvite = (TextView) itemView.findViewById(R.id.txt_status_convite_arbitro);


            viewBotaoAceitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    convite = mListaDeConvites.get(getLayoutPosition());
                    if(convite != null){

                        String nomeDonoPartida = convite.getRemetente().getNome().substring(0,1).toUpperCase() + convite.getRemetente().getNome().substring(1);
                        String sobreNomeDonoPartida = convite.getRemetente().getSobreNome().substring(0,1).toUpperCase() + convite.getRemetente().getSobreNome().substring(1);

                        ConvitesArbitroActivity activity = (ConvitesArbitroActivity) context;

                        int partidasApitadas = convite.getConvidado().getQuantidadePartidas();
                        usuarioLogado.setQuantidadePartidas(partidasApitadas + 1);
                        usuarioDataBase.Atualizar(usuarioLogado); // atualiza a quantidade de partidas apitadas.

                        convite.setStatus(1);
                        convite.getPartida().setStatusConvite(1);
                        conviteDataBase.Atualiza(convite, usuarioLogado, convitesArbitroActivity, "arbitroRespondeJogador");

                        Toast.makeText(AdapterConvitesArbitro.this.context, "Você aceitou o convite de " + nomeDonoPartida + " " +sobreNomeDonoPartida, Toast.LENGTH_LONG).show();
                    }


                }
            });

            viewBotaoAceitar.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        view.setTooltipText("Aceitar Convite.");
                    }

                    return false;
                }
            });

            viewBotaNaoAceitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    convite = mListaDeConvites.get(getLayoutPosition());
                    if(convite != null){
                        String nomeDonoPartida = convite.getRemetente().getNome().substring(0,1).toUpperCase() + convite.getRemetente().getNome().substring(1);
                        String sobreNomeDonoPartida = convite.getRemetente().getSobreNome().substring(0,1).toUpperCase() + convite.getRemetente().getSobreNome().substring(1);

                        ConvitesArbitroActivity activity = (ConvitesArbitroActivity) context;
                        convite.setStatus(3);
                        convite.getPartida().setStatusConvite(3);
                        conviteDataBase.Atualiza(convite, usuarioLogado, convitesArbitroActivity, "arbitroRespondeJogador");


                        Toast.makeText(AdapterConvitesArbitro.this.context, "Você não aceitou o convite de " + nomeDonoPartida + " " +sobreNomeDonoPartida , Toast.LENGTH_LONG).show();
                    }


                }
            });

            viewBotaNaoAceitar.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        view.setTooltipText("Não aceitar Convite.");
                    }

                    return false;
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    clickConviteArbitroRecyclerViewInterface.onCustomClick(mListaDeConvites.get(getLayoutPosition()));

                }
            });
        }
    }

}