package br.com.compremelhor.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import br.com.compremelhor.R;

public class TokenListFragment extends ListFragment {
    List<Map<String, String>> listItems = new ArrayList<>();
    SimpleAdapter adapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new SimpleAdapter(getActivity(),
                listItems,
                R.layout.list_item_layout,
                new String[]{"last4", "tokenId"},
                new int[]{R.id.last4, R.id.tokenId});
        setListAdapter(adapter);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setListAdapter(null);
    }



}
