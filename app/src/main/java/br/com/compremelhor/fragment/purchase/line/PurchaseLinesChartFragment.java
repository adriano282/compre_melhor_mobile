package br.com.compremelhor.fragment.purchase.line;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.compremelhor.R;
import br.com.compremelhor.fragment.charts.purchase.item.PieChartFragment;

/**
 * Created by adriano on 22/04/16.
 */
public class PurchaseLinesChartFragment extends Fragment{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_one, new PieChartFragment()).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_purchase_lines_chart, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        
    }
}
