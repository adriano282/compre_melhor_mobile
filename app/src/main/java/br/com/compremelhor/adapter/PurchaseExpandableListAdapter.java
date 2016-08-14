package br.com.compremelhor.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import br.com.compremelhor.R;
import br.com.compremelhor.util.helper.DatabaseHelper;

/**
 * Created by adriano on 13/08/16.
 */
public class PurchaseExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<Map.Entry<String, Double>> _listDataHeader;

    /**
     * All Purchases grouped by headers (Map) and,
     * inside each group header is the list of purchases (List) and,
     * each purchase is a purchase properties map (Map):
     *
     *      Map<
     *          Map.Entry<HeaderName, TotalValue>,
     *          List<
     *               Map<
     *                   PurchasePropertyName,
     *                   PurchasePropertyValue
     *                  >
     *             >
     *         >
     */
    private Map<Map.Entry<String, Double>, List<Map<String, Object>>> _listDataChild;
    private final String DELIMITER = "@";

    public PurchaseExpandableListAdapter(Context context, List<Map.Entry<String, Double>> _listDataHeader,
                                          Map<Map.Entry<String, Double>, List<Map<String, Object>>> _listDataChild) {
        this._context = context;
        this._listDataChild = _listDataChild;
        this._listDataHeader = _listDataHeader;
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return _listDataChild
                .get(this._listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Map.Entry<String, Double> headerTitle = (Map.Entry<String, Double>) getGroup(groupPosition);

        String title = headerTitle.getKey();
        String value = "R$ ".concat(headerTitle.getValue().toString());

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater
                    .inflate(R.layout.expandable_list_header_component, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lbl_first_div_header);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(title);


        TextView lblSubtotal = (TextView) convertView
                .findViewById(R.id.lbl_sec_div_header);
        lblSubtotal.setTypeface(null, Typeface.BOLD);
        lblSubtotal.setText(value);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Map<String, Object> childMap = (Map<String, Object>) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.purchase_row, null);
        }

        TextView tvPartner = (TextView) convertView.findViewById(R.id.tv_purchase_row_establishment_name);
        tvPartner.setText(childMap.get(DatabaseHelper.Establishment.NAME).toString());

        TextView tvPurchaseDate = (TextView) convertView.findViewById(R.id.tv_purchase_row_date);
        tvPurchaseDate.setText(childMap.get(DatabaseHelper.Purchase.DATE_CREATED).toString());

        TextView tvPurchaseStatus = (TextView) convertView.findViewById(R.id.tv_purchase_row_status);
        tvPurchaseStatus.setText(childMap.get(DatabaseHelper.Purchase.STATUS).toString());

        TextView tvPurchaseLastUpdated = (TextView) convertView.findViewById(R.id.tv_purchase_row_last_updated);
        tvPurchaseLastUpdated.setText(childMap.get(DatabaseHelper.Purchase.LAST_UPDATED).toString());

        TextView tvPurchaseTotalValue = (TextView) convertView.findViewById(R.id.tv_purchase_row_total_value);
        tvPurchaseTotalValue.setText(childMap.get(DatabaseHelper.Purchase.TOTAL_VALUE).toString());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
