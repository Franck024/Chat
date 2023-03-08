package com.example.lso_chat.Application.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.service.controls.Control;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.lso_chat.Application.Controller.Controller;
import com.example.lso_chat.R;

public class PopupNuovogruppo extends Activity {
    private TextView txtCreaGruppo;
    private Button btnInviaRichiesta;
    private TextView CodiceGruppo;
    Controller controller = Controller.getIstanza();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_nuovogruppo);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.8), (int) (height*0.2));
        WindowManager.LayoutParams params= getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x=0;
        params.y=-20;
        getWindow().setAttributes(params);

        txtCreaGruppo = findViewById(R.id.txtCreaGruppo);

        txtCreaGruppo.setOnClickListener(view ->
                startActivity(new Intent(PopupNuovogruppo.this, ControllerCreazioneGruppo.class)));
        CodiceGruppo= findViewById(R.id.CodiceGruppo);
        btnInviaRichiesta= findViewById(R.id.btn_inviaCodGruppo);
        btnInviaRichiesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!CodiceGruppo.getText().toString().isEmpty()) {
                    controller.inviaRichiesta(CodiceGruppo.getText().toString());
                    finish();
                    }
            }
        });
    }
}
