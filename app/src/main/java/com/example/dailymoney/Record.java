package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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

import java.util.Calendar;

public class Record extends AppCompatActivity {
    private EditText newDate, newTime,newName, newRemark, newAccount, newAmount;
    private Database db;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    String speech;

    RadioGroup radgroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        db = new Database(this);

        newDate = (EditText) findViewById(R.id.newDate);
        newTime = (EditText) findViewById(R.id.newTime);
//        newName = (EditText) findViewById(R.id.newPname);
        newRemark = (EditText) findViewById(R.id.newRemark);
        newAccount = (EditText) findViewById(R.id.newAcount);
        newAmount = (EditText) findViewById(R.id.newAmount);
        radgroup = (RadioGroup) findViewById(R.id.radioGroup);




        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            speech = extras.getString("EXTRA_SESSION_ID");
            //The key argument here must match that used in the other activity
        }

        Toast.makeText(Record.this, speech, Toast.LENGTH_SHORT).show();

        ImageButton BtnBack = (ImageButton) findViewById(R.id.btnBack);
        BtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Record.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

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
                                newDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                //using steMaxDate to contraint date only current date or pass date
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

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
                                newTime.setText(hourOfDay+":"+minute);
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
            } else if (id == R.id.btn_ok) {//收款

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

            } else if (id == R.id.btn_price_del) {//清除
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
}
