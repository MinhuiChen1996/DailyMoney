package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class modifyincome extends AppCompatActivity {
    private Toolbar toolbar;

    private EditText newName, newMemo, newAccount, newAmount;
    private TextView newDate, newTime;
    private Database db;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private String recordid, userid, strDate, curTime, type, cate, name, memo, account, date, time, amount;

    private RadioGroup radgroup;
    SharedPreferences sp;
    private Intent intent;

    SimpleDateFormat sdf, format;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyincome);

        //set Status bar
        setStatus();
        initToolbar();
        setTitle("Record");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recordid = extras.getString("recordId");
            //The key argument here must match that used in the other activity
        } else {
            intent = new Intent(modifyincome.this, recordinfo.class);
            startActivity(intent);
            Toast.makeText(this, "Can't find this record", Toast.LENGTH_SHORT).show();
            finish();
        }


        db = new Database(this);

        userid = getuserid();
        setData(recordid, userid);


        // get date
        format = new SimpleDateFormat("yyyy-MM-dd");

        // perform click event on edit text for select date
        newDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(modifyincome.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                c.set(year, monthOfYear, dayOfMonth);
                                strDate = format.format(c.getTime());
                                newDate.setText(strDate);
//                                newDate.setText( year+ "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                //using steMaxDate to contraint date only current date or pass date
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }

        });

        // get time
        sdf = new SimpleDateFormat("HH:mm");

        newTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int Hour = c.get(Calendar.HOUR_OF_DAY);
                int Minute = c.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(modifyincome.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                curTime = String.format("%02d:%02d", hourOfDay, minute);
                                newTime.setText(curTime);
                            }
                        }, Hour, Minute, true);
                timePickerDialog.show();
            }
        });

        amountKeyboard();

    }

    private void setData(String rid, String uid) {
        db.open();
        Cursor c = db.getRecord(rid, uid);
        if (c.getCount() > 0) {
            c.moveToFirst();
            newDate = (TextView) findViewById(R.id.newDate);
            newTime = (TextView) findViewById(R.id.newTime);
            newName = (EditText) findViewById(R.id.newPname);
            newMemo = (EditText) findViewById(R.id.newMemo);
            newAccount = (EditText) findViewById(R.id.newAcount);
            newAmount = (EditText) findViewById(R.id.newAmount);
            radgroup = (RadioGroup) findViewById(R.id.radioGroup);

            cate = c.getString(c.getColumnIndex("cate"));
            name = c.getString(c.getColumnIndex("name"));
            memo = c.getString(c.getColumnIndex("memo"));
            account = c.getString(c.getColumnIndex("account"));
            amount = c.getString(c.getColumnIndex("amount"));
            date = c.getString(c.getColumnIndex("date"));
            time = c.getString(c.getColumnIndex("time"));

            newName.setText(name);
            newMemo.setText(memo);
            newAccount.setText(account);
            newAmount.setText(amount);
            newDate.setText(date);
            newTime.setText(time);

            String cateChecked = cate;
            switch (cateChecked) {
                case "salary":
                    radgroup.check(R.id.btnSalary);
                    break;
                case "sale":
                    radgroup.check(R.id.btnSale);
                    break;
                case "invest":
                    radgroup.check(R.id.btnInvest);
                    break;
                case "others":
                    radgroup.check(R.id.btnOthers);
                    break;
            }
//            c.moveToNext();
        } else {
            intent = new Intent(modifyincome.this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Can't find this record in DB", Toast.LENGTH_SHORT).show();
            finish();
        }
        db.close();
    }

    private void amountKeyboard() {
        newAmount.setEnabled(false);
        newAmount.addTextChangedListener(new Record.TradeTextWatcher(newAmount, null));
        TextView[] mBtnkey_digits = new TextView[10];

        for (int i = 0; i < 10; i++) {
            String strid = String.format("btn_price_%d", i);
            mBtnkey_digits[i] = (TextView) findViewById(this
                    .getResources().getIdentifier(strid, "id",
                            this.getPackageName()));
            mBtnkey_digits[i].setOnClickListener(mClickListener);
        }

        TextView mBtnKey_sk = (TextView) findViewById(R.id.btn_ok);
        TextView mBtnKey_point = (TextView) findViewById(R.id.btn_price_point);
        LinearLayout mBtnKey_del = (LinearLayout) findViewById(R.id.btn_price_del);

        mBtnKey_point.setOnClickListener(mClickListener);
        mBtnKey_del.setOnClickListener(mClickListener);
        mBtnKey_sk.setOnClickListener(mClickListener);

    }

    // status bar
    private void setStatus() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlue));
    }

    private String getuserid() {
        sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        return sp.getString("userid", "");
    }

    // Toolbar setting
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public void setTitle(CharSequence title) {
        TextView tvTitle = findViewById(R.id.title);

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent(modifyincome.this, recordinfo.class);
                intent.putExtra("recordId", recordid);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class TradeTextWatcher implements TextWatcher {

        private EditText mEditText;
//      private TextView mTextView;

        public TradeTextWatcher(EditText edit, TextView text) {
            mEditText = edit;
//          mTextView = text;
        }

        @Override
        public void afterTextChanged(Editable arg0) {

            int len = mEditText.getText().length();

            mEditText.setSelection(len);

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

        }

    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.btn_price_1
                    || id == R.id.btn_price_2
                    || id == R.id.btn_price_3
                    || id == R.id.btn_price_4
                    || id == R.id.btn_price_5
                    || id == R.id.btn_price_6
                    || id == R.id.btn_price_7
                    || id == R.id.btn_price_8
                    || id == R.id.btn_price_9
                    || id == R.id.btn_price_0) {
                String input = ((TextView) view).getText().toString();
                if (input == null) {
                    newAmount.setText(input);
                } else if (input != null) {
                    String strTmp = newAmount.getText().toString();

                    if (strTmp.contains(".")) {
                        if (strTmp.length() - 1 - strTmp.toString().indexOf(".") == 2) {
                            newAmount.setText(strTmp);
                        } else {
                            strTmp += input;
                            newAmount.setText(strTmp);
                        }
                    } else {
                        strTmp += input;
                        newAmount.setText(strTmp);
                    }
                }
                newAmount.setTextSize(30);
                newAmount.setTextColor(Color.WHITE);
            } else if (id == R.id.btn_price_point)//point
            {
                String inputa = ((TextView) view).getText().toString();
                if (inputa == null) {
                    newAmount.setText(inputa);
                } else if (inputa != null) {
                    String strTmp = newAmount.getText().toString();
                    if (strTmp.contains(".")) {
                        newAmount.setText(strTmp);
                    } else {
                        strTmp += inputa;
                        if (strTmp.startsWith(".") && strTmp.trim().length() == 1) {
                            strTmp = "0" + strTmp;
                            newAmount.setText(strTmp);
                        } else {
                            newAmount.setText(strTmp);
                        }
                    }
                }
                newAmount.setTextSize(30);
                newAmount.setTextColor(Color.WHITE);
            } else if (id == R.id.btn_ok) {//save

                String date = newDate.getText().toString();
                String time = newTime.getText().toString();
                String name = newName.getText().toString();
                String memo = newMemo.getText().toString();
                String account = newAccount.getText().toString();
                String amount = newAmount.getText().toString();

                String Rcate = null;
                for (int i = 0; i < radgroup.getChildCount(); i++) {
                    RadioButton rd = (RadioButton) radgroup.getChildAt(i);
                    if (rd.isChecked()) {
                        Rcate = rd.getText().toString();
                    }
                }
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(modifyincome.this, "Please input record name.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(amount)) {
                    Toast.makeText(modifyincome.this, "Please input amount of money.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    db.open();
                    double Ramount = Double.parseDouble(amount);
                    amount = String.format("%.2f", Ramount);
                    long qid = db.updateRecord(recordid, Rcate, name, amount, date, time, memo, account);
                    if (qid != -1) {
                        Toast.makeText(getApplicationContext(), "Successfully update Record!", Toast.LENGTH_LONG).show();
                        intent = new Intent(modifyincome.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry! record isn't update.", Toast.LENGTH_LONG).show();
                    }
                    db.close();
                }

            } else if (id == R.id.btn_price_del) {// clear one digital
                if (newAmount.getText().length() > 0) {
                    String strTmp = newAmount.getText().toString();
                    strTmp = strTmp.substring(0, strTmp.length() - 1);
                    newAmount.setText(strTmp);
                } else {
                    newAmount.setText("");
                }
                newAmount.setTextSize(30);
                newAmount.setTextColor(Color.WHITE);
            }
        }
    };
}
