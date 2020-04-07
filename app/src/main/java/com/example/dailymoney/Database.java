package com.example.dailymoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;

/**
 * Author: Minhui Chen on 2019/12/1 16:18
 * Summary: database code
 */

public class Database {
    // database columns
    //customer tabe
    private static final String KEY_USERID = "userid";
    private static final String KEY_USERNAME = "userName";
    private static final String KEY_USEREMAIL = "userEmail";
    private static final String KEY_USERPASSWORD = "userPassword";
    private static final String USER_TABLE = "User";

    private static final String CREATE_TABLE_USER = "create table User ( userid integer primary key autoincrement, " +
            "userName text unique not null, " +
            "userPassword text not null," +
            "userEmail text);";

    private static final String ACCOUNTID = "accountId";
    private static final String ACCOUNTNAME = "AName";
    private static final String AAMOUNT = "Aamount";
    private static final String AREMARK = "Aremark";
    private static final String ACCOUNT_TABLE = "Account";

    private static final String CREATE_TABLE_ACCOUNT = "create table Account (accountId integer primary key autoincrement, " +
            "Aname text," +
            "Aamount number, " +
            "Aremark text, " +
            "userid integer, " +
            "foreign key(userid) references User(userid));";

    private static final String RECORDID = "recordId";
    private static final String TYPE = "type";
    private static final String CATE = "cate";
    private static final String NAME = "name";
    private static final String AMOUNT = "amount";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String MEMO = "memo";
    private static final String RACCOUNT = "account";
    private static final String RECORD_TABLE = "Record";

    private static final String CREATE_TABLE_RECORD = "create table Record (recordId integer primary key autoincrement, " +
            "type text," +
            "cate text," +
            "name text," +
            "amount number, " +
            "date text, " +
            "time text, " +
            "memo text, " +
            "account text, " +
            "userid integer, " +
            "foreign key(account) references Account(Aname)," +
            "foreign key(userid) references User(userid));";
    ;

    private static final String DATABASE_NAME = "DailyMoneyDB.db";
    private static final int DATABASE_VERSION = 1;


    private Context mContext;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;


    // Constructor
    public Database(Context ctx) {
        //
        this.mContext = ctx;
        DBHelper = new DatabaseHelper(mContext);
    }

    public Database open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    // ///////////////////////nested dB helper class ///////////////////////////////////////
    private static class DatabaseHelper extends SQLiteOpenHelper {

