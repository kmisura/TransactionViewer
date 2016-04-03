package com.misura.transactionviewer.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by kmisura on 03/04/16.
 */
public class TransactionsProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TransactionHelper mOpenHelper;

    static final int TRANSACTIONS = 100;
    static final int TRANSACTIONS_BY_SKU = 200;

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static{
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
        sWeatherByLocationSettingQueryBuilder.setTables(TransactionsContract.TransactionEntry.TABLE_NAME);
    }

    private Cursor getTransactionsBySKU(Uri uri, String[] projection, String sortOrder) {
        String sku = TransactionsContract.TransactionEntry.getSKUFromUri(uri);

        String[] selectionArgs;
        String selection = TransactionsContract.TransactionEntry.TABLE_NAME+
                "." + TransactionsContract.TransactionEntry.COLUMN_SKU + " = ? ";

        selectionArgs = new String[]{sku};

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TransactionsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, TransactionsContract.PATH_TRANSACTION, TRANSACTIONS);
        matcher.addURI(authority, TransactionsContract.PATH_TRANSACTION + "/*", TRANSACTIONS_BY_SKU);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TransactionHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TRANSACTIONS_BY_SKU:
                return TransactionsContract.TransactionEntry.CONTENT_ITEM_TYPE;
            case TRANSACTIONS:
                return TransactionsContract.TransactionEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case TRANSACTIONS_BY_SKU: {
                retCursor = getTransactionsBySKU(uri, projection, sortOrder);
                break;
            }
            case TRANSACTIONS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        TransactionsContract.TransactionEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //TODO: not needed now
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //TODO: not needed now
        return 0;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //TODO: not needed now
        return 0;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRANSACTIONS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(TransactionsContract.TransactionEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
