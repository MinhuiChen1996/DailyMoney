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

import com.smailnet.emailkit.Draft;
import com.smailnet.emailkit.EmailKit;

import java.util.Random;

public class emailverification extends AppCompatActivity {
    private Toolbar toolbar;
    SharedPreferences sp;
    String useremail, username;
    int code;
    private EditText verficeationCode;
    private Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailverification);

        //set Status bar
        setStatus();
        //set Toolbar
        initToolbar();

        useremail = getemail();

        username = getuserName();
        code = gen();

        sendMailTest(useremail,code);
        verficeationCode = (EditText) findViewById(R.id.et_code);
        next = (Button) findViewById(R.id.btn_next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputcode = verficeationCode.getText().toString();
                if(inputcode.equals(String.valueOf(code))){
                    Intent intent = new Intent(emailverification.this, modifypassword.class);
                    startActivity(intent);
                    finish();
                }else if(TextUtils.isEmpty(inputcode)){
                    Toast.makeText(emailverification.this, "Please input email.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(emailverification.this, "Please input correct code.", Toast.LENGTH_SHORT).show();
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

                Intent intent = new Intent(emailverification.this, forgetpassword.class);
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

    private String getemail() {
        sp = getSharedPreferences("findbackpassword", MODE_PRIVATE);
        return sp.getString("email", "");
    }

    private void sendMailTest(String email, int code){
//初始化框架
        EmailKit.initialize(this);

//配置发件人邮件服务器参数
        EmailKit.Config config = new EmailKit.Config()
                .setMailType(EmailKit.MailType.$163)     //选择邮箱类型，快速配置服务器参数
                .setAccount("dailymoney@163.com")             //发件人邮箱
                .setPassword("XYLCVQVQLTESHCHS");                  //密码或授权码

//设置一封草稿邮件
        Draft draft = new Draft()
                .setNickname("Daily Money Application")                      //发件人昵称
                .setTo(email)                        //收件人邮箱
                .setSubject("Verification Email From Daily Money")             //邮件主题
                .setText("Dear User " +"\r\n\r\n"+"To verify this email address belongs to you, enter the code below on the email verification page:"+"\r\n"+code +"\r\n"+"This code only validate once.");                 //邮件正文

//使用SMTP服务发送邮件
        EmailKit.useSMTPService(config)
                .send(draft, new EmailKit.GetSendCallback() {
                    @Override
                    public void onSuccess() {
                        Log.i("tag", "发送成功！");
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        Log.i("tag", "发送失败，错误：" + errMsg);
                    }
                });
    }

    private String getuserName() {
        sp = getSharedPreferences("findbackpassword", MODE_PRIVATE);
        return sp.getString("username", "");
    }
    public int gen() {
        Random r = new Random( System.currentTimeMillis() );
        return 10000 + r.nextInt(20000);
    }
}
