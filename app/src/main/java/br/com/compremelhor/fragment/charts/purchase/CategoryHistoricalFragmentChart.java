package br.com.compremelhor.fragment.charts.purchase;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.impl.DAOPurchase;
import br.com.compremelhor.dao.impl.DAOPurchaseLine;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.util.helper.DatabaseHelper;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

import static br.com.compremelhor.util.Constants.EXTRA_CATEGORY_NAME;
import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.SP_USER_ID;

/**
 * Created by adriano on 02/11/16.
 */
public class CategoryHistoricalFragmentChart extends Fragment {
    private LineChartView chart;
    private PreviewLineChartView previewChart;
    private LineChartData data;

    private LineChartData previewData;

    private DAOPurchaseLine itemDao;
    private DAOPurchase purchaseDao;
    private SharedPreferences preferences;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater
                .inflate(R.layout.fragment_category_historical_chart,
                        container, false);

        preferences = getActivity().getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
        itemDao = DAOPurchaseLine.getInstance(getActivity());
        purchaseDao = DAOPurchase.getInstance(getActivity());

        chart = (LineChartView) rootView.findViewById(R.id.chart);
        previewChart = (PreviewLineChartView) rootView.findViewById(R.id.chart_preview);

        generateData();

        chart.setLineChartData(data);

        chart.setZoomEnabled(false);
        chart.setScrollEnabled(false);


        previewChart.setLineChartData(previewData);
        previewChart.setViewportChangeListener(new ViewportChangeListener() {
            @Override
            public void onViewportChanged(Viewport viewport) {
                chart.setCurrentViewport(viewport);
            }
        });

        previewX(false);

        return rootView;
    }

    private void generateData() {
        String categoryTarget = getActivity().getIntent().getStringExtra(EXTRA_CATEGORY_NAME);

        TextView title = (TextView) rootView.findViewById(R.id.category_title);

        title.setText("R$ Categoria " + categoryTarget);

        List<Purchase> purchaseList =  purchaseDao.findAllByAttribute(DatabaseHelper.Purchase._USER_ID, String.valueOf(preferences.getInt(SP_USER_ID, 0)));

        TreeMap<Integer, Double> categorySpentByMonth = new TreeMap<>();

        List<PurchaseLine> lines = new ArrayList<>();
        for (Purchase p : purchaseList) {
            lines.addAll(itemDao.findAllByAttribute(DatabaseHelper.PurchaseLine._PURCHASE_ID, String.valueOf(p.getId())));
        }

        for (PurchaseLine pl : lines) {

            if (!pl.getCategory().equals(categoryTarget)) continue;

            Calendar date = pl.getDateCreated();
            String month = String.valueOf(date.get(Calendar.YEAR)).concat(String.valueOf(date.get(Calendar.MONTH)));

            int monthIndex = Integer.valueOf(month);

            if (!categorySpentByMonth.containsKey(Integer.valueOf(month))) {
                categorySpentByMonth.put(monthIndex, pl.getSubTotal().doubleValue());
                continue;
            }
            categorySpentByMonth.put(monthIndex, categorySpentByMonth.get(monthIndex) + pl.getSubTotal().doubleValue());
        }

        List<PointValue> values = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        float i = 0;

        for (Map.Entry<Integer, Double> pair : categorySpentByMonth.entrySet()) {
            values.add(new PointValue(i, Float.valueOf(pair.getValue().toString())));


            AxisValue axisValue = new AxisValue(i++);
            axisValue.setLabel(formatLabel(pair.getKey().toString()));
            axisValues.add(axisValue);
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN);
        line.setHasPoints(true);
        line.setHasLabels(true);

        List<Line> allLines = new ArrayList<>();
        allLines.add(line);


        data = new LineChartData(allLines);
        data.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        data.setAxisYLeft(new Axis().setHasLines(true));

        previewData = new LineChartData(data);
        previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);
    }

    private void previewX(boolean animate) {
        Viewport temViewport = new Viewport(chart.getMaximumViewport());
        float dx = temViewport.width() / 4;
        temViewport.inset(dx, 0);
        if (animate) {
            previewChart.setCurrentViewportWithAnimation(temViewport);
        } else {
            previewChart.setCurrentViewport(temViewport);
        }
        previewChart.setZoomType(ZoomType.HORIZONTAL);
    }

    String formatLabel(String yearMonth) {
        int monthIndex = Integer.valueOf(yearMonth.substring(4));
        String month = Month.getByIndex(monthIndex -1).getMonth();

        return month.concat("/").concat(yearMonth.substring(0,4));
    }

    enum Month {
        JANUARY("JAN"),
        FEBRUARY("FEV"),
        MARCH("MAR"),
        APRIL("ABR"),
        MAY("MAI"),
        JUNO("JUN"),
        JULY("JUL"),
        AUGUST("AGO"),
        SEPTEMBER("SET"),
        OCTOBER("OUT"),
        NOVEMBER("NOV"),
        DECEMBER("DEZ");

        private String month;

        Month(String month) {
            this.month = month;
        }

        public String getMonth() { return month; }
        static public Month getByIndex(int indexMonth) {
            return values()[indexMonth];
        }
    }
}
