package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    private EditText email,password;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = (EditText) findViewById(R.id.Email);
        password = (EditText) findViewById(R.id.Password);

        db = new Database(this);

        Button BtnRge = (Button)findViewById(R.id.btnregister);
        Button BtnBack =  findViewById(R.id.btnBack);
        Button BtnLogin = findViewById(R.id.btnlogin);

        BtnRge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });
        BtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String em = email.getText().toString();
                String pw = password.getText().toString();
                db.open();
                Cursor c = db.getUser(em,pw);
                if(c.getCount() != 0){
                    Intent intent = new Intent(Login.this, MainActivity.class);
//                    intent.putExtra("User", em);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Email or Password is incorrect!",
                            Toast.LENGTH_LONG).show();
                }
                db.close();
            }
        });
    }
}
