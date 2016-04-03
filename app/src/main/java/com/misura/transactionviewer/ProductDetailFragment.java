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
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.misura.transactionviewer.conversion.ExchangeOffice;
import com.misura.transactionviewer.data.TransactionsContract;

import java.util.ArrayList;
import java.util.List;

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

    private String mSKU;
    private RecyclerView mRecyclerView;
    private ExchangeOffice mExchangeOffice;

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
                appBarLayout.setTitle("Transactions for " + mSKU);//TODO move to strings.xml and add formatting
            }
        }

        mExchangeOffice = ExchangeOffice.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.product_detail, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.product_detail);

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
        List<Transaction> transactions = new ArrayList<>();
        if (data != null) {
            data.moveToNext();
            while(!data.isAfterLast()){
                Transaction transaction = new Transaction(data.getDouble(COL_AMOUNT), data.getString(COL_CURRENCY));
                transactions.add(transaction);
                data.moveToNext();
            }
        }
        mRecyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(transactions));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Transaction> mValues;

        public SimpleItemRecyclerViewAdapter(List<Transaction> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.product_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(holder.mItem.currency + " " + holder.mItem.amount);
            double amountInPounds = mExchangeOffice.exchange(holder.mItem.currency, "GBP", holder.mItem.amount);
            String description = getString(R.string.amount_in_curr, "GBP", amountInPounds);
            holder.mContentView.setText(description);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Transaction mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }

    private class Transaction{
        private double amount;
        private String currency;

        public Transaction(double amount, String currency) {
            this.amount = amount;
            this.currency = currency;
        }
    }
}
