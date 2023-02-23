package com.example.lso_chat.Application.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.lso_chat.R;
/// QUESTA CLASSE DEVE ESSERE ELIMINATA
public class ControllerStanzaChat extends AppCompatActivity {
    Button infoGruppo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

      //  infoGruppo = findViewById(R.id.infoGruppo);

        infoGruppo.setOnClickListener(
                v -> startActivity(new Intent(ControllerStanzaChat.this, PopupInfoGruppo.class)));

    }
}
