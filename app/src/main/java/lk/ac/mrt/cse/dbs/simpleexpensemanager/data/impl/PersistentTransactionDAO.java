package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * This is the class for Persistent Transaction DAO which extends SQLiteOpenHelper and
 * implements TransactionDAO interface
 * **/

public class PersistentTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {

    //constants for tables and fields of the database
    public static final String TABLE_ACCOUNTS = "ACCOUNTS";
    public static final String COLUMN_ACCOUNT_NUMBER = "ACCOUNT_NUMBER";
    public static final String COLUMN_BANK_NAME = "BANK_NAME";
    public static final String COLUMN_ACCOUNT_HOLDER_NAME = "ACCOUNT_HOLDER_NAME";
    public static final String COLUMN_BALANCE = "BALANCE";
    public static final String TABLE_TRANSACTIONS = "TRANSACTIONS";
    public static final String COLUMN_LOG_DATE = "LOG_DATE";
    public static final String COLUMN_EXPENSE_TYPE = "EXPENSE_TYPE";
    public static final String COLUMN_AMOUNT = "AMOUNT";

    public PersistentTransactionDAO(@Nullable Context context) {
        super(context, "bank.db", null, 1);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_LOG_DATE,date.toString());
        cv.put(COLUMN_ACCOUNT_NUMBER,accountNo);
        cv.put(COLUMN_EXPENSE_TYPE,expenseType.toString());
        cv.put(COLUMN_AMOUNT,amount);

        db.insert(TABLE_TRANSACTIONS,null,cv);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM "+ TABLE_TRANSACTIONS ;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);

        if(cursor.moveToFirst()){
            do{
                String sDate = cursor.getString(0);
                SimpleDateFormat sdf3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

                Date date = null;
                try{
                    date = sdf3.parse(sDate);

                }catch (Exception e){ e.printStackTrace(); }

                String accountNumber= cursor.getString(1);

                String sExpenseType = cursor.getString(2);
                ExpenseType expenseType = (sExpenseType.equals("EXPENSE"))? ExpenseType.EXPENSE : ExpenseType.INCOME;

                double amount = cursor.getDouble(3);

                Transaction transaction = new Transaction(date,accountNumber,expenseType,amount);
                transactions.add(transaction);
            }
            while (cursor.moveToNext());
        }else{
            //failure
        }
        cursor.close();
        db.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM "+ TABLE_TRANSACTIONS + " LIMIT " + limit ;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);

        if(cursor.moveToFirst()){
            do{
                String sDate = cursor.getString(0);
                //Date date = new SimpleDateFormat("dd/MM/yyyy").parse(sDate);
                SimpleDateFormat sdf3 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);

                Date date = null;
                try{
                    date = sdf3.parse(sDate);

                }catch (Exception e){ e.printStackTrace(); }

                String accountNumber= cursor.getString(1);

                String sExpenseType = cursor.getString(2);
                ExpenseType expenseType = (sExpenseType.equals("EXPENSE"))? ExpenseType.EXPENSE : ExpenseType.INCOME;

                double amount = cursor.getDouble(3);

                Transaction transaction = new Transaction(date,accountNumber,expenseType,amount);
                transactions.add(transaction);
            }
            while (cursor.moveToNext());
        }else{
            //failure
        }
        cursor.close();
        db.close();
        return transactions;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String sqlAccount = "CREATE TABLE " + TABLE_ACCOUNTS + " (" + COLUMN_ACCOUNT_NUMBER + " TEXT PRIMARY KEY, " + COLUMN_BANK_NAME + " TEXT, " + COLUMN_ACCOUNT_HOLDER_NAME + " TEXT, " + COLUMN_BALANCE + " REAL)";
        String sqlTrans = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" + COLUMN_LOG_DATE + " DATE PRIMARY KEY, " + COLUMN_ACCOUNT_NUMBER + " TEXT REFERENCES ACCOUNTS (ACCOUNT_NUMBER), " + COLUMN_EXPENSE_TYPE + " TEXT, " + COLUMN_AMOUNT + " REAL)";

        sqLiteDatabase.execSQL(sqlTrans);
        sqLiteDatabase.execSQL(sqlAccount);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
