package com.misura.transactionviewer.conversion;

/**
 * Created by kmisura on 03/04/16.
 * There can be many kinds of converters.
 * Main differentiating point is in the way they handle rates that weren't specified explicitly.
 * For example, one converter might look for the cheapest possible conversion. But if conversion rates
 * have negative cycles (which they don't in real life - at least not longer than a couple of seconds
 * because people could be making a lot of money by exploiting them) then we could exchange the currencies
 * endlessly in the negative loop and get ever cheaper values.
 * <p>
 * Other converter might simply look for the shortestPath, that is find the minimum number of exchanges
 * necessary to reach the target currency.
 */
public interface CurrencyConverter {
    double getConversionRate(int currency1, int currency2);
}
