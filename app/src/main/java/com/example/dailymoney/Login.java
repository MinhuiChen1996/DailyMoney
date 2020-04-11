package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private Database db;
    private TextView tvRegister, tvfindpw;
    private Toolbar toolbar;
    private Button BtnLogin;
    private String username, pw, encryptionPw, encryptionUsername, usPw, userid;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new Database(this);

        //set Status bar
        setStatus();
        initToolbar();
        initLogin();

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
    }

    public boolean getloginstatus() {
        return sp.getBoolean("isLogin", false);
    }

    private void checkLoginStatus() {
        sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        if (getloginstatus()) {
            if(quickaccount()){
                Intent intent = new Intent(Login.this, Record.class);
                startActivity(intent);
                finish();
            }else{
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void initLogin() {
        etUsername = (EditText) findViewById(R.id.et_user_name);
        etPassword = (EditText) findViewById(R.id.et_pw);



        tvRegister = (TextView) findViewById(R.id.tv_register);
        tvfindpw = (TextView) findViewById(R.id.tv_find_pw);
        BtnLogin = findViewById(R.id.btn_login);

        checkLoginStatus();

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });

        tvfindpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, forgetpassword.class);
                startActivity(intent);
                finish();
            }
        });

        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etUsername.getText().toString();
                pw = etPassword.getText().toString();
                encryptionUsername = MD5Utils.md5(username);
                encryptionPw = MD5Utils.md5(pw);
                Log.d("encryptionPw", encryptionPw);

                usPw = findPw(encryptionUsername);

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(Login.this, "Please input username.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(pw)) {
                    Toast.makeText(Login.this, "Please input password.", Toast.LENGTH_SHORT).show();
                    return;
                } else if ((usPw != null && !TextUtils.isEmpty(usPw) && !encryptionPw.equals(usPw))) {
                    Toast.makeText(Login.this, "The username and password entered are not consistent.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (encryptionPw.equals(usPw)) {

                    Toast.makeText(Login.this, "Login successful.", Toast.LENGTH_SHORT).show();
                    saveLoginStatus(true, username);
                    Login.this.finish();
                    startActivity(new Intent(Login.this, MainActivity.class));
                    return;
                } else {
                    Toast.makeText(Login.this, "This username does not exist.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String findPw(String userName) {
        db.open();
        String userPassword = "";
        Cursor c = db.getUsPw(userName);
        c.moveToFirst();
        if (c.getCount() != 0) {
            userid = c.getString(c.getColumnIndex("userid"));
            userPassword = c.getString(c.getColumnIndex("userPassword"));
        }
        db.close();
        return userPassword;
    }

    private void saveLoginStatus(boolean status, String userName) {
        sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("userid", userid);
        editor.putBoolean("isLogin", status);
        editor.putString("loginUserName", userName);
        editor.commit();
    }

    private boolean quickaccount(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        Boolean quickaccounting=prefs.getBoolean("quickaccounting",false);
        return quickaccounting;
    }

}
