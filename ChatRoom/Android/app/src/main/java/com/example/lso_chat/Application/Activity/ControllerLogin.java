package com.example.lso_chat.Application.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lso_chat.Application.Application.Chat_Main;
import com.example.lso_chat.Application.Controller.Controller;
import com.example.lso_chat.R;


public class ControllerLogin extends AppCompatActivity{
    private EditText editTextUsername, editTextPassword;
    private TextView textViewRegister;
    private Button btnLogin;
    private Animation animationBtn = null, animationTextView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Controller controller = Controller.getIstanza();
        controller.setLoginActivity(this);

        //Animazione cambio gradiente sfondo.
        AnimationDrawable animDrawable= (AnimationDrawable) findViewById(R.id.layout_login).getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();
        //Fine animazione cambio gradiente sfondo.

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        textViewRegister = findViewById(R.id.textViewRegister);

        animationBtn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_btn);
        animationTextView = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation_textview);

        animazioni();


        editTextUsername.setOnClickListener(v -> editTextUsername.startAnimation(animationTextView));
        editTextPassword.setOnClickListener(v -> editTextPassword.startAnimation(animationTextView));
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginClick(controller);  

            }
        });

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {onRegisterClick(view);
            }
        });
        Context context = getApplicationContext();

    }



    private void onLoginClick(Controller controller){

        String username =  editTextUsername.getText().toString().trim();
        String userPassword = editTextPassword.getText().toString().trim();
        controller.infoLogin(username,userPassword);

    }

    private void onRegisterClick(View v){
        Intent apriRegistrazione = new Intent (ControllerLogin.this, ControllerRegistrazione.class);
        startActivity(apriRegistrazione);
    }


    private void animazioni(){
        editTextUsername.setTranslationX(1000);
        editTextUsername.animate().translationX(0).setDuration(800).setStartDelay(400).start();
        editTextPassword.setTranslationX(1000);
        editTextPassword.animate().translationX(0).setDuration(800).setStartDelay(600).start();
        btnLogin.setTranslationX(1000);
        btnLogin.animate().translationX(0).setDuration(800).setStartDelay(800).start();
        textViewRegister.setTranslationY(1000);
        textViewRegister.animate().translationY(0).setDuration(800).setStartDelay(800).start();

    }

    public void LoginError(){
        runOnUiThread(()->{
            Toast.makeText(this,"Dati errati.Riprovare",Toast.LENGTH_SHORT).show();
        });

    }

    public void GenericError(){
        runOnUiThread(()->{
            Toast.makeText(this,"Errore network",Toast.LENGTH_SHORT).show();
        });
    }

    public void SignOK(){
        runOnUiThread(()->{
            Toast.makeText(this, "Registrazione completata", Toast.LENGTH_LONG).show();
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
