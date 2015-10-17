package br.com.compremelhor.controller.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.compremelhor.R;
import br.com.compremelhor.controller.adapter.ExpandableListAdapter;

/**
 * Created by adriano on 16/10/15.
 */
public class ShoppingListActivity extends Activity
        implements ExpandableListView.OnGroupExpandListener, ExpandableListView.OnGroupCollapseListener {
    private ExpandableListAdapter listAdapter;
    private ExpandableListView explictView;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list);

        explictView = (ExpandableListView) findViewById(R.id.lv_shopping_list);

        scrollView = (ScrollView) findViewById(R.id.svShoppingList);


        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        explictView.setAdapter(listAdapter);
        }

    @Override
    public void onGroupExpand(int groupPosition) {
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) explictView.getLayoutParams();
        param.height = ( explictView.getChildCount() * explictView.getHeight());
        Log.v("SCRIP", "height -->>" + param.height);
        explictView.setLayoutParams(param);
        explictView.refreshDrawableState();
        scrollView.refreshDrawableState();
    }

    @Override
    public void onGroupCollapse(int groupPosition) {
        LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) explictView.getLayoutParams();
        param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        Log.v("SCRIP", "height -->>" + param.height);
        explictView.setLayoutParams(param);
        explictView.refreshDrawableState();
        scrollView.refreshDrawableState();
    }


    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listDataHeader.add("CATEGORIA CARNES");
        listDataHeader.add("CATEGORIA BEBIDAS");
        listDataHeader.add("CATEGORIA GULOSEIMAS");

        List<String> carnes = new ArrayList<String>();
        carnes.add("Passarinho");
        carnes.add("Frango");
        carnes.add("hamburguer");


        List<String> drinks = new ArrayList<String>();
        drinks.add("Coca-cola");
        drinks.add("Dell-Vale");
        drinks.add("Guarana Antertica");

        List<String> sweets = new ArrayList<String>();
        sweets.add("passa-tempo");
        sweets.add("chocolate nestle");
        sweets.add("sorvete");

        listDataChild.put(listDataHeader.get(0), carnes);
        listDataChild.put(listDataHeader.get(1), drinks);
        listDataChild.put(listDataHeader.get(2), sweets);
    }


}
