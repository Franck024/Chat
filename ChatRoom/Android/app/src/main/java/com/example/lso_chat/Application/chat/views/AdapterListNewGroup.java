package com.example.lso_chat.Application.chat.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lso_chat.Application.Activity.ControllerChat;
import com.example.lso_chat.R;

import java.util.ArrayList;

public class AdapterListNewGroup extends RecyclerView.Adapter<AdapterListNewGroup.MyViewHolderChat> {
    Context context;
    ArrayList<String> arrayList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemCLick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        listener = clickListener;
    }

    public AdapterListNewGroup(Context ct, ArrayList<String> a) {
        context = ct;
        arrayList = a;
    }

    @NonNull
    @Override
    public AdapterListNewGroup.MyViewHolderChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inserisce le chat nella RecycleView
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_listuser, parent, false);
        return new AdapterListNewGroup.MyViewHolderChat(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListNewGroup.MyViewHolderChat holder, int position) {
        holder.textViewNome.setText(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolderChat extends RecyclerView.ViewHolder {
        private TextView textViewNome;
        private ConstraintLayout constraintLayout;
        private Button bottonerimuovi;

        public MyViewHolderChat(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            textViewNome = itemView.findViewById(R.id.textViewUserName);
            constraintLayout = itemView.findViewById(R.id.cardListUserChat);
            //Rimozione utente dalla lista: listener per il bottone di rimozione.
            bottonerimuovi = itemView.findViewById(R.id.bottonerimuovi);
            bottonerimuovi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemCLick(getAdapterPosition());
                }
            });
        }


    }

}
