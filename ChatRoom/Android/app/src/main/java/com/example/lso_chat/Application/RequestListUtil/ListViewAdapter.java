package com.example.lso_chat.Application.RequestListUtil;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lso_chat.Application.Activity.CercaGruppi;
import com.example.lso_chat.Application.Activity.Richieste;
import com.example.lso_chat.Application.Controller.Controller;
import com.example.lso_chat.Application.Entities.Richiesta;
import com.example.lso_chat.Application.Entities.Stanza;
import com.example.lso_chat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    //Variablili
    Context mContext;
    LayoutInflater inflater;
    List<Richiesta> modellist;
    ArrayList<Richiesta> arraylist;
    Richieste activityrichieste;
    Controller controller = Controller.getIstanza();
    //costruttore

    public ListViewAdapter(Context context, List<Richiesta> modellist) {
        this.mContext = context;
        this.modellist = modellist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<Richiesta>();
        this.arraylist.addAll(modellist);
    }

    public void setActivityrichieste(Richieste rc){
        this.activityrichieste=rc;
    }
    public class ViewHolder {
        TextView mTitoloTv, mCodiceTv, mUsernameTv;
        Button ammetti,rifiuta;
    }

    @Override
    public int getCount() {
        return modellist.size();
    }

    @Override
    public Object getItem(int i) {
        return modellist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int posizione, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.rigarichiesta, null);

            holder.mTitoloTv = view.findViewById(R.id.labelnomegrupporequest);
            holder.mCodiceTv = view.findViewById(R.id.rigaCodiceGrupporequest);
            holder.mUsernameTv = view.findViewById(R.id.labelusernamerequest);
            holder.ammetti= view.findViewById(R.id.ammetti);
            holder.rifiuta=view.findViewById(R.id.rifiuta);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //Setta il risultato nelle textview
        holder.mTitoloTv.setText(modellist.get(posizione).getNomestanza());
        //Faccio il cast a String del codice della stanza.
        String CodiceStanza=String.valueOf(modellist.get(posizione).getIdstanza());
        holder.mCodiceTv.setText(CodiceStanza);
        //Setto il valore del nome dell'username.
        holder.mUsernameTv.setText(modellist.get(posizione).getUsername());
        //Listview quando si preme sulla riga
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //activitycercagruppi.copiasuclipboard(modellist.get(posizione).getCodice(),modellist.get(posizione).getNome());
            }
        });


        holder.ammetti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ok","hai premuto ammetti su"+modellist.get(posizione).getNomestanza()+ "con username:"+modellist.get(posizione).getUsername());
                controller.aggiungiUtente(modellist.get(posizione).getUsername(),modellist.get(posizione).getIdstanza());
            }
        });
        holder.rifiuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("ok","hai premuto ammetti su"+modellist.get(posizione).getNomestanza()+ "con username:"+modellist.get(posizione).getUsername());
                controller.rifiutaUtente(modellist.get(posizione).getUsername(),modellist.get(posizione).getIdstanza());
            }
        });
        return view;
    }

    //filtro
    public void filtro(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        modellist.clear();
        if (charText.length() == 0) {
            modellist.addAll(arraylist);
        } else {
            for (Richiesta model : arraylist) {
                if (model.getUsername().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    modellist.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }


}

