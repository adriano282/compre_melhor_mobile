package br.com.compremelhor.util.helper;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.util.StringDateKeyComparator;
import br.com.compremelhor.util.EntryDateKeyComparator;
import br.com.compremelhor.util.function.MyFunction;
import br.com.compremelhor.view.PurchaseListView;

/**
 * Created by adriano on 13/08/16.
 */
public class ComputePurchaseListHelper {
    private static final String DOUBLE_VALUE_FORMAT = "%.2f";



    public static PurchaseListView getPurchaseListView(@NonNull List<Purchase> purchaseList,
                                                       boolean byYear,
                                                       boolean byMonthOfYear) {
        if (purchaseList.isEmpty()) { return null; }
        MyFunction<Purchase, String> function = newFunction(byYear, byMonthOfYear);
        return compute(purchaseList, function);
    }

    private static MyFunction<Purchase, String> newFunction(boolean byYear, boolean byMonthOfYear) {

        if ((byYear && byMonthOfYear) || (!byYear && !byMonthOfYear))
        {
            throw new IllegalArgumentException("Both simultaneously byYear and byMonthOfYear can not be true " +
                    "and Function must be either by year or by month of year");
        }


        MyFunction<Purchase, String> function = null;
        if (byYear) {
            function = new MyFunction<Purchase, String>() {
                @Override
                public String apply(Purchase p) {
                    return String.valueOf(p.getDateCreated().get(Calendar.YEAR));
                }
            };
        }
        else if (byMonthOfYear) {
            function = new MyFunction<Purchase, String>() {
                @Override
                public String apply(Purchase p) {
                    return monthWithYear(p.getDateCreated());
                }
            };
        }
        return function;
    }

    private static PurchaseListView compute(List<Purchase> purchaseList,
                                     MyFunction<Purchase, String> function) {

        List<Map.Entry<String, Double>> listDataHeader = new ArrayList<>();
        Map<Map.Entry<String, Double>, List<Map<String, Object>>> listDataChild = new TreeMap<>(new EntryDateKeyComparator());

        Map<String, Double> sum = getSumByFunction(purchaseList, function);

        bindContentByFunction(purchaseList, listDataHeader, listDataChild, function, sum);

        Collections.sort(listDataHeader, new EntryDateKeyComparator());
        return bindAndGetObjectView(listDataHeader, listDataChild);
    }

    /**
     * Create a PurchaseListView and Bind it with parameters
     * @param listDataHeader
     *      String name headers list
     * @param listDataChild
     *      List of map properties for grouping by month
     * @return
     *      A bound PurchaseListView object
     */
    private static PurchaseListView bindAndGetObjectView(List<Map.Entry<String, Double>> listDataHeader,
                                             Map<Map.Entry<String, Double>, List<Map<String, Object>>> listDataChild) {
        PurchaseListView plv = new PurchaseListView();

        plv.setListDataChild(listDataChild);
        plv.setListDataHeader(listDataHeader);
        return plv;
    }

