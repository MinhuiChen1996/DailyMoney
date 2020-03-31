package com.example.dailymoney;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class piechart extends AppCompatActivity {
    private Toolbar toolbar;

    private Database db;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM");
    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat mY = new SimpleDateFormat("yyyy-MM");

    private TextView monthYear;

    private RelativeLayout date_picker_button;

    private String strDe,monthYearStr,strDate;

    PieChart pieChart;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart);

        //set Status bar
        setStatus();
        initToolbar();

        setTitle("Pie Chart");

        db = new Database(this);
        monthYear = (TextView) findViewById(R.id.date_picker_text_view);
        date_picker_button = (RelativeLayout)findViewById(R.id.date_picker_button);
        pieChart = (PieChart) findViewById(R.id.piechart);
        // current month & year
        Calendar c = Calendar.getInstance();
        int cYear = c.get(Calendar.YEAR);
        int cMonth = c.get(Calendar.MONTH);
        c.set(cYear,cMonth,01);

        strDate = sdf.format(c.getTime());
        monthYear.setText(strDate);
        strDe = mY.format(c.getTime());
        initpieChart(strDe);

        date_picker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
                        monthYearStr = year + "-" + (month + 1) + "-" + i2;
                        c.set(year,month,i2);
                        strDe = mY.format(c.getTime());
                        initpieChart(strDe);
                        monthYear.setText(formatMonthYear(monthYearStr));

                    }
                });
                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });

    }

    private void initpieChart(String str){

        db.open();
        Cursor c = db.pieChartPay(str);
        ArrayList<String> cate = new ArrayList<>();
        ArrayList<Double> amount = new ArrayList<>();

        c.moveToFirst();
        while(!c.isAfterLast()) {
            cate.add(c.getString(c.getColumnIndex("Pcate")));
            amount.add(c.getDouble(c.getColumnIndex("cateTotal")));
            c.moveToNext();
        }

        ArrayList yVals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        for (int counter = 0; counter < amount.size(); counter++) {
            xVals.add(cate.get(counter));
            yVals.add(new BarEntry(Float.valueOf(String.valueOf(amount.get(counter))), counter));
        }

        PieDataSet dataSet = new PieDataSet (yVals, "");

        PieData  data = new PieData (xVals, dataSet);

        pieChart.setData(data);// set the data and list of labels into chart
        pieChart.setDescription("Monthly Category Report");  // set the description
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(5000, 5000);
    }

    String formatMonthYear(String str) {
        Date date = null;
        try {
            date = input.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return sdf.format(date);
    }
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

                Intent intent = new Intent(piechart.this, MainActivity.class);
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
}
