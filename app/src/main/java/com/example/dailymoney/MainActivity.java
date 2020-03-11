package com.example.dailymoney;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends ListActivity {

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
        speakButton = findViewById(R.id.voice_btn);



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

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognitionActivity();
            }
        });

        voiceinputbuttons();
    }

    public void voiceinputbuttons() {
        speakButton = (Button) findViewById(R.id.voice_btn);
    }

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
}