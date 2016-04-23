package br.com.compremelhor.fragment.charts;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.impl.DAOEstablishment;
import br.com.compremelhor.dao.impl.DAOPurchase;
import br.com.compremelhor.dao.impl.DAOPurchaseLine;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.util.helper.ComputePurchaseLinesHelper;
import br.com.compremelhor.util.helper.DatabaseHelper;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

import static br.com.compremelhor.util.Constants.EXTRA_PARTNER_ID;
import static br.com.compremelhor.util.Constants.EXTRA_PURCHASE_ID;

/**
 * Created by adriano on 23/04/16.
 */
public class PieChartFragment extends Fragment {

    private PieChartView chart;
    private PieChartData data;

    private boolean hasLabels = true;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = false;
    private boolean hasCenterText1 = false;
    private boolean hasCenterText2 = false;
    private boolean isExploded = true;
    private boolean hasLabelForSelected = false;

    private int purchaseId;
    private int partnerId;

    private DAOEstablishment daoEstablishment;
    private DAOPurchaseLine daoPurchaseLine;

    private Map<String, Double> sumByCategories;
    private double totalValue;

    public PieChartFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.chart_fragment_pie_chart,
                container, false);

        chart = (PieChartView) rootView.findViewById(R.id.pie_chart);
        chart.setOnValueTouchListener(new ValueTouchListener());


        daoEstablishment = DAOEstablishment.getInstance(getActivity());
        daoPurchaseLine = DAOPurchaseLine.getInstance(getActivity());

        purchaseId = getActivity().getIntent().getIntExtra(EXTRA_PURCHASE_ID, 0);
        partnerId = getActivity().getIntent().getIntExtra(EXTRA_PARTNER_ID, 0);

        generateData();
        chart.setPieChartData(data);

        return rootView;
    }


    private void generateData() {
        List<PurchaseLine> items = daoPurchaseLine.findAllByForeignId(DatabaseHelper.PurchaseLine._PURCHASE_ID, purchaseId);
        totalValue = DAOPurchase.getInstance(getActivity()).find(purchaseId).getTotalValue().doubleValue();
        sumByCategories = ComputePurchaseLinesHelper.getSumByCategory(items);
        List<SliceValue> values = new ArrayList<>();

        Set<Map.Entry<String, Double>> entries = sumByCategories.entrySet();

        for (Map.Entry<String, Double> pair : entries) {
            SliceValue sliceValue = new SliceValue(pair.getValue().floatValue(), ChartUtils.pickColor());
            sliceValue.setLabel(pair.getKey());
            values.add(sliceValue);
        }


        data = new PieChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);

        if (isExploded) {
            data.setSlicesSpacing(24);
        }

        if (hasCenterText1) {
            data.setCenterText1("% Gastos Por Categoria");

            // Get roboto-italic font.
            Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
            data.setCenterText1Typeface(tf);

            // Get font size from dimens.xml and convert it to sp(library uses sp values).
            data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
                    (int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
        }

    }

    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            String whenSelected =
                    String.format("%,.2f%% - R$ %,.2f",
                            (value.getValue()/totalValue)*100,
                            value.getValue());

            Toast.makeText(getActivity(), whenSelected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }


}
