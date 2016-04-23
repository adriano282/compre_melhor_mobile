package br.com.compremelhor.view;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by adriano on 23/04/16.
 */
public class SkuListView {
    private TreeMap<String, List<String>> childDataList = new TreeMap<>();
    private List<String> headerDataList = new ArrayList<>();
    private double totalValue;

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public TreeMap<String, List<String>> getChildDataList() {
        return childDataList;
    }

    public void setChildDataList(TreeMap<String, List<String>> childDataList) {
        this.childDataList = childDataList;
    }

    public List<String> getHeaderDataList() {
        return headerDataList;
    }

    public void setHeaderDataList(List<String> headerDataList) {
        this.headerDataList = headerDataList;
    }
}
