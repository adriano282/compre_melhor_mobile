package br.com.compremelhor.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.TreeMap;

import br.com.compremelhor.R;

/**
 * Created by adriano on 16/10/15.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader;
    private TreeMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 TreeMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
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
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosition);
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
        String headerTitle = (String) getGroup(groupPosition);
        String[] split = headerTitle.split("/");
        String title = split[0];
        String value = split[1];

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_header_component, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lbl_first_div_header);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(title);

        TextView lblSubtotal = (TextView) convertView.findViewById(R.id.lbl_sec_div_header);
        lblSubtotal.setTypeface(null, Typeface.BOLD);
        lblSubtotal.setText(value);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childText = (String) getChild(groupPosition, childPosition);
        String[] split = childText.split("/");
        String name = split[0];
        String qtde = split[1];
        String value = split[2];
        String itemId = split[3];

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.shopping_list_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblItem);
        txtListChild.setText(name);

        TextView tvQtde = (TextView) convertView.findViewById(R.id.lblQtde);
        tvQtde.setText(qtde);

        TextView tvValue = (TextView) convertView.findViewById(R.id.lblValueItem);
        tvValue.setText(value);

        TextView tvItemId = (TextView) convertView.findViewById(R.id.tv_purchase_line_id);
        tvItemId.setText(itemId);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
