package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class Register extends AppCompatActivity {
    private EditText email, password, username, password2;
    private String newUsername,newEmail,newPassowrd,newPassowrd2;
    DatePickerDialog datePickerDialog;
    private Database db;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //set Status bar
        setStatus();
        initToolbar();
        initRegsiter();

    }
    // status bar
    private void setStatus() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlue));
    }

    // Toolbar setting
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(Register.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initRegsiter(){

        db = new Database(this);

        username = (EditText) findViewById(R.id.et_user_name);
        password = (EditText) findViewById(R.id.et_pw);
        password2 = (EditText) findViewById(R.id.et_pw_again);
        email = (EditText) findViewById(R.id.et_email);

        Button FinButton = (Button)findViewById(R.id.btn_register);

        FinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getEditString();
                if (TextUtils.isEmpty(newUsername)) {
                    Toast.makeText(Register.this, "Please input Username.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(newPassowrd)) {
                    Toast.makeText(Register.this, "Please input Password.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(newPassowrd2)) {
                    Toast.makeText(Register.this, "Please input Password again.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!newPassowrd.equals(newPassowrd2)) {
                    Toast.makeText(Register.this, "Password is different!", Toast.LENGTH_SHORT).show();
                    return;
                }else if(isExistUserName(newUsername)){
                    Toast.makeText(Register.this, "This user is exist.", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    saveRegisterInfo(newUsername, newPassowrd,newEmail);
                    Register.this.finish();
                }
            }
        });
    }

    public void getEditString(){
         newUsername = username.getText().toString().trim();
         newEmail = email.getText().toString();
         newPassowrd = password.getText().toString();
         newPassowrd2 = password2.getText().toString();
    }
    private boolean isExistUserName(String userName){

        boolean has_userName=false;
        String md5Username = MD5Utils.md5(userName); // encryption username by MD5
        db.open();
        Cursor c = db.checkUser(md5Username);
        if(c.getCount() != 0){
            has_userName=true;
        }
        db.close();
        return has_userName;
    }


    private void saveRegisterInfo(String userName,String pw, String email){
        db.open();
        String md5Username = MD5Utils.md5(userName); // encryption username by MD5
        String md5Pw = MD5Utils.md5(pw);// encryption password by MD5
        long id = db.insertUser(md5Username,md5Pw, email);
        if(id != -1)
        {
            Toast.makeText(getApplicationContext(),"Registration success!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Sorry! register is failed.", Toast.LENGTH_LONG).show();
        }
        db.close();

    }

}
