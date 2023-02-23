package com.example.lso_chat.Application.Application;

import android.app.Application;
import android.content.Intent;

import com.example.lso_chat.Application.Activity.ControllerLogin;

public class Chat_Main extends Application {



    public void onCreate(){
          super.onCreate();
          Intent loginIntent = new Intent(Chat_Main.this, ControllerLogin.class);

     }

}
