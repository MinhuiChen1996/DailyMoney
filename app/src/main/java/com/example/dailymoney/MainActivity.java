package com.example.dailymoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLConnection;
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

    private TextView monthYear, tv_logout, nav_username, tv_income, tv_expense, tv_balance;

    private Intent intent;

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;

    SimpleCursorAdapter myAdapter;
    private Database db;
    String[] columns = new String[]{"_id", "name", "date", "memo", "amount"};
    int[] recordList = new int[]{R.id._id, R.id.name, R.id.date, R.id.memo, R.id.amount};

    public ArrayAdapter mList;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM");
    SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat mY = new SimpleDateFormat("yyyy-MM");

    private RelativeLayout date_picker_button;

    private String strDe, monthYearStr, strDate, speech, username, userid;
    private View headerview;
    SharedPreferences sp;
    Calendar c;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final int REQUEST_CODE_PERMISSIONS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new Database(this);

        //set Status bar
        setStatus();
        //set Toolbar
        initToolbar();

        // click button event
        initAddButton();
        initVoiceButton();

        //set Drawer Layout
        initDrawerLayout();

        // setting for navagtion view
        initgNavagationView();

        mListView = (ListView) findViewById(R.id.list);



        tv_income = (TextView) findViewById(R.id.tv_income);
        tv_expense = (TextView) findViewById(R.id.tv_expense);
        tv_balance = (TextView) findViewById(R.id.tv_balance);


        setTitle("Daily Money");
        headerview = navigationView.getHeaderView(0);
        tv_logout = (TextView) headerview.findViewById(R.id.nav_logout);

        monthYear = (TextView) findViewById(R.id.date_picker_text_view);
        date_picker_button = (RelativeLayout) findViewById(R.id.date_picker_button);

        checkLogin();

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


        strDe = currentMonthYear();
        userid = getuserid();
        initListRecord(strDe, userid);
        setBalance(strDe, userid);

        date_picker_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strDe = initMonthYearPicker();
                initListRecord(strDe, userid);
                setBalance(strDe, userid);
            }
        });


    }


    private void logout() {
        SharedPreferences sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isLogin", false);
        editor.putString("loginUserName", "");
        editor.putString("userid", "");
        editor.commit();
        nav_username.setText("");
        Toast.makeText(MainActivity.this, "Logout successful.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void setBalance(String date, String userid) {
        db.open();
        Cursor expenseC = db.sumMonth(date, "Expense", userid);
        double expense, income, balance;
        if (expenseC.getCount() > 0) {
            expenseC.moveToFirst();
            expense = expenseC.getDouble(expenseC.getColumnIndex("Total"));
            expenseC.moveToNext();
        } else {
            expense = 0;
        }
        Cursor incomeC = db.sumMonth(date, "Income", userid);
        if (incomeC.getCount() > 0) {
            incomeC.moveToFirst();
            income = incomeC.getDouble(expenseC.getColumnIndex("Total"));
            incomeC.moveToNext();
        } else {
            income = 0;
        }
        balance = income + expense;
        tv_income.setText(String.format("%.2f", income));
        tv_expense.setText(String.format("%.2f", expense));
        tv_balance.setText(String.format("%.2f", balance));
        db.close();
    }


    private String currentMonthYear() {
        // current month & year
        c = Calendar.getInstance();
        int cYear = c.get(Calendar.YEAR);
        int cMonth = c.get(Calendar.MONTH);
        c.set(cYear, cMonth, 01);

        strDate = sdf.format(c.getTime());
        monthYear.setText(strDate);
        strDe = mY.format(c.getTime());
        return strDe;
    }

    private String initMonthYearPicker() {
        MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
        pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
                monthYearStr = year + "-" + (month + 1) + "-" + i2;
                c.set(year, month, i2);
                strDe = mY.format(c.getTime());
                monthYear.setText(formatMonthYear(monthYearStr));
            }
        });
        pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
        return strDe;
    }

    private String exportMonthYear() {
        MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
        pickerDialog.setListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int i2) {
                c.set(year, month, i2);
                strDe = mY.format(c.getTime());
            }
        });
        pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
        return strDe;
    }

    private void initListRecord(String str, String uid) {

        db.open();
        Cursor c = db.monthRecord(str, uid);
        myAdapter = new SimpleCursorAdapter(this, R.layout.row_record, c, columns, recordList);
        mListView.setAdapter(myAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String recordId = ((TextView) view.findViewById(R.id._id)).getText().toString();
                Log.d("detail", recordId);
                Intent intent = new Intent(MainActivity.this, recordinfo.class);
                intent.putExtra("recordId", recordId);
                startActivity(intent);
                finish();
            }
        });
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
                "Try to say the name and price for record");
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

            AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Please select what you said").setAdapter(mList, new DialogInterface.OnClickListener() {
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
        navigationView.setItemIconTintList(null);
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

    //left menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.details:
                break;
            case R.id.bar_chart:
                intent = new Intent(MainActivity.this, barchart.class);
                startActivity(intent);
                finish();
                break;
            case R.id.pie_chart:
                intent = new Intent(MainActivity.this, piechart.class);
                startActivity(intent);
                finish();
                break;
            case R.id.navigation_export:
                strDe = exportMonthYear();
                createCSV(strDe);
                break;
            case R.id.navigation_setting:
                intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.navigation_backupAndRestore:
                intent = new Intent(MainActivity.this, backupAndrestrore.class);
                startActivity(intent);
                finish();
                break;
            case R.id.navigation_about:
                intent = new Intent(MainActivity.this, about.class);
                startActivity(intent);
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // add menu in toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_btn, menu);
        return true;
    }

    // menu click event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                intent = new Intent(MainActivity.this, search.class);
                startActivity(intent);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    //back pressed
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

    private void checkLogin() {

        sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        if (getloginstatus()) {
            username = sp.getString("loginUserName", "");
            nav_username = (TextView) headerview.findViewById(R.id.nav_username);
            nav_username.setText("Hi " + username);
        } else {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean getloginstatus() {
        return sp.getBoolean("isLogin", false);
    }

    private String getuserid() {
        sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        return sp.getString("userid", "");
    }


    //check permissions.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_CODE_PERMISSIONS
            );
        }
    }

    public void createCSV(String str) {
        verifyStoragePermissions(this);
        boolean success = true;
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + this.getResources().getString(R.string.app_name));

        if (!folder.exists())
            success = folder.mkdirs();
        if (success) {
            String filename = folder.toString() + File.separator;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("CSV file Name");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setPositiveButton("Save", (dialog, which) -> {
                String m_Text = input.getText().toString();
                String out = filename + m_Text + ".csv";
                writeToCSVfile(strDe, userid, out);
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            builder.show();
        } else
            Toast.makeText(this, "Unable to create directory. Retry", Toast.LENGTH_SHORT).show();
        /*        return filename;*/
    }

    private void writeToCSVfile(String str, String userid, String filename) {
        db.open();
        Cursor c = db.monthRecord(str, userid);
        FileWriter fw = null;
        try {
            int rowcount = 0;
            int colcount = 0;
            fw = new FileWriter(filename);
            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();

            if (rowcount > 0)
                c.moveToFirst();
            for (int i = 0; i < colcount; i++) {
                if (i != colcount - 1) {
                    bw.write(c.getColumnName(i) + ",");
                } else {
                    bw.write(c.getColumnName(i));
                }
            }
            bw.write("\r\n");
            for (int i = 0; i < rowcount; i++) {
                c.moveToPosition(i);
                for (int j = 0; j < colcount; j++) {
                    if (j != colcount - 1) {
                        bw.write(c.getString(j) + ",");
                    } else {
                        bw.write(c.getString(j));
                    }
                }
                bw.write("\r\n");
                bw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        db.close();
        File file = new File(filename);
        shareFile(file);
    }

    private void shareFile(File file) {
        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        intentShareFile.setType(URLConnection.guessContentTypeFromName(file.getName()));
        intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
        startActivity(Intent.createChooser(intentShareFile, "Share File"));
    }



}