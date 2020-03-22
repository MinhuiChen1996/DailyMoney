package com.example.dailymoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ListView mListView;
    private Toolbar toolbar;
    private FloatingActionButton add;
    private FloatingActionButton voice;
    private DrawerLayout drawerLayout;
    private TextView textView;
    private NavigationView navigationView;


    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    SimpleCursorAdapter myAdapter;
    private Database db;
    String[] columns = new String[] {"Pname","Pdate","Premark","Pamount"};
    int[] recordList = new int []{ R.id.pname, R.id.pdate, R.id.premark, R.id.pamoount};

    public Button speakButton;
    public ArrayAdapter mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView= (ListView) findViewById(R.id.list);
        //设置状态栏透明
        setStatus();

        textView = (TextView) findViewById(R.id.tv_main);
        //设置Toolbar
        initToolbar();

        //按钮点击事件
        initAddButton();

        initVoiceButton();

        //设置菜单打开关闭的监听
        initDrawerLayout();

        //菜单的设置
        initgNavagationView();


        db = new Database(this);
        db.open();
        Cursor c = db.getAllPay();
        myAdapter = new SimpleCursorAdapter(this, R.layout.row_record, c, columns,recordList);
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
        mListView.setAdapter(myAdapter);


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
                    String speech = mList.getItem(which).toString();
                    Intent intent = new Intent(MainActivity.this, Record.class);
                    intent.putExtra("EXTRA_SESSION_ID", speech);
                    startActivity(intent);
                }
            }).create();
            dialog.show();

        }
    }

    //菜单的设置
    private void initgNavagationView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    //设置菜单打开关闭的监听
    private void initDrawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    //按钮点击事件
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
    //设置Toolbar
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    //设置状态栏透明
    private void setStatus() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }


    //左侧菜单item点击时回调
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.charts:
            case R.id.navigation_export:
            case R.id.navigation_setting:
            case R.id.navigation_backup:
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
}