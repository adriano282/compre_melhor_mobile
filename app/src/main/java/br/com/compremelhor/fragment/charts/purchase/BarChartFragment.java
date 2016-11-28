package br.com.compremelhor.fragment.charts.purchase;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.impl.DAOPurchase;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.util.function.MyFunction;
import br.com.compremelhor.util.function.Predicate;
import br.com.compremelhor.util.helper.ComputePurchaseLinesHelper;
import br.com.compremelhor.util.helper.ComputePurchaseListHelper;
import br.com.compremelhor.util.helper.DatabaseHelper;
import br.com.compremelhor.view.PurchaseListView;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.PieChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.PieChartView;

import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.SP_USER_ID;
import static br.com.compremelhor.util.Constants.SP_VIEW_PURCHASE_BY_YEAR;

/**
 * Created by adriano on 13/08/16.
 */
public class BarChartFragment extends Fragment {

    private PieChartView chartTop;
    private ColumnChartView chartBotton;

    private PieChartData lineData;
    private ColumnChartData columnData;

    private TextView lblPirChart;
    Map<String, Double> result;
    Double sumBySelectedMonth;

    TextView lblBarChart;
        List<Purchase> purchaseList;
    PurchaseListView plv;
    SharedPreferences preferences;
    DAOPurchase dao;

    public BarChartFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        dao = DAOPurchase.getInstance(getActivity());

        purchaseList =
                dao.findAllByAttribute(DatabaseHelper.Purchase._USER_ID,
                        String.valueOf(preferences.getInt(SP_USER_ID, 0)));

        boolean byYear = preferences.getBoolean(SP_VIEW_PURCHASE_BY_YEAR, false);

        plv = ComputePurchaseListHelper
                        .getPurchaseListView(purchaseList, byYear, !byYear);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater
                .inflate(R.layout.fragment_purchase_list_chart,
                        container,
                        false);
        String header = plv.getListDataHeader().get(0).getKey();
        lblPirChart =  (TextView) rootView.findViewById(R.id.pie_chart_label);
        lblPirChart.setText(header);

        chartTop = (PieChartView) rootView.findViewById(R.id.pie_chart);
        chartTop.setOnValueTouchListener( new ValueTouchListener());
        lblBarChart = (TextView) rootView.findViewById(R.id.bar_chart_lbl);
        if (preferences.getBoolean(SP_VIEW_PURCHASE_BY_YEAR, false)) {
            lblBarChart.setText("Gasto Total por Ano");
        } else {
            lblBarChart.setText("Gasto Total por Mês");
        }

        generatePieDate(header, preferences.getBoolean(SP_VIEW_PURCHASE_BY_YEAR, false));


        chartBotton = (ColumnChartView) rootView.findViewById(R.id.chart_bottom);
        generateColumnData(preferences.getBoolean(SP_VIEW_PURCHASE_BY_YEAR, false));

