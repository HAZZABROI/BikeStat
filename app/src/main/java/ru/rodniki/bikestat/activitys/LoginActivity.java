package ru.rodniki.bikestat.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import ru.rodniki.bikestat.R;
import ru.rodniki.bikestat.database.ConnectionClass;

public class LoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button loginButton;
    TextView signupText, loginText;
    CardView cardView;

    ConstraintLayout passLayout;
    Animation animL;

    final int newTopMargin = 300;
    final int startTopMargin = 0;
    boolean isRegOpen = false;

    ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);
        cardView = findViewById(R.id.cardViewMap);
        loginText = findViewById(R.id.loginText);
        passLayout = findViewById(R.id.rePassLayout);

        connectionClass = new ConnectionClass();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                //TODO: Complete register
//                DoRegister doRegister  = new DoRegister();
//                doRegister.execute("");
            }
        });

        passLayout.setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if(!isRegOpen){
                    passLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!isRegOpen){

                }else{
                    passLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isRegOpen){
                    animL = animCreate(newTopMargin, false);
                    loginText.setText("Регистрация");
                    loginButton.setText("Зарегистрироваться");
                    signupText.setText("Назад к входу");
                    animL.setDuration(600);
                    passLayout.startAnimation(animL);
                    isRegOpen = true;
                }else{
                    animL = animCreate(startTopMargin, true);
                    loginText.setText("Вход");
                    loginButton.setText("Войти");
                    signupText.setText("Зарегистрироваться");
                    animL.setDuration(600);
                    passLayout.startAnimation(animL);
                    isRegOpen = false;
                }



            }
        });
    }

    public class DoRegister extends AsyncTask<String,String,String>
    {
        String namestr = username.getText().toString();
        String passstr = password.getText().toString();
        String z = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            Toast.makeText(LoginActivity.this, "Подождите...",Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            if(namestr.trim().equals("") || passstr.trim().equals(""))
                z = "Пожалуйста заполните все поля";
            else{
                try{
                    Connection conn = connectionClass.CONN();
                    if(conn == null){
                        String query = "insert into users values('"+namestr+"','"+passstr+"')";

                        Statement stmnt = conn.createStatement();
                        stmnt.executeUpdate(query);

                        z = "Регистрация успешна";
                        isSuccess=true;
                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    Log.e("Error",  ex.toString());
                    z = "Неизвестная ошибка";
                }
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            if(isSuccess){
                Toast.makeText(getBaseContext(),""+z, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    public Animation animCreate(int margin, boolean back) {
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) passLayout.getLayoutParams();
                if(back) {
                    params.topMargin = (int) (margin * interpolatedTime * -1);
                }else{
                    params.topMargin = (int) (margin * interpolatedTime);
                }
                passLayout.setLayoutParams(params);
            }
        };
        return anim;
    }
}