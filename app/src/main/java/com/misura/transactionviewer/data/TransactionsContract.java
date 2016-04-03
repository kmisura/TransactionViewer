package com.misura.transactionviewer.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by kmisura on 03/04/16.
 */
public class TransactionsContract {

    public static final String CONTENT_AUTHORITY = "com.misura.transactionviewer";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TRANSACTION = "transaction";

    public static final class TransactionEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTION;

        public static final String TABLE_NAME = "transakction";

        public static final String COLUMN_SKU = "sku";
        public static final String COLUMN_CURRENCY = "curr";
        public static final String COLUMN_AMOUNT = "amount";

        public static Uri buildTransactionUriForSku(String sku) {
            return CONTENT_URI.buildUpon().appendPath(sku).build();
        }

        public static String getSKUFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
