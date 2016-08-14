package br.com.compremelhor.view;

import java.util.List;
import java.util.Map;

/**
 * Created by adriano on 13/08/16.
 */
public class PurchaseListView {
    private List<Map.Entry<String, Double>> listDataHeader;
    private Map<Map.Entry<String, Double>, List<Map<String, Object>>> listDataChild;

    public Map<Map.Entry<String, Double>, List<Map<String, Object>>> getListDataChild() {
        return listDataChild;
    }

    public void setListDataChild(Map<Map.Entry<String, Double>, List<Map<String, Object>>> listDataChild) {
        this.listDataChild = listDataChild;
    }

    public List<Map.Entry<String, Double>> getListDataHeader() {
        return listDataHeader;
    }

    public void setListDataHeader(List<Map.Entry<String, Double>> listDataHeader) {
        this.listDataHeader = listDataHeader;
    }
}
