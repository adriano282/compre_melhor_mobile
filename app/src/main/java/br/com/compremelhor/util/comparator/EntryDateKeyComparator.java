package br.com.compremelhor.util.comparator;

import android.util.Log;

import java.util.Comparator;
import java.util.Map;

import br.com.compremelhor.util.helper.ComputePurchaseListHelper;

/**
 * Created by adriano on 14/08/16.
 */
public class EntryDateKeyComparator implements Comparator<Map.Entry<String, Double>> {
    @Override
    public int compare(Map.Entry<String, Double> lhs, Map.Entry<String, Double> rhs) {
        String left = lhs.getKey();
        String right = rhs.getKey();

        try {

            String rMon = "";
            String rYe = "";
            String lMon = "";
            String lYe = "";
            if (right.contains("/") && left.contains("/")) {
                rMon = right.split("/")[0];
                rYe = right.split("/")[1];

                lMon = left.split("/")[0];
                lYe = left.split("/")[1];
            } else {
                return Integer.valueOf(left) - Integer.valueOf(right);
            }


            if (Integer.valueOf(lYe).intValue() != Integer.valueOf(rYe).intValue()) {
                return Integer.valueOf(lYe) - Integer.valueOf(rYe);
            }

            ComputePurchaseListHelper.Months rM = ComputePurchaseListHelper.Months.valueOf(rMon);
            ComputePurchaseListHelper.Months lM = ComputePurchaseListHelper.Months.valueOf(lMon);
            return lM.ordinal() - rM.ordinal();
        } catch (Exception e) {
            Log.d("Comparator", e.getMessage());
            return -1;
        }
    }
}
