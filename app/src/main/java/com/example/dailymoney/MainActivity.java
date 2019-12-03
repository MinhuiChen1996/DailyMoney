package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends ListActivity {
    SimpleCursorAdapter myAdapter;
    private Database db;
    String[] columns = new String[] {"Pname","Pdate","Premark","Pamount"};
    int[] recordList = new int []{ R.id.pname, R.id.pdate, R.id.premark, R.id.pamoount};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new Database(this);
        db.open();
        Cursor c = db.getAllPay();
        myAdapter = new SimpleCursorAdapter(this, R.layout.row_record, c, columns,recordList);
//        myAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
//            @Override
//            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
//                if (view.getId() == R.id.pname) {
//                    int resID = getApplicationContext().getResources().getIdentifier(cursor.getString(5), "drawable", getApplicationContext().getPackageName());
//                    IV.setImageDrawable(getApplicationContext().getResources().getDrawable(resID));
//                    return true;
//                }
//                return false;
//            }
//        });
        setListAdapter(myAdapter);

        Button BtnLogin = (Button)findViewById(R.id.Login);
        Button BtnAdd =  findViewById(R.id.addNewRecord);
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        BtnAdd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Record.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
