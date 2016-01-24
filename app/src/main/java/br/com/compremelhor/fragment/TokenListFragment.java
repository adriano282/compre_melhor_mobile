package br.com.compremelhor.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.SimpleAdapter;

import com.stripe.model.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.compremelhor.R;
import br.com.compremelhor.TokenList;

public class TokenListFragment extends ListFragment implements TokenList {
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

    @Override
    public void addToList(Token token) {
        String endingIn = getResources().getString(R.string.endingIn);
        Map<String, String> map = new HashMap<>();
        map.put("last4", endingIn + " " + token.getCard().getLast4());
        map.put("tokenId", token.getId());
        listItems.add(map);
        adapter.notifyDataSetChanged();
    }
}
