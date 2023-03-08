package com.example.lso_chat.Application.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lso_chat.Application.Controller.Controller;
import com.example.lso_chat.Application.Entities.Richiesta;
import com.example.lso_chat.Application.ListPartecipantiUtil.ListPartecipantiAdapter;
import com.example.lso_chat.R;

import java.util.ArrayList;
import java.util.List;

public class PopupInfoGruppo extends Activity {
    private TextView InfoCodiceGruppo;
    private ListView listview;
    private ListPartecipantiAdapter adaperpartecipanti;
    private Controller controller = Controller.getIstanza();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_infogruppo);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.8), (int) (height*0.4));
        WindowManager.LayoutParams params= getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x=0;
        params.y=-20;
        getWindow().setAttributes(params);
        controller.setPopupInfoGruppo(this);
        //InfoCodiceGruppo = findViewById(R.id.InfoCodiceGruppo);
        controller.recuperaPartecipanti();
    }
    public void riempilista(ArrayList<String> listpartecipanti){
        listview= findViewById(R.id.listviewpartecipanti);
        listpartecipanti.add(controller.getUtente().getusername());
        //Passa I risultati alla classe listViewAdapter
        adaperpartecipanti = new ListPartecipantiAdapter(this,listpartecipanti);
        //Gli passo l'istanza dell'activity all'adapter
        //adaperpartecipanti.setActivityrichieste(this);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listview.setAdapter(adaperpartecipanti);
            }
        });
    }

}
