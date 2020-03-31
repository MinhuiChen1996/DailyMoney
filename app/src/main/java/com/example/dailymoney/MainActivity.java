package com.example.dailymoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ListView mListView;

    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private FloatingActionButton add;
    private FloatingActionButton voice;

    private TextView textView,monthYear,username;

    private Intent intent;

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    SimpleCursorAdapter myAdapter;
    private Database db;
    String[] columns = new String[] {"Pname","Pdate","Premark","Pamount"};
    int[] recordList = new int []{ R.id.pname, R.id.pdate, R.id.premark, R.id.pamount};

    public ArrayAdapter mList;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM");
    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat mY = new SimpleDateFormat("yyyy-MM");

    private RelativeLayout date_picker_button;

    private String strDe,monthYearStr,strDate,speech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView= (ListView) findViewById(R.id.list);
        //set Status bar
        setStatus();

        textView = (TextView) findViewById(R.id.tv_main);
        //set Toolbar
        initToolbar();

        // click button event
        initAddButton();
        initVoiceButton();

        //set Drawer Layout
        initDrawerLayout();

        // setting for navagtion view
        initgNavagationView();

        setTitle("Daily Money");


        username = (TextView) findViewById(R.id.nav_username);

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });


        monthYear = (TextView) findViewById(R.id.date_picker_text_view);
        date_picker_button = (RelativeLayout)findViewById(R.id.date_picker_button);

        // current month & year
        Calendar c = Calendar.getInstance();
        int cYear = c.get(Calendar.YEAR);
        int cMonth = c.get(Calendar.MONTH);
        c.set(cYear,cMonth,01);

        strDate = sdf.format(c.getTime());
        monthYear.setText(strDate);
        strDe = mY.format(c.getTime());
        initListRecord(strDe);

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
                        initListRecord(strDe);
                        monthYear.setText(formatMonthYear(monthYearStr));

                    }
                });
                pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
            }
        });



    }
    private void initListRecord(String str){
        db = new Database(this);
        db.open();
        Cursor c = db.monthPay(str);
        myAdapter = new SimpleCursorAdapter(this, R.layout.row_record, c, columns,recordList);
        mListView.setAdapter(myAdapter);
        db.close();
        // bind image with records
  /*      myAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.pname) {
                    int resID = getApplicationContext().getResources().getIdentifier(cursor.getString(5), "drawable", getApplicationContext().getPackageName());
                    IV.setImageDrawable(getApplicationContext().getResources().getDrawable(resID));
                    return true;
                }
                return false;
            }
        });*/
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

    // voice function
    public void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // dialog menu to display user voice and user can select correct sentence

            //array list to store the voice detection result
            ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mList = new ArrayAdapter(this, android.R.layout.simple_list_item_1, matches);

            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Please select what you said").setAdapter(mList,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    speech = mList.getItem(which).toString();
                    Intent intent = new Intent(MainActivity.this, Record.class);
                    intent.putExtra("EXTRA_SESSION_ID", speech);
                    startActivity(intent);
                    finish();
                }
            }).create();
            dialog.show();

        }
    }

    // menu setting
    private void initgNavagationView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    // close drawer
    private void initDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
    // Toolbar setting
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // status bar
    private void setStatus() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlue));
    }

    //左侧菜单item点击时回调
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.details:
                break;
            case R.id.bar_chart:
                Intent intent = new Intent(MainActivity.this, barchart.class);
                startActivity(intent);
                finish();
                break;
            case R.id.pie_chart:
                intent = new Intent(MainActivity.this, piechart.class);
                startActivity(intent);
                finish();
                break;
            case R.id.navigation_export:
                textView.setText(item.getTitle());
                break;
            case R.id.navigation_setting:
                textView.setText(item.getTitle());
                break;
            case R.id.navigation_backup:
                textView.setText(item.getTitle());
                break;
            case R.id.navigation_policy:
                Uri uri = Uri.parse("https://daily-money-0.flycricket.io/privacy.html"); // missing 'http://' will cause crashed
                intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.navigation_about:
                textView.setText(item.getTitle());
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    //返回键的处理
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // clikc button
    private void initAddButton() {
        add = (FloatingActionButton) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Record.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void initVoiceButton() {
        voice = (FloatingActionButton) findViewById(R.id.voice);
        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognitionActivity();
            }
        });
    }


    // add menu in toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    // menu click event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent intent = new Intent(MainActivity.this, search.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}