        return rootView;
    }

    private void generatePieDate(String targetString, boolean year) {
        Predicate<Purchase> filter;
        lblPirChart.setText(targetString);
        if (year)
        {
            final int currentTarget = Integer.valueOf(targetString.substring(targetString.indexOf("/") + 1).trim());
            filter = new Predicate<Purchase>() {
                @Override
                public boolean test(Purchase p) {
                    return p.getDateCreated().get(Calendar.YEAR) == currentTarget;
                }
            };
        }
        else
        {
            ComputePurchaseListHelper.Months month = ComputePurchaseListHelper.Months.valueOf(targetString.split("/")[0].trim());
            final int currentTarget = Integer.valueOf(month.ordinal());
            filter = new Predicate<Purchase>() {
                @Override
                public boolean test(Purchase p) {
                    return p.getDateCreated().get(Calendar.MONTH) == currentTarget;
                }
            };
        }

        MyFunction<PurchaseLine, String> groupFunction = new MyFunction<PurchaseLine, String>() {
            @Override
            public String apply(PurchaseLine purchaseLine) {
                return purchaseLine.getCategory();
            }
        };

        MyFunction<PurchaseLine, Double> computeFunction = new MyFunction<PurchaseLine, Double>() {
            @Override
            public Double apply(PurchaseLine purchaseLine) {
                return purchaseLine.getSubTotal().doubleValue();
            }
        };

        List<PurchaseLine> lines = new ArrayList<>();

        for (Purchase p : purchaseList) {
            if (filter.test(p)) {
                lines.addAll(p.getItems());
            }
        }

        result = ComputePurchaseLinesHelper
                .getGroupedSum(lines,
                        groupFunction,
                        computeFunction);



        Iterator<Map.Entry<String, Double>> iterator = result.entrySet().iterator();
        List<SliceValue> values = new ArrayList<>();
        sumBySelectedMonth = 0.0;

        while(iterator.hasNext()) {
            Map.Entry<String, Double> entry = iterator.next();

            SliceValue sliceValue = new SliceValue(entry.getValue().floatValue(), ChartUtils.pickColor());
            sliceValue.setLabel(entry.getKey());
            values.add(sliceValue);
            sumBySelectedMonth += entry.getValue();
        }


        lineData = new PieChartData(values);
        lineData.setHasLabels(true);
        lineData.setHasLabelsOnlyForSelected(false);
        lineData.setHasLabelsOutside(false);
        lineData.setHasCenterCircle(false);
        lineData.setSlicesSpacing(24);

        //lineData.setCenterText1("% Gastos Por Categoria");

        // Get roboto-italic font.
        //Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Italic.ttf");
        //lineData.setCenterText1Typeface(tf);

        // Get font size from dimens.xml and convert it to sp(library uses sp values).
        //lineData.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity,
        //(int) getResources().getDimension(R.dimen.pie_chart_text1_size)));
        chartTop.setPieChartData(lineData);
    }

    private class ValueTouchListener implements PieChartOnValueSelectListener {

        @Override
        public void onValueSelected(int arcIndex, SliceValue value) {
            String whenSelected =
                    String.format("%s: %,.2f%% - R$ %,.2f",
                            new String(value.getLabel()), (value.getValue()/sumBySelectedMonth)*100,
                            value.getValue());

            Toast.makeText(getActivity(), whenSelected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }


    private void generateColumnData(boolean byYear) {
        List<AxisValue> axisValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;

/**        if (byYear) {
            plv = ComputePurchaseListHelper
                    .getPurchaseListView(purchaseList, byYear, !byYear);
        }
**/
        Iterator<Map.Entry<Map.Entry<String, Double>, List<Map<String, Object>>>> iterator =
                plv.getListDataChild().entrySet().iterator();



        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<Map.Entry<String, Double>, List<Map<String, Object>>> entry = iterator.next();

            values = new ArrayList<>();
            values.add(new SubcolumnValue((Float.valueOf(entry.getKey().getValue().toString())), ChartUtils.pickColor()));


            String header = entry.getKey().getKey();
            axisValues.add(new AxisValue(i++).setLabel(header));

            columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
        }

        columnData = new ColumnChartData(columns);
        columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2));

        chartBotton.setColumnChartData(columnData);

        chartBotton.setOnValueTouchListener(new ColumnChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                Log.d("ColumnChart", "columnIndex: " + columnIndex);
                Log.d("ColumnChart", "subcolumnIndex: " + subcolumnIndex);
                Log.d("ColumnChart", "value: " + value);


                String target = String.valueOf(chartBotton.getChartData().getAxisXBottom().getValues().get(columnIndex).getLabelAsChars());
                Log.d("ColumnChart", "column Label: " + target);
                generatePieDate(target, preferences.getBoolean(SP_VIEW_PURCHASE_BY_YEAR, false));
            }

            @Override
            public void onValueDeselected() {

            }
        });

        chartBotton.setZoomType(ZoomType.HORIZONTAL);
        chartBotton.setZoomLevel(0, 0, 1.2f);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
