package com.misura.transactionviewer.conversion;

import android.content.Context;
import android.util.Log;

import com.misura.transactionviewer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by kmisura on 03/04/16.
 */
public class ExchangeOffice {
    private static final String LOG_TAG = ExchangeOffice.class.getSimpleName();
    private static ExchangeOffice INSTANCE;
    private Map<String, Integer> mIndexMap;
    private CurrencyConverter mCurrencyConverter;

    public static ExchangeOffice getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ExchangeOffice(context);
        }
        return INSTANCE;
    }

    private ExchangeOffice(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.rates);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine();
            JSONArray jsonArray = new JSONArray(line);
            mIndexMap = buildCurrencyIndexMap(jsonArray);
            mCurrencyConverter = buildCurrencyConverter(mIndexMap.keySet().size(), mIndexMap, jsonArray);
        } catch (Exception e) {
            Log.e(LOG_TAG, "A problem occurred while reading the rates file", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Failed to close the rates file", e);
                }
            }
        }
    }

    private Map<String, Integer> buildCurrencyIndexMap(JSONArray ratesJsonArray) throws JSONException {
        TreeSet<String> possibleCurrencies = new TreeSet<>();
        for (int i = 0; i < ratesJsonArray.length(); i++) {
            JSONObject conv = ratesJsonArray.getJSONObject(i);
            possibleCurrencies.add(conv.getString("from"));
            possibleCurrencies.add(conv.getString("to"));
        }

        Map<String, Integer> returnMap = new HashMap<>();

        Iterator<String> it = possibleCurrencies.iterator();
        int idx = 0;
        while (it.hasNext()) {
            returnMap.put(it.next(), idx++);
        }
        return returnMap;
    }

    private CurrencyConverter buildCurrencyConverter(int V, Map<String, Integer> indexMap, JSONArray ratesJsonArray) throws JSONException {
        List<DirectedEdge> edges = new ArrayList<>();
        for (int i = 0; i < ratesJsonArray.length(); i++) {
            JSONObject conv = ratesJsonArray.getJSONObject(i);
            int curr1 = indexMap.get(conv.getString("from"));
            int curr2 = indexMap.get(conv.getString("to"));
            double rate = conv.getDouble("rate");
            edges.add(new DirectedEdge(curr1, curr2, rate));
        }

        return new ShortestPathCurrencyConverter(V, edges);
    }

    public double exchange(String fromCurrency, String toCurrency, double amount) {
        int idx1 = mIndexMap.get(fromCurrency);
        int idx2 = mIndexMap.get(toCurrency);
        double rate = mCurrencyConverter.getConversionRate(idx1, idx2);
        return amount * rate;
    }
}
