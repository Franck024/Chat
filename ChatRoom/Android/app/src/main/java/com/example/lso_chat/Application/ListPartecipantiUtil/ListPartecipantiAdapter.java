package com.example.lso_chat.Application.ListPartecipantiUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.example.lso_chat.Application.Entities.Richiesta;
import com.example.lso_chat.Application.RequestListUtil.ListViewAdapter;
import com.example.lso_chat.R;

import java.util.ArrayList;
import java.util.List;

public class ListPartecipantiAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<String> arraylist;
    LayoutInflater inflater;
    ArrayList<String> modellist;

    public ListPartecipantiAdapter(Context context, ArrayList<String> modellist) {
        this.mContext = context;
        this.modellist = modellist;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<String>();
        this.arraylist.addAll(this.modellist);
        inflater = LayoutInflater.from(mContext);
    }

    public class ViewHolder {
        TextView nomepartecipante;
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
    public View getView(int posizione, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.riga_partecipanti, null);
            holder.nomepartecipante = view.findViewById(R.id.nomepartecipante);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.nomepartecipante.setText(arraylist.get(posizione));

        return view;
    }
}
