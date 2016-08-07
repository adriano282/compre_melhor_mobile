package br.com.compremelhor.util.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.view.SkuListView;

/**
 * Created by adriano on 23/04/16.
 */
public class ComputePurchaseLinesHelper {

    public static SkuListView getSkuListView(TreeSet<PurchaseLine> items) {
        TreeMap<String, List<String>> listDataChild = new TreeMap<>();
        List<String> listDataHeader = new ArrayList<>();

        TreeMap<String, Double> sumByCategoryMap = new TreeMap<>();

        for (PurchaseLine line : items) {
            String currentCategory = line.getCategory();

            if (!sumByCategoryMap.containsKey(currentCategory)) {
                sumByCategoryMap.put(currentCategory, 0.0);
                listDataHeader.add(currentCategory);
                listDataChild.put(currentCategory, new ArrayList<String>());
            }

            sumByCategoryMap.put(currentCategory,
                    Double.valueOf(String.format("%.2f", (sumByCategoryMap.get(currentCategory) + line.getSubTotal().doubleValue()))));

            String name = line.getProductName();
            listDataChild
                    .get(currentCategory)
                    .add(String.format("%s -/ Qtde: %,.2f -/ R$ %,.2f / %d",
                            name.length() > 13 ? name.substring(0, 13).concat("...") : name,
                            line.getQuantity(),
                            line.getSubTotal(),
                            line.getId()));
        }

        Collections.sort(listDataHeader);

        Iterator<Map.Entry<String, Double>> iterator = sumByCategoryMap.entrySet().iterator();
        double valueTotal = 0.0;
        int i = 0;

        while(iterator.hasNext()) {
            Map.Entry<String, Double> pair = iterator.next();
            String newHeader = pair.getKey() + "/R$ " + pair.getValue();

            valueTotal += pair.getValue();

            listDataChild.put(newHeader, listDataChild.get(listDataHeader.get(i)));
            listDataChild.remove(listDataHeader.get(i));
            listDataHeader.set(i, newHeader);

            i++;
        }

        SkuListView slv = new SkuListView();

        slv.setTotalValue(Double.valueOf(String.format("%.2f", valueTotal)));
        slv.setChildDataList(listDataChild);
        slv.setHeaderDataList(listDataHeader);
        return slv;
    }


    public static Map<String, Double> getSumByCategory(List<PurchaseLine> items) {
        if (items == null) throw new IllegalArgumentException("Items:List<PurchaseLine> can not be null");

        HashMap<String, Double> totalByCategories = new HashMap<>();

        for (PurchaseLine item : items) {
            if (!totalByCategories.containsKey(item.getCategory())) {
                totalByCategories.put(item.getCategory(), 0.0);
            }

            totalByCategories.put(item.getCategory(),
                    totalByCategories.get(item.getCategory()) + item.getSubTotal().doubleValue());
        }

        return totalByCategories;
    }
}
