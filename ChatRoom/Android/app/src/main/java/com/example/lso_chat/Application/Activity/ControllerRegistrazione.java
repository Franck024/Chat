package com.example.lso_chat.Application.Activity;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lso_chat.Application.Controller.Controller;
import com.example.lso_chat.R;
import com.google.android.material.textfield.TextInputLayout;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerRegistrazione extends AppCompatActivity{
    private EditText editTextNome, editTextPassword, editTextConfirmPassword;
    private Button btnRegister;
    private TextInputLayout editTextPasswordLayout,editTextConfirmPasswordLayout,EditTextUsernameLayout;
    private Animation animationBtn = null, animationTextView = null;
    private TextView textCredentialRules;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrazione);

        //Animazione cambio gradiente sfondo.
        AnimationDrawable animDrawable= (AnimationDrawable) findViewById(R.id.layout_registrazione).getBackground();
        animDrawable.setEnterFadeDuration(10);
        animDrawable.setExitFadeDuration(5000);
        animDrawable.start();
        //Fine animazione cambio gradiente sfondo.


        EditTextUsernameLayout = findViewById(R.id.editTextUsernameLayout);
        editTextNome = findViewById(R.id.editTextNome);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPasswordLayout = findViewById(R.id.editTextPasswordLayout);
        editTextConfirmPasswordLayout = findViewById(R.id.editTextConfirmPasswordLayout);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        animationBtn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_btn);
        animationTextView = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.animation_textview);
        Controller controller = Controller.getIstanza();
        controller.setRegistrazioneActivity(this);

        //animazioni();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkCredentials())
                {
                    Log.d("registrazione", "onClick: premuto su bottone registrazione, le credenziali rispettano i requisiti.");
                    String username =  editTextNome.getText().toString().trim();
                    String userPassword = editTextPassword.getText().toString().trim();
                    controller.infoRegistrazione(username,userPassword);
                }
            }
        });
        //Listener per quando cambia il testo nel textbox password
        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        //Quando cambio il testo azzera l'errore
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editTextPasswordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Listener per quando cambia il testo nel confermapassword
        editTextConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
        //Quando cambi il testo azzera l'errore
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editTextConfirmPasswordLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //Quando cambi il testo del campo username azzera l'errore
        editTextNome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                EditTextUsernameLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextNome.setOnClickListener(v -> editTextNome.startAnimation(animationTextView));
        editTextPassword.setOnClickListener(v -> editTextPassword.startAnimation(animationTextView));
        editTextConfirmPassword.setOnClickListener(v -> editTextConfirmPassword.startAnimation(animationTextView));
    }




    public void SignError(){
        runOnUiThread(()->{
        Toast.makeText(this, "Errore network", Toast.LENGTH_LONG).show();
        });
    }



    public void AlreadySigned(){
        runOnUiThread(()->{
        Toast.makeText(this, "L'utente è gia registrato", Toast.LENGTH_LONG).show();
        });
}


    private boolean checkCredentials() {
        String usernameText = editTextNome.getText().toString().trim();
        String passwordText = editTextPassword.getText().toString().trim();
        String confPasswordText = editTextConfirmPassword.getText().toString().trim();

        if (usernameText.isEmpty()) {
            editTextNome.setError("Campo obbligatorio.");
            editTextNome.requestFocus();
            return false;
        }


        if(!verificaUsername((usernameText))){
            EditTextUsernameLayout.setError(getResources().getString(R.string.usernamerules)); //Setto il testo dell'alert delle regole username con quello memorizzato nel file strings.xml https://stackoverflow.com/questions/46713418/android-studio-settext-with-string-set-in-code
            editTextNome.requestFocus();
            return false;
        }

        if (passwordText.isEmpty()) {
            editTextPasswordLayout.setError("Campo obbligatorio.");
            editTextPassword.requestFocus();
            return false;
        }

        if (confPasswordText.isEmpty()) {
            editTextConfirmPasswordLayout.setError("Campo obbligatorio.");
            editTextConfirmPassword.requestFocus();
            return false;
        }

        if (!verificaPass(passwordText)) {
            editTextPasswordLayout.setError(getResources().getString(R.string.passwordrules)); //Setto il testo dell'alert delle regole password con quello memorizzato nel file strings.xml https://stackoverflow.com/questions/46713418/android-studio-settext-with-string-set-in-code
            editTextPassword.requestFocus();
            return false;
        }

        if (!(passwordText.equals(confPasswordText))) {
            editTextPasswordLayout.setError("Le password non corrispondono.");
            editTextConfirmPasswordLayout.setError("Le password non corrispondono");
            editTextPassword.requestFocus();
            return false;
        }


        return true;
    }


    private boolean verificaUsername(String username) {
        Pattern path = Pattern.compile("([A-Za-z0-9]{5,10})");
        Matcher m = path.matcher(username);
        return m.matches();
    }


    private boolean verificaPass(String password) {
        Pattern path= Pattern.compile("(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{5,10}"); //https://stackoverflow.com/questions/19605150/regex-for-password-must-contain-at-least-eight-characters-at-least-one-number-a
        Matcher m = path.matcher(password);
        return m.matches();
    }

    private boolean verificaNumerica(String nome, String cognome, String numero) {
        if (nome.matches(".*\\d+.*")) {
            editTextNome.setError("Il nome deve essere formato da sole lettere.");
            editTextNome.requestFocus();
            return false;
        }return true;
    }



    private void animazioni() {
        editTextNome.setTranslationX(1000);
        editTextNome.animate().translationX(0).setDuration(300).setStartDelay(300).start();
        editTextPassword.setTranslationX(1000);
        editTextPassword.animate().translationX(0).setDuration(300).setStartDelay(350).start();
        editTextConfirmPassword.setTranslationX(1000);
        editTextConfirmPassword.animate().translationX(0).setDuration(300).setStartDelay(400).start();
        btnRegister.setTranslationY(1000);
        btnRegister.animate().translationY(0).setDuration(300).setStartDelay(450).start();
    }
}

