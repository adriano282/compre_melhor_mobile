package br.com.compremelhor.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import br.com.compremelhor.R;
import br.com.compremelhor.adapter.ExpandableListAdapter;
import br.com.compremelhor.dao.impl.DAOEstablishment;
import br.com.compremelhor.dao.impl.DAOPurchaseLine;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.util.DatabaseHelper;

import static br.com.compremelhor.util.Constants.EXTRA_PARTNER_ID;
import static br.com.compremelhor.util.Constants.EXTRA_PURCHASE_ID;

/**
 * Created by adriano on 22/04/16.
 */
public class PurchaseLinesFragment extends android.support.v4.app.Fragment  {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView explicitView;

    private double valueTotal;
    private TextView tvPartnerName;
    private TextView tvValueTotal;

    private List<String> listDataHeader = new ArrayList<>();
    private HashMap<String, List<String>> listDataChild = new HashMap<>();

    private int purchaseId;
    private int partnerId;

    private DAOEstablishment daoEstablishment;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_purchase_lines, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        daoEstablishment = DAOEstablishment.getInstance(getActivity());

        purchaseId = getActivity().getIntent().getIntExtra(EXTRA_PURCHASE_ID, 0);
        partnerId = getActivity().getIntent().getIntExtra(EXTRA_PARTNER_ID, 0);

        fillFields();
        setWidgets();

        tvValueTotal.setText("R$ " + valueTotal);

    }

    protected void setWidgets() {
        explicitView = (ExpandableListView) getActivity().findViewById(R.id.lv_shopping_list);
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);
        explicitView.setAdapter(listAdapter);
        tvPartnerName = (TextView) getActivity().findViewById(R.id.fragment_tv_partner_name);
        tvPartnerName.setText(daoEstablishment.find(partnerId).getName());
        tvValueTotal = (TextView) getActivity().findViewById(R.id.tv_value_total_purchase);


    }

    protected void fillFields() {
        final TreeSet<PurchaseLine> items =
                new TreeSet<>(DAOPurchaseLine.getInstance(getActivity())
                        .findAllByForeignId(DatabaseHelper.PurchaseLine._PURCHASE_ID, purchaseId));

        listDataChild = new HashMap<>();
        listDataHeader = new ArrayList<>();

        Map<String, Double> sumByCategoryMap = new HashMap<>();

        for (PurchaseLine line : items) {
            String currentCategory = line.getCategory();

            if (!sumByCategoryMap.containsKey(line.getCategory())) {
                sumByCategoryMap.put(line.getCategory(), 0.0);
                listDataHeader.add(currentCategory);
                listDataChild.put(currentCategory, new ArrayList<String>());
            }

            sumByCategoryMap.put(currentCategory, sumByCategoryMap.get(currentCategory) + line.getSubTotal().doubleValue());
            listDataChild
                    .get(currentCategory)
                    .add(line.getProductName() + "/" + line.getQuantity() + "/ R$" + line.getSubTotal() + "/" + line.getId());
        }

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

        this.valueTotal = valueTotal;
    }
}
