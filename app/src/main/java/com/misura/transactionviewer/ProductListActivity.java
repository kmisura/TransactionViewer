package com.misura.transactionviewer;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.misura.transactionviewer.data.TransactionsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of Products. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ProductDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ProductListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ProductListActivity.class.getSimpleName();
    private static final String PREF_DATA_ALREADY_IN_DB = "data_already_in_db";

    private boolean mTwoPane;
    private ReadTransactionsTask mReadTransactionsTask;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.product_list);

        if (findViewById(R.id.product_detail_container) != null) {
            mTwoPane = true;
        }

        mReadTransactionsTask = new ReadTransactionsTask(this);
        mReadTransactionsTask.execute();

    }

    @Override
    protected void onDestroy() {
        if (mReadTransactionsTask != null) {
            mReadTransactionsTask.cancel(true);
            mReadTransactionsTask.setActivity(null);
        }
        super.onDestroy();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<ProductOverview> mValues;

        public SimpleItemRecyclerViewAdapter(List<ProductOverview> items) {
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
            holder.mIdView.setText(mValues.get(position).sku);
            holder.mContentView.setText("" + mValues.get(position).numTransactions);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ProductDetailFragment.ARG_ITEM_ID, holder.mItem.sku);
                        ProductDetailFragment fragment = new ProductDetailFragment();
                        fragment.setArguments(arguments);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.product_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ProductDetailActivity.class);
                        intent.putExtra(ProductDetailFragment.ARG_ITEM_ID, holder.mItem.sku);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public ProductOverview mItem;

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

    private static class ReadTransactionsTask extends AsyncTask<Void, Void, List<ProductOverview>> {
        private ProductListActivity mActivity;

        /**
         * @param activity Parent activity. Task is static to avoid memory leaks in case of long operations. Activity should be set
         *                 to null using the setter method when parent activity is destroyed.
         */
        ReadTransactionsTask(ProductListActivity activity) {
            mActivity = activity;
        }

        void setActivity(ProductListActivity activity) {
            mActivity = activity;
        }

        @Override
        protected List<ProductOverview> doInBackground(Void... params) {
            Map<String, Integer> counterMap = new HashMap<>();  //counts number of transactions for each sku
            BufferedReader br = null;
            try {
                InputStream is = mActivity.getResources().openRawResource(R.raw.transactions);
                br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();
                JSONArray jsonArray = new JSONArray(line);

                SharedPreferences prefs = mActivity.getSharedPreferences(ProductListActivity.class.getSimpleName(), Context.MODE_PRIVATE);
                boolean saveToDb = !prefs.getBoolean(PREF_DATA_ALREADY_IN_DB, false);
                if (saveToDb) {
                    saveTransactionsToDb(jsonArray);
                    if(isCancelled()){
                        return null;
                    }
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(PREF_DATA_ALREADY_IN_DB, true);
                    editor.apply();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonTransaction = jsonArray.getJSONObject(i);
                    String sku = jsonTransaction.getString("sku");
                    if (!counterMap.containsKey(sku)) {
                        counterMap.put(sku, 0);
                    }
                    counterMap.put(sku, counterMap.get(sku) + 1);
                }

                List<ProductOverview> returnList = new ArrayList<>();
                for (String sku : counterMap.keySet()) {
                    returnList.add(new ProductOverview(sku, counterMap.get(sku)));
                }

                Collections.sort(returnList, new Comparator<ProductOverview>() {
                    @Override
                    public int compare(ProductOverview lhs, ProductOverview rhs) {
                        return lhs.sku.compareTo(rhs.sku);
                    }
                });

                return returnList;
            } catch (Exception e) {
                Log.e(LOG_TAG, "There was a problem while reading the transactions file", e);
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "There was a problem while closing the file", e);
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }

        private void saveTransactionsToDb(JSONArray jsonTransactions) throws JSONException {
            ContentValues[] cvArray = new ContentValues[jsonTransactions.length()];

            for (int i = 0; i < jsonTransactions.length(); i++) {
                if (isCancelled())
                    break;
                JSONObject transaction = jsonTransactions.getJSONObject(i);
                ContentValues transactionValues = new ContentValues();

                transactionValues.put(TransactionsContract.TransactionEntry.COLUMN_SKU, transaction.getString("sku"));
                transactionValues.put(TransactionsContract.TransactionEntry.COLUMN_AMOUNT, transaction.getDouble("amount"));
                transactionValues.put(TransactionsContract.TransactionEntry.COLUMN_CURRENCY, transaction.getString("currency"));

                cvArray[i] = transactionValues;
            }

            if(mActivity != null) {
                mActivity.getContentResolver().bulkInsert(TransactionsContract.TransactionEntry.CONTENT_URI, cvArray);
            }
        }

        @Override
        protected void onPostExecute(List<ProductOverview> overviews) {
            if (mActivity != null && overviews != null) {
                mActivity.mRecyclerView.setAdapter(mActivity.new SimpleItemRecyclerViewAdapter(overviews));
            }
        }
    }

    private static class ProductOverview {
        private String sku;
        private int numTransactions;

        public ProductOverview(String sku, int numTransactions) {
            this.sku = sku;
            this.numTransactions = numTransactions;
        }
    }
}
