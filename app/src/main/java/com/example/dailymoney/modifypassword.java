package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class modifypassword extends AppCompatActivity {
    private Toolbar toolbar;
    SharedPreferences sp;
    private EditText newpassword;
    private Button next;
    private Database db;
    String userid, userpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifypassword);
        //set Status bar
        setStatus();
        //set Toolbar
        initToolbar();

        userid =  getuserid();

        db = new Database(this);

        newpassword = (EditText) findViewById(R.id.et_password);
        next = (Button) findViewById(R.id.btn_next);



        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userpassword = newpassword.getText().toString();
                if (TextUtils.isEmpty(userpassword)) {
                    Toast.makeText(modifypassword.this, "Please input new password.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    updatepassword(userid,userpassword);
                    SharedPreferences preferences = getSharedPreferences("findbackpassword", 0);
                    preferences.edit().remove("text").commit();
                }

            }
        });


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

                Intent intent = new Intent(modifypassword.this, forgetpassword.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // status bar
    private void setStatus() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlue));

    }

    private String getuserid() {
        sp = getSharedPreferences("findbackpassword", MODE_PRIVATE);
        return sp.getString("userid", "");
    }

    private void updatepassword(String userid, String passwrod){

        db.open();
        String md5Pw = MD5Utils.md5(passwrod);// encryption password by MD5
        long id = db.updatePassword(userid,md5Pw);
        if (id != -1) {
            Toast.makeText(getApplicationContext(), "Change password success!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(modifypassword.this, Login.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Sorry! change password is failed.", Toast.LENGTH_LONG).show();
        }
        db.close();

    }
}
