package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
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
import android.widget.Toast;

import java.util.Calendar;

public class Register extends AppCompatActivity {
    private EditText email, password, firstname, surname, DOB;
    DatePickerDialog datePickerDialog;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new Database(this);

        email = (EditText) findViewById(R.id.newEmail);
        password = (EditText) findViewById(R.id.newPassword);
        firstname = (EditText) findViewById(R.id.newFirstname);
//        surname = (EditText) findViewById(R.id.newSurname);
//        DOB = (EditText) findViewById(R.id.newDOB);

        Button BtnBack =  findViewById(R.id.btnBack);
        BtnBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        // perform click event on edit text
        DOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(Register.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                DOB.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        //Click to finish register
        Button FinButton = (Button)findViewById(R.id.register);
        FinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String newEmail = email.getText().toString();
                String newPassowrd = password.getText().toString();
                String newFirstname = firstname.getText().toString();
                String newSurname = surname.getText().toString();
                String newDob = DOB.getText().toString();

                if(newEmail.isEmpty() || newPassowrd.isEmpty() || newFirstname.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please fill all details with *", Toast.LENGTH_LONG).show();
                }
                else{
                        db.open();
                        Cursor c = db.checkUser(newEmail);
                        if(c.getCount() != 0){
                            Toast.makeText(getApplicationContext(),"This account is exists", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            long id = db.insertUser(newEmail,newPassowrd, newFirstname, newSurname, newDob);
                            if(id != -1)
                            {
                                Toast.makeText(getApplicationContext(),"Successfully register!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Register.this, Login.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"Sorry! register is failed.", Toast.LENGTH_LONG).show();
                            }
                        }
                        db.close();
                    }
            }
        });
    }
}
