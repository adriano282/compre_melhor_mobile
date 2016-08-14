package br.com.compremelhor.fragment.purchase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.compremelhor.R;
import br.com.compremelhor.fragment.charts.purchase.BarChartFragment;

/**
 * Created by adriano on 12/08/16.
 */
public class PurchaseListChartFragment extends Fragment {
    private static PurchaseListChartFragment instance;
    private static final String TAG = "purchaseListChartFrag";

    public static PurchaseListChartFragment newInstance(String mTag) {
        Log.d(TAG, "newInstance");
        if (instance == null) {
            instance = new PurchaseListChartFragment();
            Log.d(TAG, "instance is null");
        }

        Bundle args = new Bundle();
        args.putString("mTag", mTag);
        instance.setArguments(args);
        return instance;

    }

    public void refreshFragment() {
        getActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.purchase_placeholder, new BarChartFragment())
                .commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.purchase_placeholder, new BarChartFragment())
                    .commit();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_purchase, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
