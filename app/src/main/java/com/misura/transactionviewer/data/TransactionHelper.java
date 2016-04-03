package com.misura.transactionviewer.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kmisura on 03/04/16.
 */
public class TransactionHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "transaction.db";

    public TransactionHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TransactionsContract.TransactionEntry.TABLE_NAME + " (" +
                TransactionsContract.TransactionEntry._ID + " INTEGER PRIMARY KEY , " +
                TransactionsContract.TransactionEntry.COLUMN_SKU + " TEXT NOT NULL, " +
                TransactionsContract.TransactionEntry.COLUMN_CURRENCY + " REAL NOT NULL, " +
                TransactionsContract.TransactionEntry.COLUMN_AMOUNT + " REAL NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_TRANSACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TransactionsContract.TransactionEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
