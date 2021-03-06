package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class forgetpassword extends AppCompatActivity {
    private Toolbar toolbar;

    private EditText email;
    private Button next;
    private Database db;
    String userid,username, useremail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        //set Status bar
        setStatus();
        //set Toolbar
        initToolbar();

        db = new Database(this);

        email = (EditText) findViewById(R.id.et_newemail);
        next = (Button) findViewById(R.id.btn_next);


//        useremail = "amrojiwen@gmail.com";

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useremail = email.getText().toString().trim();
                if (TextUtils.isEmpty(useremail)) {
                    Log.d("email",useremail);
                    Toast.makeText(forgetpassword.this, "Please input email.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (isExistUserEmail(useremail)) {
                    Intent intent = new Intent(forgetpassword.this, emailverification.class);
                    startActivity(intent);
                    finish();
                    return;
                } else {
                    Toast.makeText(forgetpassword.this, "This email not exist.", Toast.LENGTH_SHORT).show();
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

                Intent intent = new Intent(forgetpassword.this, Login.class);
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

    private boolean isExistUserEmail(String userEmail) {

        boolean has_userEmail = false;

        db.open();

        Cursor c = db.checkEmail(userEmail);
        c.moveToFirst();
        if (c.getCount() != 0) {

            userid = c.getString(c.getColumnIndex("userid"));
            username = c.getString(c.getColumnIndex("userName"));

            SharedPreferences sp = getSharedPreferences("findbackpassword", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("userid", userid);
            editor.putString("email", useremail);
            editor.commit();
            has_userEmail = true;
        }

        db.close();
        return has_userEmail;
    }
}
