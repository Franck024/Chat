package com.example.lso_chat.Application.chat.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lso_chat.Application.Activity.ControllerChat;
import com.example.lso_chat.Application.Controller.Controller;
import com.example.lso_chat.Application.Entities.Stanza;
import com.example.lso_chat.R;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/*
Mostra la lista dei gruppi attivi
 */

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyViewHolderChat> {
    Context context;
    List<Stanza> listGruppi;
    public AdapterChat(Context ct){
        context = ct;
        listGruppi = new ArrayList<Stanza>(Controller.getIstanza().getUtente().getStanze());

    }


    @NonNull
    @Override
    public MyViewHolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inserisce le chat nella RecycleView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new MyViewHolderChat(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderChat holder, int position) {
        // Cambia il nome in item_chat
        holder.textViewNome.setText(listGruppi.get(position).getNome());
        if(listGruppi.get(position).getUltimo_msg()!=null){
            Log.d("ok","Il gruppo "+listGruppi.get(position).getNome()+" ha un messaggio!");
            holder.textViewMittente.setText(listGruppi.get(position).getUltimo_msg().getMittente().getusername()+": ");
            holder.textViewUltimoMessaggio.setText(listGruppi.get(position).getUltimo_msg().getCorpo());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            String formattedDateTime = listGruppi.get(position).getUltimo_msg().getTempo().format(formatter);
            holder.textViewOra.setText(formattedDateTime);
            DateTimeFormatter formatterdata = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDateTime2 = listGruppi.get(position).getUltimo_msg().getTempo().format(formatterdata);
            holder.textViewData.setText(formattedDateTime2);
        }
        else{
            holder.textViewMittente.setText("");
            holder.textViewOra.setText("");
            holder.textViewUltimoMessaggio.setText("");
            holder.textViewData.setText("");

        }
        //Apertura chat
        //holder.bind(listGruppi.get(position).getNome(), 1); //prova
        ConstraintLayout layout = holder.getConstraintLayout();
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context ctx = layout.getContext();
                Log.d("ok","hai selezionato la stanza"+listGruppi.get(position).getNome()+ "Con ID: "+listGruppi.get(position).getCodice());
                Intent intent = new Intent(ctx, ControllerChat.class);
                intent.putExtra("key",String.valueOf(listGruppi.get(position).getCodice()));

                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listGruppi.size();
    }

    public class MyViewHolderChat extends RecyclerView.ViewHolder{
        //Una sorta di controller di item_chat
        private TextView textViewNome;
        private ConstraintLayout constraintLayout;
        private TextView textViewMittente;
        private TextView textViewUltimoMessaggio;
        private TextView textViewOra;
        private TextView textViewData;

        public MyViewHolderChat(@NonNull View itemView){
            super(itemView);
            textViewNome = itemView.findViewById(R.id.textViewChatName);
            constraintLayout = itemView.findViewById(R.id.cardListChat);
            textViewMittente= itemView.findViewById(R.id.textViewMittente);
            textViewUltimoMessaggio= itemView.findViewById(R.id.textViewUltimoMessaggio);
            textViewOra= itemView.findViewById(R.id.textViewOra);
            textViewData= itemView.findViewById(R.id.textViewData);

        }

        /////////////////////////////////////////////////
        //per aprire la chat
        public void bind(String txtNome, double unreadMessageCount){
            textViewNome.setText(txtNome);
            if (unreadMessageCount > 0) textViewNome.setTypeface(null, Typeface.BOLD);
        }
        public ConstraintLayout getConstraintLayout() {
            return constraintLayout;
        }
        //////////////////////////////////////////////////
    }
}
