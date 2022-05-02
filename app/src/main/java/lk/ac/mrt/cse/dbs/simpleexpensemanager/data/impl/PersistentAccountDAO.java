package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * This is the class for Persistent Account DAO which extends SQLiteOpenHelper and
 * implements AccountDAO interface
 * **/

public class PersistentAccountDAO extends SQLiteOpenHelper implements AccountDAO {

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

    public PersistentAccountDAO(@Nullable Context context) {
        super(context, "bank.db", null, 1);
    }

    @Override
    public List<String> getAccountNumbersList() {

        List<String> accountNumbers = new ArrayList<>();
        String sql = "SELECT "+ COLUMN_ACCOUNT_NUMBER + " FROM "+ TABLE_ACCOUNTS ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);

        if(cursor.moveToFirst()){
            do{
                String accountNumber = cursor.getString(0);
                accountNumbers.add(accountNumber);
            }
            while (cursor.moveToNext());
        }else{
            //failure
        }
        cursor.close();
        db.close();
        return accountNumbers;


    }

    @Override
    public List<Account> getAccountsList() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM "+ TABLE_ACCOUNTS ;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);

        if(cursor.moveToFirst()){
            do{
                String accountNumber = cursor.getString(0);
                String bankName = cursor.getColumnName(1);
                String holder = cursor.getString(2);
                double balance = cursor.getDouble(3);

                Account account = new Account(accountNumber,bankName,holder,balance);
                accounts.add(account);
            }
            while (cursor.moveToNext());
        }else{
            //failure
        }
        cursor.close();
        db.close();
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        String sql = "SELECT * FROM "+ TABLE_ACCOUNTS+ " WHERE "+ COLUMN_ACCOUNT_NUMBER+ "=\"" + accountNo +"\";";
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(sql,null);
        Account account = null;

        if(cursor.moveToFirst()){
            do{
                String accountNumber = cursor.getString(0);
                String bankName = cursor.getColumnName(1);
                String holder = cursor.getString(2);
                double balance = cursor.getDouble(3);

                account = new Account(accountNumber,bankName,holder,balance);

            }
            while (cursor.moveToNext());
        }else{
            //failure
        }
        cursor.close();
        db.close();
        return account;

    }

    @Override
    public void addAccount(Account account) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ACCOUNT_NUMBER,account.getAccountNo());
        cv.put(COLUMN_BANK_NAME,account.getBankName());
        cv.put(COLUMN_ACCOUNT_HOLDER_NAME,account.getAccountHolderName());
        cv.put(COLUMN_BALANCE,account.getBalance());

        db.insert(TABLE_ACCOUNTS,null,cv);


    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = getWritableDatabase();
        String sql = "DELETE FROM "+ TABLE_ACCOUNTS+" WHERE "+ COLUMN_ACCOUNT_NUMBER + "=\"" + accountNo + "\";" ;
        db.execSQL(sql);
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        Account account = this.getAccount(accountNo);
        double updatedBalance;
        if(expenseType == ExpenseType.EXPENSE){
            updatedBalance = account.getBalance() - amount;
        }else{
            updatedBalance = account.getBalance() + amount;
        }
        SQLiteDatabase db = getWritableDatabase();

        String sql = "UPDATE "+ TABLE_ACCOUNTS + " SET " + COLUMN_BALANCE + "=" + updatedBalance + " WHERE "+ COLUMN_ACCOUNT_NUMBER + "=\"" + accountNo + "\";";
        db.execSQL(sql);

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
