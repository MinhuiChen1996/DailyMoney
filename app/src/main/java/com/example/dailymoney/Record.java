package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Locale;

import static java.lang.String.format;

public class Record extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText newName, newMemo, newAccount, newAmount;
    private TextView newDate, newTime;
    private Database db;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private String speech, userid, strDate, sPrice, pName, curTime, type;

    private RadioGroup radgroup,radgroup2;
    SharedPreferences sp;

    private Spinner sp_option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //set Status bar
        setStatus();
        initToolbar();
        setTitle("Record");

        sp_option = (Spinner) findViewById(R.id.sp_option);
        List<String> optionsList = new LinkedList<>(Arrays.asList("Expense", "Income"));
        ArrayAdapter arr_adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, optionsList);
        //set theme
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp_option.setAdapter(arr_adapter);
        sp_option.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    Intent intent = new Intent(Record.this, income.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        db = new Database(this);

        newDate = (TextView) findViewById(R.id.newDate);
        newTime = (TextView) findViewById(R.id.newTime);
        newName = (EditText) findViewById(R.id.newPname);
        newMemo = (EditText) findViewById(R.id.newMemo);
        newAccount = (EditText) findViewById(R.id.newAcount);
        newAmount = (EditText) findViewById(R.id.newAmount);
        radgroup = (RadioGroup) findViewById(R.id.radioGroup);
        radgroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
//        radgroup.clearCheck();
        radgroup2.clearCheck();
        radgroup.setOnCheckedChangeListener(listener1);
        radgroup2.setOnCheckedChangeListener(listener2);




        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            speech = extras.getString("EXTRA_SESSION_ID");
            //The key argument here must match that used in the other activity
        }

        // split the keyword from speech
        sPrice = extractPrice(speech);

        newAmount.setText(sPrice);
;



        pName = extracProduct(speech);
        newName.setText(pName);


        // current date
        Calendar c = Calendar.getInstance();
        int cYear = c.get(Calendar.YEAR);
        int cMonth = c.get(Calendar.MONTH);
        int cDay = c.get(Calendar.DAY_OF_MONTH);
        c.set(cYear, cMonth, cDay);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        strDate = format.format(c.getTime());

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

        // current time
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        curTime = sdf.format(date);
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
                                curTime = format("%02d:%02d", hourOfDay, minute);
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
            String strid = format("btn_price_%d", i);
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

    private RadioGroup.OnCheckedChangeListener listener1 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                radgroup.setOnCheckedChangeListener(null); // remove the listener before clearing so we don't throw that stackoverflow exception(like Vladimir Volodin pointed out)
                radgroup2.clearCheck(); // clear the second RadioGroup!
                radgroup2.setOnCheckedChangeListener(listener2); //reset the listener
                Log.e("XXX2", "do the work");
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener listener2 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                radgroup.setOnCheckedChangeListener(null);
                radgroup.clearCheck();
                radgroup.setOnCheckedChangeListener(listener1);
                Log.e("XXX2", "do the work");
            }
        }
    };

    @Override
    public void setTitle(CharSequence title) {
        TextView tvTitle = findViewById(R.id.title);

        if (tvTitle != null) {
            tvTitle.setText(title);
        }
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
            } else if (id == R.id.btn_ok) {
                //save

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
                    else {
                        for (int y = 0; y < radgroup2.getChildCount(); y++) {
                            rd = (RadioButton) radgroup2.getChildAt(y);
                            if (rd.isChecked()) {
                                Rcate = rd.getText().toString();
                            }
                        }
                    }
                }

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(Record.this, "Please input record name.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(amount)) {
                    Toast.makeText(Record.this, "Please input amount of money.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    userid = getuserid();
                    db.open();
                    type = "Expense";

                    double Ramount = Double.parseDouble(amount);
                    Ramount = -Ramount;
                    amount = String.format("%.2f", Ramount);
                    Log.d("amount", amount);
                    long qid = db.insertRecord(type, Rcate, name, amount, date, time, memo, account, userid);
                    if (qid != -1) {
                        if(continuousexpense()){
                            Toast.makeText(getApplicationContext(), "Successfully Save Record!", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(getIntent());
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Successfully Save Record!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(Record.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Sorry! record isn't add.", Toast.LENGTH_LONG).show();
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


    public static String extractPrice(final String str) {
        if (str == null || str.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c) || c == '.') {
                if (sb.toString().length() - 1 - sb.toString().toString().indexOf(".") == 2) {
                    break;
                }
                else{
                    sb.append(c);
                    found = true;
                }
            }
        }
        if(sb.toString().equals("")){
            return sb.toString();
        }
        else {
            double Vamount = Double.parseDouble(sb.toString());
            String amount = String.format("%.2f", Vamount);
            return amount;
        }
    }

    public static String extracProduct(final String str) {
        if (str == null || str.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c) || c == '$' || c == '€'|| c == '£') {
                break;
            } else {
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

    private String getuserid() {
        sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        return sp.getString("userid", "");
    }

    private boolean continuousexpense(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        Boolean continuousexpense=prefs.getBoolean("continuousexpense",false);
        return continuousexpense;
    }


}
