package souza.edenilson.Adapter;


import android.content.Context;
import android.graphics.Color;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import souza.edenilson.DataBase.PartidaDataBase;
import souza.edenilson.Interface.PartidaModalRecyclerViewModal;
import souza.edenilson.Interface.PartidaRecyclerView_Interface;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.Model.Partida;
import souza.edenilson.Model.Usuario;
import souza.edenilson.queroapitar.MainActivity;
import souza.edenilson.queroapitar.R;

public class AdapterPartidasModal extends RecyclerView.Adapter<AdapterPartidasModal.PartidaModalViewHolder> {

    PartidaDataBase partidaDataBase;
    public static PartidaModalRecyclerViewModal partidaModalRecyclerViewModal ;
    public List<Partida> mListaDePartidas;
    private RadioGroup lastCheckedRadioGroup = null;
    private Context context;

    public AdapterPartidasModal(Context ctx, List<Partida> listDePartidas, PartidaModalRecyclerViewModal _partidaModalRecyclerViewModal) {
        this.context = ctx;
        this.mListaDePartidas = listDePartidas;
        this.partidaModalRecyclerViewModal = _partidaModalRecyclerViewModal;
        partidaDataBase = new PartidaDataBase();
    }

    @NonNull
    @Override
    public PartidaModalViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View itemViewModal = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_modal_de_partidas, viewGroup, false);

        itemViewModal.setBackgroundColor(Color.parseColor("#F0FFFF"));
        return new PartidaModalViewHolder(itemViewModal);
    }

    @Override
    public void onBindViewHolder(@NonNull PartidaModalViewHolder holder, int position) {
        Partida partida = mListaDePartidas.get(position);
        MetodosPublicos metodosPublicos = new MetodosPublicos();

        if (partida != null) {

            // ADICIONO DINAMICAMENTE O RADIO BUTTON NA RECYCLERVIEW
            RadioButton rb = new RadioButton(AdapterPartidasModal.this.context);
            rb.setId(position);
            holder.viewRadioGroupPartidaSelecionada.addView(rb);

            if (partida.getDataDaPartida() != null) {
                String[] separaData = partida.getDataDaPartida().split("-");
                String dia = separaData[0];
                String mes = separaData[1];
                String ano = separaData[2];
                String dataFormatada = dia + "/" + mes + "/" + ano;

                // CAMPOS DA TELA MODAL - LISTA DE PARTIDAS
                String endereco = partida.getEndereco().getLogradouro() + ", " + partida.getEndereco().getNumero() + " - " +
                        partida.getEndereco().getBairro() + " - " + partida.getEndereco().getCidade() + "/" + metodosPublicos.GetSiglaEstado(partida.getEndereco().getEstado());

                holder.viewIdPartidaModal.setText(partida.getID());
                holder.viewDonoDaPartidaModal.setText(partida.getUsuarioDonoDaPartida().getNome() + " " + partida.getUsuarioDonoDaPartida().getSobreNome());
                holder.viewTipoDeEsporteModal.setText(partida.getEsporte().getNome());
                holder.viewDataDoJogoModal.setText(dataFormatada + " às " + partida.getHoraDaPartida());
                holder.viewEnderecoDaPartidaModal.setText(endereco);

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


    protected class PartidaModalViewHolder extends RecyclerView.ViewHolder {

        // CAMPOS DA MODAL LISTA DE PARTIDAS - XML conecta_partida_arbitro.xml
        protected TextView viewIdPartidaModal;
        protected TextView viewDonoDaPartidaModal;
        protected TextView viewDataDoJogoModal;
        protected TextView viewEnderecoDaPartidaModal;
        protected TextView viewTipoDeEsporteModal;
        public RadioButton viewRadioPartidaSelecionada;
        public RadioGroup viewRadioGroupPartidaSelecionada;


        public PartidaModalViewHolder(@NonNull View itemView) {
            super(itemView);

            // CAMPOS DA MODAL
            viewIdPartidaModal = (TextView) itemView.findViewById(R.id.txt_id_partida);
            viewDonoDaPartidaModal = (TextView) itemView.findViewById(R.id.txt_dono_partida_modal);
            viewDataDoJogoModal = (TextView) itemView.findViewById(R.id.txt_data_da_partida_modal);
            viewEnderecoDaPartidaModal = (TextView) itemView.findViewById(R.id.txt_endereco_partida_modal);
            viewTipoDeEsporteModal = (TextView) itemView.findViewById(R.id.txt_nome_esporte_modal);
            viewRadioGroupPartidaSelecionada = (RadioGroup) itemView.findViewById(R.id.radioGroupPartidaSelecionada);

            viewRadioGroupPartidaSelecionada.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int idPartidaSelecionada) {
                    if (lastCheckedRadioGroup != null && lastCheckedRadioGroup.getCheckedRadioButtonId() != viewRadioGroupPartidaSelecionada.getCheckedRadioButtonId()
                            && lastCheckedRadioGroup.getCheckedRadioButtonId() != -1) {
                        lastCheckedRadioGroup.clearCheck();

                        Toast.makeText(AdapterPartidasModal.this.context,
                                "Radio button clicked " + radioGroup.getCheckedRadioButtonId(),
                                Toast.LENGTH_SHORT).show();

                    }
                    lastCheckedRadioGroup = viewRadioGroupPartidaSelecionada;
                    partidaModalRecyclerViewModal.onCustomClickModal(mListaDePartidas.get(getLayoutPosition()), true , idPartidaSelecionada);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //partidaModalRecyclerViewModal.onCustomClick(mListaDePartidas.get(getLayoutPosition()));
                   // partidaModalRecyclerViewModal.onCustomClickModal(mListaDePartidas.get(getLayoutPosition()), new Usuario() );

                }
            });
        }
    }

}