        //
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        //
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TABLE_USER);
            db.execSQL(CREATE_TABLE_ACCOUNT);
            db.execSQL(CREATE_TABLE_RECORD);
        }

        @Override
        //
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // dB structure change..
        }
    }

    public void close() {
        DBHelper.close();
    }
    //////////////////////////// end nested dB helper class //////////////////////////////////////

    public long insertUser(String userName, String userPassword, String userEmail) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, userName);
        initialValues.put(KEY_USERPASSWORD, userPassword);
        initialValues.put(KEY_USEREMAIL, userEmail);
        return db.insert(USER_TABLE, null, initialValues);
    }

    public boolean deleteUser(String email) {
        //
        return db.delete(USER_TABLE, KEY_USEREMAIL + "=" + email, null) > 0;
    }

    public Cursor getAllUser() {
        return db.query(USER_TABLE, new String[]
                        {
                                KEY_USERID,
                                KEY_USEREMAIL,
                                KEY_USERNAME,
                                KEY_USERPASSWORD
                        },
                null, null, null, null, null);
    }

    public Cursor getUsPw(String userName) throws SQLException {
        String[] selectionArgs = {userName};
        return db.query(true, USER_TABLE, new String[]
                        {
                                KEY_USERID,
                                KEY_USERNAME,
                                KEY_USEREMAIL,
                                KEY_USERPASSWORD
                        },
                KEY_USERNAME + "=?", selectionArgs, null, null, null, null);
    }

    public Cursor getUser(String userName, String password) throws SQLException {
        String[] selectionArgs = {userName, password};
        return db.query(true, USER_TABLE, new String[]
                        {
                                KEY_USERID,
                                KEY_USERNAME,
                                KEY_USEREMAIL,
                                KEY_USERPASSWORD
                        },
                KEY_USERNAME + "=? and " + KEY_USERPASSWORD + "=?", selectionArgs, null, null, null, null);
    }

    public Cursor checkUser(String userName) throws SQLException {
        String[] selectionArgs = {userName};
        return db.query(true, USER_TABLE, new String[]
                        {
                                KEY_USERID,
                                KEY_USERNAME,
                                KEY_USERPASSWORD,
                                KEY_USEREMAIL
                        },
                KEY_USERNAME + "=?", selectionArgs, null, null, null, null);
    }

    public Cursor getAllAccount() {
        return db.query(ACCOUNT_TABLE, new String[]
                        {
                                ACCOUNTID,
                                ACCOUNTNAME,
                                AAMOUNT,
                                AREMARK,
                                KEY_USERID
                        },
                null, null, null, null, null);
    }

    public Cursor getAllRecord() {
        return db.query(RECORD_TABLE, new String[]
                        {
                                RECORDID + " as _id",
                                TYPE,
                                CATE,
                                NAME,
                                AMOUNT,
                                DATE,
                                TIME,
                                MEMO,
                                RACCOUNT,
                                KEY_USERID
                        },
                null, null, null, null, "date(" + DATE + ") desc"
        );
    }

    public Cursor seearchRecord(String str, String userid) {
        String[] selectionArgs = {"%" + str + "%", "%" + str + "%", "%" + str + "%", "%" + str + "%", "%" + str + "%", userid};
        return db.query(RECORD_TABLE, new String[]
                        {
                                RECORDID + " as _id",
                                TYPE,
                                CATE,
                                NAME,
                                AMOUNT,
                                DATE,
                                TIME,
                                MEMO,
                                RACCOUNT,
                                KEY_USERID
                        },
                CATE + " like ? or " + NAME + " like ? or " + AMOUNT + " like ? or " + MEMO + " like ? or " + RACCOUNT + " like ? and " + KEY_USERID + "=?", selectionArgs, null, null, "date(" + DATE + ") desc"
        );
    }

    public Cursor barChartPay(String str, String Type, String userid) {
        String[] selectionArgs = {str + "%", Type, userid};
        return db.query(RECORD_TABLE, new String[]
                        {
                                RECORDID + " as _id",
                                "sum(" + AMOUNT + ") as dayTotal",
                                TYPE,
                                DATE,
                                KEY_USERID

                        },
                DATE + " like ? and " + TYPE + "=? and " + KEY_USERID + "=?", selectionArgs, DATE, null, "date(" + DATE + ") asc"
        );
    }

    public Cursor pieChartPay(String str, String Type, String userid) {
        String[] selectionArgs = {str + "%", Type, userid};
        return db.query(RECORD_TABLE, new String[]
                        {
                                RECORDID + " as _id",
                                TYPE,
                                CATE,
                                "sum(" + AMOUNT + ") as cateTotal",
                                KEY_USERID
                        },
                DATE + " like ? and " + TYPE + "=? and " + KEY_USERID + "=?", selectionArgs, CATE, null, null

        );
    }

    public Cursor sumMonth(String str, String Type, String userid) {
        String[] selectionArgs = {str + "%", Type, userid};
        return db.query(RECORD_TABLE, new String[]
                        {
                                RECORDID + " as _id",
                                TYPE,
                                "sum(" + AMOUNT + ") as Total",
                                KEY_USERID
                        },
                DATE + " like ? and " + TYPE + "=? and " + KEY_USERID + "=?", selectionArgs, null, null, null

        );
    }

    public Cursor monthRecord(String str, String userid) {
        String[] selectionArgs = {str + "%", userid};
        return db.query(RECORD_TABLE, new String[]
                        {
                                RECORDID + " as _id",
                                TYPE,
                                CATE,
                                NAME,
                                AMOUNT,
                                DATE,
                                TIME,
                                MEMO,
                                RACCOUNT
                        },
                DATE + " like ? and " + KEY_USERID + "=?", selectionArgs, null, null, "date(" + DATE + ") desc," + TIME + " desc"
        );
    }

    public Cursor getRecord(String rid, String userid) {
        String[] selectionArgs = {rid, userid};
        return db.query(RECORD_TABLE, new String[]
                        {
                                RECORDID,
                                TYPE,
                                CATE,
                                NAME,
                                AMOUNT,
                                DATE,
                                TIME,
                                MEMO,
                                RACCOUNT
                        },
                RECORDID + "=? and " + KEY_USERID + "=?", selectionArgs, null, null, null
        );
    }

    public boolean deleteRocord(String rid) {
        //
        return db.delete(RECORD_TABLE, RECORDID + "=" + rid, null) > 0;
    }

    public long insertRecord(String type, String cate, String name, Double amount, String date, String time, String memo, String accountName, String userid) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(TYPE, type);
        initialValues.put(CATE, cate);
        initialValues.put(NAME, name);
        initialValues.put(AMOUNT, amount);
        initialValues.put(DATE, date);
        initialValues.put(TIME, time);
        initialValues.put(MEMO, memo);
        initialValues.put(RACCOUNT, accountName);
        initialValues.put(KEY_USERID, userid);
        return db.insert(RECORD_TABLE, null, initialValues);
    }

    public void backup(String outFileName) {

        //database path
        final String inFileName = mContext.getDatabasePath(DATABASE_NAME).toString();
        Log.d("inFileName", inFileName);

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(mContext, "Backup Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(mContext, "Unable to backup database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void importDB(String inFileName) {

        final String outFileName = mContext.getDatabasePath(DATABASE_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(mContext, "Import Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(mContext, "Unable to import database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}