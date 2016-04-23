package br.com.compremelhor.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import br.com.compremelhor.R;
import br.com.compremelhor.adapter.ExpandableListAdapter;
import br.com.compremelhor.dao.impl.DAOEstablishment;
import br.com.compremelhor.dao.impl.DAOPurchaseLine;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.util.helper.ComputePurchaseLinesHelper;
import br.com.compremelhor.util.helper.DatabaseHelper;
import br.com.compremelhor.view.SkuListView;

import static br.com.compremelhor.util.Constants.EXTRA_PARTNER_ID;
import static br.com.compremelhor.util.Constants.EXTRA_PURCHASE_ID;

public class PurchaseLinesFragment extends android.support.v4.app.Fragment  {
    private double valueTotal;
    private TextView tvValueTotal;

    private List<String> headerDataList;
    private TreeMap<String, List<String>> childDataList;

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

        tvValueTotal.setText(String.format("R$ %,.2f", valueTotal));
    }

    protected void setWidgets() {
        ExpandableListView explicitView = (ExpandableListView) getActivity().findViewById(R.id.lv_shopping_list);
        ExpandableListAdapter listAdapter = new ExpandableListAdapter(getActivity(), headerDataList, childDataList);
        explicitView.setAdapter(listAdapter);
        TextView tvPartnerName = (TextView) getActivity().findViewById(R.id.fragment_tv_partner_name);
        tvPartnerName.setText(daoEstablishment.find(partnerId).getName());
        tvValueTotal = (TextView) getActivity().findViewById(R.id.tv_value_total_purchase);
    }

    protected void fillFields() {
        final TreeSet<PurchaseLine> items =
                new TreeSet<>(DAOPurchaseLine.getInstance(getActivity())
                        .findAllByForeignId(DatabaseHelper.PurchaseLine._PURCHASE_ID, purchaseId));

        SkuListView slv = ComputePurchaseLinesHelper.getSkuListView(items);
        childDataList = slv.getChildDataList();
        headerDataList = slv.getHeaderDataList();
        this.valueTotal = slv.getTotalValue();
    }
}
