package com.example.dailymoney;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
/**
 * Author: Minhui Chen on 2019/12/1 16:18
 * Summary: database code
 */

public class Database
{
    // database columns
    //customer tabe
    private static final String KEY_USERID 	    = "userid";
    private static final String KEY_USEREMAIL 	= "userEmail";
    private static final String KEY_USERPASSWORD    = "userPassword";
    private static final String KEY_FIRSTNAME    = "firstName";
    private static final String  KEY_SECONDNAME	    = "secondName";
    private static final String  KEY_USERDOB	    = "userDOB";
    private static final String USER_TABLE 	= "User";

    private static final String CREATE_TABLE_USER = "create table User ( userid integer primary key autoincrement, " +
            "userEmail text unique not null, " +
            "userPassword text not null," +
            "firstName text not null, "  +
            "secondName text, " +
            "userDOB text);";

    private static final String ACCOUNTID 	    = "accountId";
    private static final String ACCOUNTNAME 	= "AName";
    private static final String AAMOUNT    = "Aamount";
    private static final String AREMARK    = "Aremark";
    private static final String ACCOUNT_TABLE 	= "Account";

    private static final String CREATE_TABLE_ACCOUNT = "create table Account (accountId integer primary key autoincrement, " +
            "Aname text," +
            "Aamount number, "+
            "Aremark text, "+
            "userid integer, " +
            "foreign key(userid) references User(userid));";

    private static final String PAYID 	    = "payId";
    private static final String PCATE 	= "Pcate";
    private static final String PNAME 	= "Pname";
    private static final String PAMOUNT    = "Pamount";
    private static final String PDATE    = "Pdate";
    private static final String  PTIME	    = "Ptime";
    private static final String  PREMARK	    = "Premark";
    private static final String PAY_TABLE 	= "Pay";

    private static final String CREATE_TABLE_PAY = "create table Pay (payId integer primary key autoincrement, " +
            "Pcate text," +
            "Pname text," +
            "Pamount number, "+
            "Pdate text, "+
            "Ptime text, "+
            "Premark text, " +
            "Aname text, " +
            "foreign key(Aname) references Account(Aname));";

    private static final String DATABASE_NAME 	= "DailyMoneyDB";
    private static final int DATABASE_VERSION 	= 1;

    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;
    // Constructor
    public Database(Context ctx)
    {
        //
        this.context 	= ctx;
        DBHelper 		= new DatabaseHelper(context);
    }

    public Database open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    // ///////////////////////nested dB helper class ///////////////////////////////////////
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        //
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        //
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_TABLE_USER);
            db.execSQL(CREATE_TABLE_ACCOUNT);
            db.execSQL(CREATE_TABLE_PAY);
        }
        @Override
        //
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            // dB structure change..
        }
    }
    public void close()
    {
        DBHelper.close();
    }
    //////////////////////////// end nested dB helper class //////////////////////////////////////

    public long insertUser(String userEmail, String userPassword,String fristname, String secondName, String userDOB)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USEREMAIL, userEmail);
        initialValues.put(KEY_USERPASSWORD, userPassword);
        initialValues.put(KEY_FIRSTNAME, fristname);
        initialValues.put(KEY_SECONDNAME, secondName);
        initialValues.put(KEY_USERDOB, userDOB);
        return db.insert(USER_TABLE, null, initialValues);
    }

    public boolean deleteUser(String email)
    {
        //
        return db.delete(USER_TABLE, KEY_USEREMAIL + "=" + email, null) > 0;
    }
    public Cursor getAllUser()
    {
        return db.query(USER_TABLE, new String[]
                        {
                                KEY_USERID,
                                KEY_USEREMAIL,
                                KEY_USERPASSWORD,
                                KEY_FIRSTNAME,
                                KEY_SECONDNAME,
                                KEY_USERDOB
                        },
                null, null, null, null, null);
    }

    public Cursor getUser(String email, String password) throws SQLException
    {
        String[] selectionArgs = {email, password};
        return db.query(true, USER_TABLE, new String[]
                        {
                                KEY_USERID,
                                KEY_USEREMAIL,
                                KEY_USERPASSWORD,
                                KEY_FIRSTNAME,
                                KEY_SECONDNAME,
                                KEY_USERDOB
                        },
                KEY_USEREMAIL + "=? and " + KEY_USERPASSWORD + "=?",  selectionArgs, null, null, null, null);
    }

    public Cursor checkUser(String email) throws SQLException
    {
        String[] selectionArgs = {email};
        return db.query(true, USER_TABLE, new String[]
                        {
                                KEY_USERID,
                                KEY_USEREMAIL,
                                KEY_USERPASSWORD,
                                KEY_FIRSTNAME,
                                KEY_SECONDNAME,
                                KEY_USERDOB
                        },
                KEY_USEREMAIL + "=? ",  selectionArgs, null, null, null, null);
    }

    public Cursor getAllAccount()
    {
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

    public Cursor getAllPay()
    {
        return db.query(PAY_TABLE, new String[]
                        {
                                PAYID +" as _id",
                                PCATE,
                                PNAME,
                                PAMOUNT,
                                PDATE,
                                PTIME,
                                PREMARK,
                                ACCOUNTNAME
                        },
                null, null, null, null, "date("+PDATE+") desc"
        );
    }

    public Cursor seearchPay(String str){
        String[] selectionArgs = {"%"+str+"%","%"+str+"%","%"+str+"%","%"+str+"%","%"+str+"%"};
        return db.query(PAY_TABLE, new String[]
                        {
                                PAYID +" as _id",
                                PCATE,
                                PNAME,
                                PAMOUNT,
                                PDATE,
                                PTIME,
                                PREMARK,
                                ACCOUNTNAME
                        },
                PCATE+" like ? or "+PNAME+" like ? or " +PAMOUNT+" like ? or "+PREMARK+" like ? or "+ACCOUNTNAME+" like ?", selectionArgs, null, null, "date("+PDATE+") desc"
        );
    }
    public Cursor monthPay(String str){
        String[] selectionArgs = {str+"%"};
        return db.query(PAY_TABLE, new String[]
                        {
                                PAYID +" as _id",
                                PCATE,
                                PNAME,
                                PAMOUNT,
                                PDATE,
                                PTIME,
                                PREMARK,
                                ACCOUNTNAME
                        },
                PDATE+" like ?", selectionArgs, null, null, "date("+PDATE+") desc"
        );
    }

    public boolean deletePay(Integer payid)
    {
        //
        return db.delete(PAY_TABLE, PAYID + "=" + payid, null) > 0;
    }

    public long insertPay(String pcate,String pname, Double pamount,String pdate, String ptime, String premark,String accountName)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(PCATE, pcate);
        initialValues.put(PNAME, pname);
        initialValues.put(PAMOUNT, pamount);
        initialValues.put(PDATE, pdate);
        initialValues.put(PTIME, ptime);
        initialValues.put(PREMARK, premark);
        initialValues.put(ACCOUNTNAME, accountName);
        return db.insert(PAY_TABLE, null, initialValues);
    }

}