    /**
     * This method formats the header with total value of each month
     * @param listDataHeader
     *      String name headers list
     * @param listDataChild
     *      List of map properties for grouping by month
     */
    private static void formatAndReplaceHeaders(List<Map.Entry<String, Double>> listDataHeader,
                                                Map<Map.Entry<String, Double>, List<Map<String, Object>>> listDataChild,
                                                Map<String, Double> sumByDelimiterGroup) {

        Iterator<Map.Entry<String, Double>> iterator = sumByDelimiterGroup.entrySet().iterator();
        Collections.sort(listDataHeader, new EntryDateKeyComparator());

        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, Double> pair = iterator.next();

            List<Map<String, Object>> save = listDataChild.get(listDataHeader.get(i));
            listDataChild.remove(listDataHeader.get(i));
            listDataChild.put(pair, save);
            listDataHeader.set(i, pair);
            i++;
        }
    }

    private static void bindContentByFunction(List<Purchase> purchaseList,
                                              List<Map.Entry<String, Double>> listDataHeader,
                                              Map<Map.Entry<String, Double>, List<Map<String, Object>>> listDataChild,
                                              MyFunction<Purchase, String> function,
                                              Map<String, Double> sum) {

        SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        for (Purchase p : purchaseList) {
            String delimiterResultFunction = function.apply(p);
            Map.Entry<String, Double> entry =
                    new AbstractMap.SimpleEntry<>(delimiterResultFunction, sum.get(delimiterResultFunction));

            if (!listDataHeader.contains(entry)) {
                    listDataHeader.add(entry);
                    listDataChild.put(entry, new ArrayList<Map<String, Object>>());
            }

            Map<String, Object> purchasePropertiesMap = new HashMap<>();
            purchasePropertiesMap.put(DatabaseHelper.Purchase._ESTABLISHMENT_ID, p.getEstablishment().getId());
            purchasePropertiesMap.put(DatabaseHelper.Purchase.STATUS, p.getStatus());

            Double totalValue = p.getTotalValue().doubleValue() +
                    (p.getFreight() != null ? p.getFreight().getRideValue().doubleValue() : 0.0);


            purchasePropertiesMap.put(DatabaseHelper.Purchase.TOTAL_VALUE, totalValue);
            purchasePropertiesMap.put(DatabaseHelper.Purchase._ID, p.getId());
            purchasePropertiesMap.put(DatabaseHelper.Purchase.DATE_CREATED, dt.format(p.getDateCreated().getTime()));
            purchasePropertiesMap.put(DatabaseHelper.Purchase.LAST_UPDATED, dt.format(p.getLastUpdated().getTime()));

            purchasePropertiesMap.put(DatabaseHelper.Establishment.NAME, p.getEstablishment().getName());


            listDataChild
                    .get(entry)
                    .add(purchasePropertiesMap);
        }
    }

    /**
     *
     * @return
     *      Parsed portuguese string month name: JAN/2016, FEV/2016 ... DEZ/2016
     */
    private static String monthWithYear(Calendar date) {
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        return String.valueOf(
                parseIntMonth(month)) +
                "/" + year;
    }

    /**
     * @param monthOfYear
     *      Year month int index: JANUARY=0; FEBRUARY=1 ... DECEMBER=11
     * @return
     *      Parsed portuguese string month name: JANEIRO, FEVEREIRO ... DEZEMBRO
     */
    private static String parseIntMonth(int monthOfYear) {
        return Months
                .listValues()
                .get(monthOfYear);
    }

    /**
     * Enum class for Months of Year
     */
    public enum Months {
        JANEIRO,
        FEVEREIRO,
        MARÇO,
        ABRIL,
        MAIO,
        JUNHO,
        JULHO,
        AGOSTO,
        SETEMBRO,
        OUTUBRO,
        NOVEMBRO,
        DEZEMBRO;

        /**
         * @return
         *  List objects with string month names
         */
        public static List<String> listValues() {
            List<String> months = new ArrayList<>();
            for (Months m : values()) {
                months.add(m.toString());
            }
            return months;
        }
    }

    private static Map<String, Double> getSumByFunction(List<Purchase> purchaseList, MyFunction<Purchase, String> groupFunction) {
        Map<String, Double> sumByFunction = new TreeMap<>(new StringDateKeyComparator());

        for (Purchase p : purchaseList) {
            String groupDelimiter = groupFunction.apply(p);

            if (!sumByFunction.containsKey(groupDelimiter)) {
                sumByFunction.put(groupDelimiter, 0.0);
            }

            sumByFunction.put(groupDelimiter,
                    Double.valueOf(
                            String.format(DOUBLE_VALUE_FORMAT,
                                    (sumByFunction.get(groupDelimiter)
                                            + p.getTotalValue().doubleValue()
                                    ))));

        }
        return sumByFunction;

    }
}
