package ru.rodniki.bikestat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().equals("username") && password.getText().toString().equals("password")){
                    Toast.makeText(MainActivity.this, "Успешный вход",Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(MainActivity.this, "Не успешный",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}