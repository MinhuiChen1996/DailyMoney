package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class barchart extends AppCompatActivity {

    private Toolbar toolbar;

    private Database db;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM");
    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat mY = new SimpleDateFormat("yyyy-MM");

    private TextView monthYear;

    private RelativeLayout date_picker_button;

    private String strDe, monthYearStr, strDate;

    BarChart barChart;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barchart);


        //set Status bar
        setStatus();
        initToolbar();

        setTitle("Bar Chart");


        db = new Database(this);
        monthYear = (TextView) findViewById(R.id.date_picker_text_view);
        date_picker_button = (RelativeLayout) findViewById(R.id.date_picker_button);
        barChart = (BarChart) findViewById(R.id.barchart);
        // current month & year
        Calendar c = Calendar.getInstance();
        int cYear = c.get(Calendar.YEAR);
        int cMonth = c.get(Calendar.MONTH);
        c.set(cYear, cMonth, 01);

        strDate = sdf.format(c.getTime());
        monthYear.setText(strDate);
        strDe = mY.format(c.getTime());
        String userid = getuserid();

        RadioGroup rad = (RadioGroup)findViewById(R.id.radioSelector);
        RadioButton expnese = (RadioButton)findViewById(R.id.rdb_expense);
        RadioButton income = (RadioButton)findViewById(R.id.rdb_income);

        rad.check(R.id.rdb_expense);
        initExpense(userid,c);

        expnese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barChart.notifyDataSetChanged();
                barChart.invalidate();
                initExpense(userid,c);
            }
        });
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barChart.notifyDataSetChanged();
                barChart.invalidate();
                initIncome(userid,c);
            }
        });

    }

    private void initIncome(String userid, Calendar c){
        barchartIncome(strDe, userid);
        date_picker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
                        monthYearStr = year + "-" + (month + 1) + "-" + i2;
                        c.set(year, month, i2);
                        strDe = mY.format(c.getTime());
                        barchartIncome(strDe, userid);
                        monthYear.setText(formatMonthYear(monthYearStr));

                    }
                });
                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });
    }

    private void initExpense(String userid, Calendar c){
        barchartExpense(strDe, userid);
        date_picker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
                pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
                        monthYearStr = year + "-" + (month + 1) + "-" + i2;
                        c.set(year, month, i2);
                        strDe = mY.format(c.getTime());
                        barchartExpense(strDe, userid);
                        monthYear.setText(formatMonthYear(monthYearStr));

                    }
                });
                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });
    }

    private void barchartIncome(String str, String uid) {

        db.open();

        Cursor c = db.barChartRecord(str,"Income",uid);
        ArrayList<String> date = new ArrayList<>();
        ArrayList<Double> amount = new ArrayList<>();


        c.moveToFirst();
        while (!c.isAfterLast()) {
            date.add(c.getString(c.getColumnIndex("date")).substring(c.getString(c.getColumnIndex("date")).length() - 2));
            amount.add(c.getDouble(c.getColumnIndex("dayTotal")));
            c.moveToNext();
        }
        c.close();
        db.close();

        ArrayList<BarEntry> yVals = new ArrayList<>();

        ArrayList<String> xVals = new ArrayList<>();

        for (int counter = 0; counter < amount.size(); counter++) {
            xVals.add(date.get(counter));
            yVals.add(new BarEntry(Float.valueOf(String.valueOf(amount.get(counter))), counter));
        }


        BarDataSet bardataset = new BarDataSet(yVals, "Day");

        BarData data = new BarData(xVals, bardataset);

        barChart.setData(data); // set the data and list of labels into chart

//        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);


        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);

        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisRight().setEnabled(false);
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setStartAtZero(false);

        for (IDataSet set : barChart.getData().getDataSets()) {
            set.setDrawValues(true);
            set.setValueTextSize(10);
            set.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    DecimalFormat mFormat = new DecimalFormat("###,###,##0.00");
                    return mFormat.format(value);
                }
            });
        }
        barChart.animateY(1500);

        barChart.invalidate();

    }

    private void barchartExpense(String str, String uid) {

        db.open();

        Cursor c = db.barChartRecord(str,"Expense",uid);
        ArrayList<String> date = new ArrayList<>();
        ArrayList<Double> amount = new ArrayList<>();


        c.moveToFirst();
        while (!c.isAfterLast()) {
            date.add(c.getString(c.getColumnIndex("date")).substring(c.getString(c.getColumnIndex("date")).length() - 2));
            Double reverseAmount = c.getDouble(c.getColumnIndex("dayTotal"));
            reverseAmount = -reverseAmount;
            amount.add(reverseAmount);
            c.moveToNext();
        }
        c.close();
        db.close();

        ArrayList<BarEntry> yVals = new ArrayList<>();
        ArrayList<String> xVals = new ArrayList<>();

        for (int counter = 0; counter < amount.size(); counter++) {
            xVals.add(date.get(counter));
            yVals.add(new BarEntry(Float.valueOf(String.valueOf(amount.get(counter))), counter));
        }

        BarDataSet bardataset = new BarDataSet(yVals, "Day");
        BarData data = new BarData(xVals, bardataset);
        barChart.setData(data); // set the data and list of labels into chart
//        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        barChart.getAxisLeft().setDrawAxisLine(false);
        barChart.getAxisRight().setEnabled(false);
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setStartAtZero(false);
        for (IDataSet set : barChart.getData().getDataSets()) {
            set.setDrawValues(true);
            set.setValueTextSize(10);
            set.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    DecimalFormat mFormat = new DecimalFormat("###,###,##0.00");
                    return mFormat.format(value);
                }
            });
        }
        barChart.animateY(1500);
        barChart.invalidate();
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

                Intent intent = new Intent(barchart.this, MainActivity.class);
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

    private String getuserid() {
        sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        return sp.getString("userid", "");
    }


}
