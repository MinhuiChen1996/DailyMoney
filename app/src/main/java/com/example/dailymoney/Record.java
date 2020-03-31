package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Record extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText newName, newRemark, newAccount, newAmount;
    private TextView newDate, newTime;
    private Database db;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    String speech;

    RadioGroup radgroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //set Status bar
        setStatus();
        initToolbar();

        db = new Database(this);

        newDate = (TextView) findViewById(R.id.newDate);
        newTime = (TextView) findViewById(R.id.newTime);
        newName = (EditText) findViewById(R.id.newPname);
        newRemark = (EditText) findViewById(R.id.newRemark);
        newAccount = (EditText) findViewById(R.id.newAcount);
        newAmount = (EditText) findViewById(R.id.newAmount);
        radgroup = (RadioGroup) findViewById(R.id.radioGroup);




        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            speech = extras.getString("EXTRA_SESSION_ID");
            //The key argument here must match that used in the other activity
        }

//        Toast.makeText(Record.this, speech, Toast.LENGTH_SHORT).show();

        // split the keyword from speech
        String sPrice = extractPrice(speech);
        newAmount.setText(sPrice);

        String pName =  extracProduct(speech);
        newName.setText(pName);

/*        //back to main screen
        ImageButton BtnBack = (ImageButton) findViewById(R.id.btnBack);
        BtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Record.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });*/


        // current date
        Calendar c = Calendar.getInstance();
        int cYear = c.get(Calendar.YEAR);
        int cMonth = c.get(Calendar.MONTH);
        int cDay = c.get(Calendar.DAY_OF_MONTH);
        c.set(cYear,cMonth,cDay);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = format.format(c.getTime());

        newDate.setText(strDate);
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
                datePickerDialog = new DatePickerDialog(Record.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                c.set(year,monthOfYear,dayOfMonth);
                                String strDate = format.format(c.getTime());
                                newDate.setText(strDate);
//                                newDate.setText( year+ "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                //using steMaxDate to contraint date only current date or pass date
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

       // current time
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String curTime=sdf.format(date);
        newTime.setText(curTime);
        newTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int Hour = c.get(Calendar.HOUR_OF_DAY);
                int Minute = c.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(Record.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String curTime = String.format("%02d:%02d", hourOfDay, minute);
                                newTime.setText(curTime);
                            }
                        }, Hour, Minute, true);
                timePickerDialog.show();
            }
        });


        newAmount.setEnabled(false);
        newAmount.addTextChangedListener(new TradeTextWatcher(newAmount, null));
        TextView[] mBtnkey_digits = new TextView[10];

        for (int i = 0; i < 10; i++) {
            String strid = String.format("btn_price_%d", i);
            mBtnkey_digits[i] = (TextView)findViewById(this
                    .getResources().getIdentifier(strid, "id",
                            this.getPackageName()));
            mBtnkey_digits[i].setOnClickListener(mClickListener);
        }

        TextView mBtnKey_sk = (TextView)findViewById(R.id.btn_ok);
        TextView mBtnKey_point = (TextView)findViewById(R.id.btn_price_point);
        LinearLayout mBtnKey_del = (LinearLayout)findViewById(R.id.btn_price_del);

        mBtnKey_point.setOnClickListener(mClickListener);
        mBtnKey_del.setOnClickListener(mClickListener);
        mBtnKey_sk.setOnClickListener(mClickListener);

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

                Intent intent = new Intent(Record.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
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
                if (input == null){
                    newAmount.setText(input);
                }else if (input != null) {
                    String strTmp = newAmount.getText().toString();
                    strTmp += input;
                    newAmount.setText(strTmp);
                }
                newAmount.setTextSize(30);
                newAmount.setTextColor(Color.BLACK);
            }else if (id == R.id.btn_price_point)//点
            {
                String inputa = ((TextView) view).getText().toString();
                if (inputa == null){
                    newAmount.setText(inputa);
                }else if (inputa != null) {
                    String strTmp = newAmount.getText().toString();
                    strTmp += inputa;
                    newAmount.setText(strTmp);
                }
                newAmount.setTextSize(30);
                newAmount.setTextColor(Color.BLACK);
            } else if (id == R.id.btn_ok) {//save

                String date = newDate.getText().toString();
                String time = newTime.getText().toString();
                String Pname = newName.getText().toString();
                String Premark = newRemark.getText().toString();
                String Paccount = newAccount.getText().toString();
                String strPamount = newAmount.getText().toString();

                double Pamount = Double.parseDouble(strPamount);
                String Pcate = null;
                for(int i = 0; i < radgroup.getChildCount(); i++){
                    RadioButton rd = (RadioButton) radgroup.getChildAt(i);
                    if(rd.isChecked())
                    {
                        Pcate = rd.getText().toString();
                    }
                }
                db.open();
//                String query = "SELECT userid FROM " + "user_table" + " WHERE " + "userEmail" + " = '" + em + "'";
//                Cursor mCursor = db.rawQuery(query, null);
//
                long qid = db.insertPay(Pcate, Pname, Pamount,date, time, Premark, Paccount);
                if(qid != -1)
                {
                    Toast.makeText(getApplicationContext(),"Successfully Save Record!", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(getIntent());
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Sorry! record isn't add.", Toast.LENGTH_LONG).show();
                }
                db.close();

            } else if (id == R.id.btn_price_del) {// clear one digital
                if (newAmount.getText().length() > 0) {
                    String strTmp = newAmount.getText().toString();
                    strTmp = strTmp.substring(0, strTmp.length() - 1);
                    newAmount.setText(strTmp);
                }else {
                    newAmount.setText("");
                }
                newAmount.setTextSize(30);
                newAmount.setTextColor(Color.BLACK);
            }
        }
    };
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


    public static String extractPrice(final String str){
        if(str == null || str.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        boolean found =  false;
        for(char c :  str.toCharArray()){
            if(Character.isDigit(c)|| c == '.'){
                sb.append(c);
                found=true;
            }
            else if(found || c == '$' || c == '€'){
                break;
            }
        }
        return sb.toString();
    }

    public static String extracProduct(final String str){
        if(str == null || str.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for(char c :  str.toCharArray()){
            if( Character.isDigit(c) || c == '$' || c == '€'){
                break;
            }
            else{
                sb.append(c);
            }
        }
        return sb.toString();
    }
    // status bar
    private void setStatus() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlue));
    }
}
