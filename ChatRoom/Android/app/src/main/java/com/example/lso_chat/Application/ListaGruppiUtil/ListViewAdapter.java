package com.example.lso_chat.Application.ListaGruppiUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lso_chat.Application.Activity.CercaGruppi;
import com.example.lso_chat.Application.Entities.Stanza;
import com.example.lso_chat.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListViewAdapter extends BaseAdapter {

    //Variablili
    Context mContext;
    LayoutInflater inflater;
    List<com.example.lso_chat.Application.Entities.Stanza> modellist;
    ArrayList<com.example.lso_chat.Application.Entities.Stanza> arraylist;
    CercaGruppi activitycercagruppi;

    //costruttore

    public ListViewAdapter(Context context, List<com.example.lso_chat.Application.Entities.Stanza> modellist) {
        this.mContext = context;
        this.modellist = modellist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<com.example.lso_chat.Application.Entities.Stanza>();
        this.arraylist.addAll(modellist);
    }

    public void setActivitycercagruppi(CercaGruppi cg){
        this.activitycercagruppi=cg;
    }
    public class ViewHolder {
        TextView mTitoloTv, mCodiceTv;
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
            view = inflater.inflate(R.layout.rigagruppo, null);

            holder.mTitoloTv = view.findViewById(R.id.titoloRiga);
            holder.mCodiceTv = view.findViewById(R.id.rigaCodiceGruppo);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //Setta il risultato nelle textview
        holder.mTitoloTv.setText(modellist.get(posizione).getNome());
        //Faccio il cast a String del codice della stanza.
        String CodiceStanza=String.valueOf(modellist.get(posizione).getCodice());
        holder.mCodiceTv.setText(CodiceStanza);

        //Listview quando si preme sulla riga
        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                activitycercagruppi.copiasuclipboard(modellist.get(posizione).getCodice(),modellist.get(posizione).getNome());
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
            for (Stanza model : arraylist) {
                if (model.getNome().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    modellist.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }


}

