package br.com.compremelhor.fragment.purchase;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import br.com.compremelhor.R;
import br.com.compremelhor.activity.PurchaseActivity;
import br.com.compremelhor.adapter.PurchaseExpandableListAdapter;
import br.com.compremelhor.dao.impl.DAOPurchase;
import br.com.compremelhor.dao.impl.DAOPurchaseLine;
import br.com.compremelhor.model.Purchase;
import br.com.compremelhor.model.PurchaseLine;
import br.com.compremelhor.util.helper.ComputePurchaseListHelper;
import br.com.compremelhor.util.helper.DatabaseHelper;
import br.com.compremelhor.view.PurchaseListView;

import static br.com.compremelhor.util.Constants.EXTRA_PARTNER_ID;
import static br.com.compremelhor.util.Constants.EXTRA_PURCHASE_ID;
import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.REQUEST_CODE_PURCHASE_VIEWED;
import static br.com.compremelhor.util.Constants.SP_USER_ID;
import static br.com.compremelhor.util.Constants.SP_VIEW_PURCHASE_BY_YEAR;

/**
 * Created by adriano on 12/08/16.
 */
public class PurchaseListFragment extends Fragment {
    private static PurchaseListFragment instance;
    private static final String TAG = "PurchaseListFrament";

    private PurchaseExpandableListAdapter listAdapter;
    private ExpandableListView explicitView;

    private SharedPreferences preferences;
    private DAOPurchase dao;

    private Map<Map.Entry<String, Double>, List<Map<String, Object>>> childDataList;
    private List<Map.Entry<String, Double>> headerDataList;

    public static PurchaseListFragment newInstance(String mTag) {
        Log.d(TAG, "newInstance");
        if (instance == null) {
            instance = new PurchaseListFragment();
            Log.d(TAG, "instance is null");
        }

        Bundle args = new Bundle();
        args.putString("mTag", mTag);
        instance.setArguments(args);
        return instance;

    }

    @Override
    public void onCreate(Bundle state) {
        Log.d(TAG, "onCreate");
        super.onCreate(state);
        preferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        dao = DAOPurchase.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_purchase_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        fillFields();
        setWidgets();
    }

    public void changeCat(PurchaseLine p, String ca) {
        p.setCategory(ca);
        DAOPurchaseLine.getInstance(getActivity()).insertOrUpdate(p);
    }
    public void changeDate(Purchase p, int month) {
        Calendar date = Calendar.getInstance();
        date.set(Calendar.MONTH, month);

        p.setDateCreated(date);

        dao.insertOrUpdate(p);
    }

    public void fillFields() {
        Log.d(TAG, "fillFields");
        List<Purchase> purchaseList =
                dao.findAllByAttribute(DatabaseHelper.Purchase._USER_ID,
                        String.valueOf(preferences.getInt(SP_USER_ID, 0)));

        boolean byYear = preferences.getBoolean(SP_VIEW_PURCHASE_BY_YEAR, false);

        PurchaseListView plv =
                ComputePurchaseListHelper
                        .getPurchaseListView(purchaseList, byYear, !byYear);
        if (plv != null) {
            childDataList = plv.getListDataChild();
            headerDataList = plv.getListDataHeader();
        }
    }

    public void setWidgets() {
        Log.d(TAG, "setWidgets");
        explicitView =
                (ExpandableListView) getActivity()
                        .findViewById(R.id.lv_purchase_list);


        listAdapter =
                new PurchaseExpandableListAdapter(getActivity(),
                        headerDataList,
                        childDataList);

        explicitView.setAdapter(listAdapter);
        explicitView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent,
                                        View v,
                                        int groupPosition,
                                        int childPosition,
                                        long id) {
                Map<String, Object> selectedPurchaseProperties = childDataList
                        .get(headerDataList.get(groupPosition))
                        .get(childPosition);

                int purchaseId = (int) selectedPurchaseProperties
                        .get(DatabaseHelper.Purchase._ID);

                int partnerId = (int) selectedPurchaseProperties
                        .get(DatabaseHelper.Purchase._ESTABLISHMENT_ID);

                Intent intent = new Intent(getActivity(), PurchaseActivity.class);
                intent.putExtra(EXTRA_PURCHASE_ID, purchaseId);
                intent.putExtra(EXTRA_PARTNER_ID, partnerId);
                startActivityForResult(intent, REQUEST_CODE_PURCHASE_VIEWED);
                return true;
            }
        });

        TextView tv = (TextView) getActivity().findViewById(R.id.purchase_list_empty);
        if (headerDataList.size() > 0) {
            tv.setVisibility(TextView.GONE);
        } else {
            tv.setVisibility(TextView.VISIBLE);
        }
    }
}
