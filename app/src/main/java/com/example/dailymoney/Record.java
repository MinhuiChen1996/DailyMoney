package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Record extends AppCompatActivity {
    private EditText newDate, newTime,newName, newRemark, newAccount, newAmount;
    private Database db;

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    String speech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        db = new Database(this);

        newDate = (EditText) findViewById(R.id.newDate);
        newTime = (EditText) findViewById(R.id.newTime);
        newName = (EditText) findViewById(R.id.newPname);
        newRemark = (EditText) findViewById(R.id.newRemark);
        newAccount = (EditText) findViewById(R.id.newAcount);
        newAmount = (EditText) findViewById(R.id.newAmount);

        newTime.set


        final RadioGroup radgroup = (RadioGroup) findViewById(R.id.radioGroup);

        Button BtnBack = (Button)findViewById(R.id.btnBack);
        Button BtnSave = (Button)findViewById(R.id.btnSave);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            speech = extras.getString("EXTRA_SESSION_ID");
            //The key argument here must match that used in the other activity
        }

        Toast.makeText(Record.this, speech, Toast.LENGTH_SHORT).show();

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

        // insert new record of expenditure
        BtnSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

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
                long id = db.insertPay(Pcate, Pname, Pamount,date, time, Premark, Paccount);
                if(id != -1)
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

            }
        });
    }
}
