package com.example.dailymoney;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class search extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Intent intent;

    private EditText searchText;

    SimpleCursorAdapter myAdapter;
    private Database db;
    String[] columns = new String[]{"name", "date", "memo", "amount"};
    int[] recordList = new int[]{R.id.name, R.id.date, R.id.memo, R.id.amount};
    private ListView mListView;
    public ArrayAdapter mList;

    private SearchView mSearchView;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //set Status bar
        setStatus();
        //set Toolbar
        initToolbar();
        // list view for search result
        mListView = (ListView) findViewById(R.id.list);
        db = new Database(this);

        searchText = (EditText) findViewById(R.id.searchText);
        ImageButton btnSearch = (ImageButton) findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchText.getText().toString();
                String userid = getuserid();
                if (query.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "no text", Toast.LENGTH_LONG).show();
                } else {
                    db.open();
                    Cursor c = db.seearchRecord(query, userid);

                    myAdapter = new SimpleCursorAdapter(search.this, R.layout.row_record, c, columns, recordList);
                    mListView.setAdapter(myAdapter);
                    db.close();
                }
            }
        });
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

                Intent intent = new Intent(search.this, MainActivity.class);
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

    //返回键的处理
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private String getuserid() {
        sp = getSharedPreferences("loginInfo", MODE_PRIVATE);
        return sp.getString("userid", "");
    }

}
