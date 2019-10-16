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

import java.util.List;

import souza.edenilson.Interface.ArbitrosRecyclerView_Interface;
import souza.edenilson.MetodosPublicos.MetodosPublicos;
import souza.edenilson.Model.Endereco;
import souza.edenilson.Model.Esporte;
import souza.edenilson.Model.Usuario;
import souza.edenilson.queroapitar.R;

public class AdapterPartidasArbitros extends RecyclerView.Adapter<AdapterPartidasArbitros.ArbitroViewHolder> {

    public static ArbitrosRecyclerView_Interface clickArbitrosRecyclerViewInterface;
    Context mctx;
    public List<Usuario> mListaDeUsuarios;

    public AdapterPartidasArbitros(Context ctx, List<Usuario> listDeArbitros, ArbitrosRecyclerView_Interface clickArbitrosRecyclerViewInterface){
        this.mctx = ctx;
        this.mListaDeUsuarios = listDeArbitros;
        this.clickArbitrosRecyclerViewInterface = clickArbitrosRecyclerViewInterface;
    }


    @NonNull
    @Override
    public ArbitroViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.lista_de_arbitros, viewGroup, false);

        itemView.setBackgroundColor(Color.parseColor("#F0FFFF"));
        return new ArbitroViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ArbitroViewHolder holder, int position) {

        MetodosPublicos metodosPublicos = new MetodosPublicos();
        Usuario arbitro = mListaDeUsuarios.get(position);
        Endereco endereco = arbitro.getEndereco();
        String endereco_arbitro = "Cidade: Não informada.";
        String distancia_disponivel = "Disponivel em KM: Não informado.";

        String tipo_primeiro_esporte = "Esporte: Não informado.";
        String primeiro_valor_cobrado = "Valor: Não informado.";

        String tipo_segundo_esporte = "Esporte: Não informado.";
        String segundo_valor_cobrado = "Valor: Não informado.";

        String tipo_terceiro_esporte = "Esporte: Não informado.";
        String terceiro_valor_cobrado = "Valor: Não informado.";

        if(arbitro != null){

            String tipo = "";
            String status = "";
            if(arbitro.getTipoDeUsuario() == 1){
                tipo = "Árbitro";
            }

            if(endereco != null){
                endereco_arbitro = arbitro.getEndereco().getCidade() + "/" + metodosPublicos.GetSiglaEstado(arbitro.getEndereco().getEstado());
            }

            if(arbitro.getListaDeEsportes() != null){

               // holder.linear_segundo_esporte.setVisibility(View.GONE);
               // holder.linear_terceiro_esporte.setVisibility(View.GONE);

                holder.viewSegundoEsporte.setVisibility(View.GONE);
                holder.viewSegundoValorCobrado.setVisibility(View.GONE);
                holder.viewTerceiroEsporte.setVisibility(View.GONE);
                holder.viewTerceiroValorCobrado.setVisibility(View.GONE);

                for (Esporte user_esporte: arbitro.getListaDeEsportes()) {

                    if(user_esporte != null && user_esporte.getIdentificadorEsporte() != null && user_esporte.getIdentificadorEsporte().equals("primeiroEsporte")){

                        primeiro_valor_cobrado = "Valor: " + user_esporte.getValor();
                        tipo_primeiro_esporte = user_esporte.getNome();

                        holder.viewPrimeiroEsporte.setText(tipo_primeiro_esporte);
                        holder.viewPrimeiroValorCobrado.setText(primeiro_valor_cobrado);
                    }

                    if(user_esporte != null && user_esporte.getIdentificadorEsporte() != null && user_esporte.getIdentificadorEsporte().equals("segundoEsporte")) {
                        holder.linear_segundo_esporte.setVisibility(View.VISIBLE);
                        holder.viewSegundoEsporte.setVisibility(View.VISIBLE);
                        holder.viewSegundoValorCobrado.setVisibility(View.VISIBLE);

                        segundo_valor_cobrado = "Valor: " + user_esporte.getValor();
                        tipo_segundo_esporte = user_esporte.getNome();

                        holder.viewSegundoEsporte.setText(tipo_segundo_esporte);
                        holder.viewSegundoValorCobrado.setText(segundo_valor_cobrado);

                    }

                    if(user_esporte != null && user_esporte.getIdentificadorEsporte() != null && user_esporte.getIdentificadorEsporte().equals("terceiroEsporte")) {

                        holder.linear_terceiro_esporte.setVisibility(View.VISIBLE);
                        holder.viewTerceiroEsporte.setVisibility(View.VISIBLE);
                        holder.viewTerceiroValorCobrado.setVisibility(View.VISIBLE);

                        terceiro_valor_cobrado = "Valor: " + user_esporte.getValor();
                        tipo_terceiro_esporte = user_esporte.getNome();

                        holder.viewTerceiroEsporte.setText(tipo_terceiro_esporte);
                        holder.viewTerceiroValorCobrado.setText(terceiro_valor_cobrado);
                    }

                }
            }

          String nomeArbitro = arbitro.getNome().substring(0,1).toUpperCase() + arbitro.getNome().substring(1);
          String sobreNomeArbitro = arbitro.getSobreNome().substring(0,1).toUpperCase() + arbitro.getSobreNome().substring(1);

          holder.viewNomeArbitro.setText(nomeArbitro + " " + sobreNomeArbitro);
          holder.viewCidadeArbitro.setText(endereco_arbitro);


          if(arbitro.getDistanciaDisponivel() > 0)
                        distancia_disponivel = "Disponivel em KM: " + String.valueOf(arbitro.getDistanciaDisponivel());

          holder.viewDistanciaDisponivel.setText(distancia_disponivel);

        }
    }

    @Override
    public int getItemCount() {
        return mListaDeUsuarios.size();
    }

    public void remove(int position) {
        if (position < 0 || position >= mListaDeUsuarios.size()) {
            return;
        }

        Usuario user = mListaDeUsuarios.get(position);
        mListaDeUsuarios.remove(position);
       // arbitroDatabase.Remover(user.getID());
        notifyItemRemoved(position);
    }

    protected class ArbitroViewHolder extends RecyclerView.ViewHolder{

        protected TextView viewNomeArbitro;
        protected TextView viewPrimeiroEsporte;
        protected TextView viewPrimeiroValorCobrado;
        protected TextView viewSegundoEsporte;
        protected TextView viewSegundoValorCobrado;
        protected TextView viewTerceiroEsporte;
        protected TextView viewTerceiroValorCobrado;

        protected TextView viewCidadeArbitro;
        protected TextView viewDistanciaDisponivel;
        protected TextView viewStatusPartida;

        LinearLayout linear_segundo_esporte;
        LinearLayout linear_terceiro_esporte;



        public ArbitroViewHolder(@NonNull View itemView) {
            super(itemView);

            linear_segundo_esporte = (LinearLayout) itemView.findViewById(R.id.linearSegundoEsporte);
            linear_terceiro_esporte = (LinearLayout) itemView.findViewById(R.id.linearTerceiroEsporte);

            viewNomeArbitro = (TextView)itemView.findViewById(R.id.txt_nome_arbitro);
            viewPrimeiroEsporte = (TextView)itemView.findViewById(R.id.txt_primeiro_esporte);
            viewPrimeiroValorCobrado = (TextView)itemView.findViewById(R.id.txt_primeiro_valor_arbitragem);

            viewSegundoEsporte = (TextView)itemView.findViewById(R.id.txt_segundo_esporte);
            viewSegundoValorCobrado = (TextView)itemView.findViewById(R.id.txt_segundo_valor_arbitragem);

            viewTerceiroEsporte = (TextView)itemView.findViewById(R.id.txt_terceiro_esporte);
            viewTerceiroValorCobrado = (TextView)itemView.findViewById(R.id.txt_terceiro_valor_arbitragem);

            viewCidadeArbitro = (TextView)itemView.findViewById(R.id.txt_cidade);
            viewDistanciaDisponivel = (TextView)itemView.findViewById(R.id.txt_distancia_disponivel);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickArbitrosRecyclerViewInterface.onCustomClick(mListaDeUsuarios.get(getLayoutPosition()));
                }
            });
        }
    }
}
