package com.misura.transactionviewer;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.misura.transactionviewer.data.TransactionsContract;
import com.misura.transactionviewer.dummy.DummyContent;

/**
 * A fragment representing a single Product detail screen.
 * This fragment is either contained in a {@link ProductListActivity}
 * in two-pane mode (on tablets) or a {@link ProductDetailActivity}
 * on handsets.
 */
public class ProductDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final String ARG_ITEM_ID = "item_id";

    private static final int DETAIL_LOADER = 0;
    private static final String[] DETAIL_COLUMNS = {
            TransactionsContract.TransactionEntry._ID,
            TransactionsContract.TransactionEntry.COLUMN_SKU,
            TransactionsContract.TransactionEntry.COLUMN_AMOUNT,
            TransactionsContract.TransactionEntry.COLUMN_CURRENCY,
    };
    public static final int COL_ID = 0;
    public static final int COL_SKU = 1;
    public static final int COL_AMOUNT = 2;
    public static final int COL_CURRENCY = 3;

    private DummyContent.Product mItem;
    private String mSKU;
    private TextView mTextView;

    public ProductDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mSKU = getArguments().getString(ARG_ITEM_ID);

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle("Transactions for ...");//TODO
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.product_detail, container, false);

        mTextView = (TextView) rootView.findViewById(R.id.product_detail);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = TransactionsContract.TransactionEntry.buildTransactionUriForSku(mSKU);
        return new CursorLoader(getActivity(), uri, DETAIL_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            int count = 0;
            while(!data.isAfterLast()){
                count++;
                data.moveToNext();
            }
            mTextView.setText("" + count);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
