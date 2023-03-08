package com.example.lso_chat.Application.chat.views;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.lso_chat.Application.Entities.Messaggio;
import com.example.lso_chat.databinding.ItemContainerNuovoUtenteBinding;
import com.example.lso_chat.databinding.ItemContainerReceivedMessageBinding;
import com.example.lso_chat.databinding.ItemContainerSentMessageBinding;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AdapterMessaggio extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final ArrayList<Messaggio> chatMessages;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    public static final int VIEW_TYPE_NUOVOUTENTE = 3;

    public AdapterMessaggio( ArrayList<Messaggio> chatMessages, String senderId) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_RECEIVED)
            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),parent,false));
        if (viewType == VIEW_TYPE_NUOVOUTENTE)
            return new NuovoUtenteViewHolder(ItemContainerNuovoUtenteBinding.inflate(LayoutInflater.from(parent.getContext())
            ,parent,false));
        else
            return new SentMessageViewHolder(ItemContainerSentMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT)
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        else if(getItemViewType(position)== VIEW_TYPE_NUOVOUTENTE)
            ((NuovoUtenteViewHolder) holder).setData(chatMessages.get(position));
        else
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position));
    }

    @Override
    public int getItemViewType(int position){
        if( chatMessages.get(position).getMittente().getusername().equals(senderId))
            return VIEW_TYPE_SENT;
        if(chatMessages.get(position).getMittente().getusername().contentEquals("system"))
            return VIEW_TYPE_NUOVOUTENTE;
        return VIEW_TYPE_RECEIVED;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(Messaggio messaggio){
            binding.textViewMessaggioSent.setText(messaggio.getMittente().getusername()+": "+ messaggio.getCorpo());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
            String formattedDateTime = messaggio.getTempo().format(formatter);
            binding.textViewOrario.setText(formattedDateTime);

        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding){
            super(itemContainerReceivedMessageBinding.getRoot());
            binding= itemContainerReceivedMessageBinding;
        }

        void setData(Messaggio messaggio){
            binding.textNomeMittente.setText(messaggio.getMittente().getusername());
            binding.textViewMessaggioReceived.setText(messaggio.getCorpo());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
            String formattedDateTime = messaggio.getTempo().format(formatter);

            binding.textViewOrarioReceived.setText(formattedDateTime);
        }
    }

    static class NuovoUtenteViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerNuovoUtenteBinding binding;

        NuovoUtenteViewHolder(ItemContainerNuovoUtenteBinding itemContainerNuovoUtenteBinding){
            super(itemContainerNuovoUtenteBinding.getRoot());
            binding= itemContainerNuovoUtenteBinding;
        }

        void setData(Messaggio messaggio){
            binding.textViewNuovoUtente.setText(messaggio.getCorpo());
        }
    }
}
