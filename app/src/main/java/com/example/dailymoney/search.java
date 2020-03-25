package com.example.dailymoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

public class search extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Intent intent;

    private EditText searchText;

    SimpleCursorAdapter myAdapter;
    private Database db;
    String[] columns = new String[] {"Pname","Pdate","Premark","Pamount"};
    int[] recordList = new int []{ R.id.pname, R.id.pdate, R.id.premark, R.id.pamount};
    private ListView mListView;
    public ArrayAdapter mList;

    private SearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //set Status bar
        setStatus();

        //set Toolbar
        initToolbar();

        //set Drawer Layout
        initDrawerLayout();

        // setting for navagtion view
        initgNavagationView();

        // list view for search result
        mListView = (ListView) findViewById(R.id.list);
        db = new Database(this);

        searchText = (EditText)findViewById(R.id.searchText);
        ImageButton btnSearch = (ImageButton)findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchText.getText().toString();

                if(query.isEmpty() ){
                    Toast.makeText(getApplicationContext(),"no text", Toast.LENGTH_LONG).show();
                }
                else{
                    db.open();
                    Cursor c = db.seearchPay(query);

                    myAdapter = new SimpleCursorAdapter(search.this, R.layout.row_record, c, columns,recordList);
                    mListView.setAdapter(myAdapter);
                    db.close();
                }
            }
        });
    }

    // menu setting
    private void initgNavagationView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view_search);
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
                intent = new Intent(search.this, MainActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.charts:

                break;
            case R.id.navigation_export:

                break;
            case R.id.navigation_setting:

                break;
            case R.id.navigation_backup:

                break;
            case R.id.navigation_policy:
                Uri uri = Uri.parse("https://daily-money-0.flycricket.io/privacy.html"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;
            case R.id.navigation_about:

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
