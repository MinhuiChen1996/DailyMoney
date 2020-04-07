package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class recordinfo extends AppCompatActivity {

    private Toolbar toolbar;
    private Database db;
    Intent intent;

    SharedPreferences sp;

    String type, cate, name, memo, account, time, date, hourMinutes, amount, recordid;

    ImageView iv_cate;
    TextView tv_name, tv_type, tv_amount, tv_memo, tv_time, tv_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordinfo);

        //set Status bar
        setStatus();
        initToolbar();

        db = new Database(this);

        String userid = getuserid();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recordid = extras.getString("recordId");
            //The key argument here must match that used in the other activity
        } else {
            intent = new Intent(recordinfo.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Can't find this record", Toast.LENGTH_SHORT).show();
            finish();
        }

        initReordinfo(recordid, userid);

        Button btn_modify = (Button) findViewById(R.id.btn_modify);
        btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.equals("Income")) {
                    intent = new Intent(recordinfo.this, modifyincome.class);
                    intent.putExtra("recordId", recordid);
                    startActivity(intent);
                    finish();
                } else {
                    intent = new Intent(recordinfo.this, modify.class);
                    intent.putExtra("recordId", recordid);
                    startActivity(intent);
                    finish();
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

    // status bar
    private void setStatus() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlue));

    }


    // add menu in toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_btn, menu);
        return true;
    }

    // menu click event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                AlertDialog.Builder dialog = new AlertDialog.Builder(recordinfo.this);
                dialog.setTitle("Delete");
                dialog.setMessage("Do you want to delete this record?");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.open();
                        boolean result = db.deleteRocord(recordid);
                        if (result) {
                            Toast.makeText(recordinfo.this, "Delete the record", Toast.LENGTH_SHORT).show();
                            db.close();
                            intent = new Intent(recordinfo.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            db.close();
                            Toast.makeText(recordinfo.this, "Sorry, delete error.", Toast.LENGTH_SHORT).show();
                            intent = new Intent(recordinfo.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        }
                    }
                });
                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                dialog.show();
                break;

            case android.R.id.home:
                intent = new Intent(recordinfo.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private String getuserid() {
        sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        return sp.getString("userid", "");
    }

    private void initReordinfo(String rid, String uid) {
        db.open();
        Cursor c = db.getRecord(rid, uid);

        Log.d("detail2", rid + " " + uid);
        int i = c.getCount();

        if (c.getCount() > 0) {
            c.moveToFirst();
            type = c.getString(c.getColumnIndex("type"));
            cate = c.getString(c.getColumnIndex("cate"));
            name = c.getString(c.getColumnIndex("name"));
            memo = c.getString(c.getColumnIndex("memo"));
            account = c.getString(c.getColumnIndex("account"));
            amount = c.getString(c.getColumnIndex("amount"));
            date = c.getString(c.getColumnIndex("date"));
            hourMinutes = c.getString(c.getColumnIndex("time"));

            time = date + " " + hourMinutes;

            iv_cate = (ImageView) findViewById(R.id.iv_cate);
            tv_name = (TextView) findViewById(R.id.tv_name);
            tv_type = (TextView) findViewById(R.id.tv_type);
            tv_time = (TextView) findViewById(R.id.tv_time);
            tv_amount = (TextView) findViewById(R.id.tv_amount);
            tv_memo = (TextView) findViewById(R.id.tv_memo);
            tv_account = (TextView) findViewById(R.id.tv_account);

            tv_name.setText(name);
            tv_type.setText(type);
            tv_time.setText(time);
            tv_amount.setText(amount);
            tv_memo.setText(memo);
            tv_account.setText(account);
            if (memo.equals("")) {
                tv_memo.setText("no memo");
            }
            if (account.equals("")) {
                tv_account.setText("no linked account");
            }
            String image = cate;
            switch (image) {
                case "food":
                    iv_cate.setImageResource(R.drawable.food);
                    break;
                case "daily":
                    iv_cate.setImageResource(R.drawable.daily);
                    break;
                case "clothes":
                    iv_cate.setImageResource(R.drawable.clothes);
                    break;
                case "traffic":
                    iv_cate.setImageResource(R.drawable.traffic);
                    break;
                case "salary":
                    iv_cate.setImageResource(R.drawable.salary);
                    break;
                case "sale":
                    iv_cate.setImageResource(R.drawable.sale);
                    break;
                case "invest":
                    iv_cate.setImageResource(R.drawable.invest);
                    break;
                case "others":
                    iv_cate.setImageResource(R.drawable.others);
                    break;
            }
            c.moveToNext();
        } else {
            intent = new Intent(recordinfo.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Can't find this record in DB", Toast.LENGTH_SHORT).show();
            finish();
        }
        db.close();
    }

}
