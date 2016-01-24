package br.com.compremelhor.controller.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.NumberFormat;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.DAOCart;
import br.com.compremelhor.model.Cart;
import br.com.compremelhor.model.PurchaseLine;

import static br.com.compremelhor.useful.Constants.PREFERENCES;
import static br.com.compremelhor.useful.Constants.SP_FREIGHT_VALUE;

public class ClosePurchaseFragment extends Fragment {
    private TextView tvSubTotalPurchase;
    private TextView tvSubTotalFreight;
    private TextView tvTotalPurchase;

    private Button btnClosePurchase;

    private SharedPreferences preferences;

    @Override
    public void onViewCreated(View view, Bundle savedState) {
        preferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        tvSubTotalPurchase = (TextView) view.findViewById(R.id.tv_purchase_sub_total);
        tvSubTotalFreight = (TextView) view.findViewById(R.id.tv_freight_sub_total);
        tvTotalPurchase = (TextView) view.findViewById(R.id.tv_value_total_purchase);

        btnClosePurchase = (Button) view.findViewById(R.id.btn_close_purchase);

        Cart cart = DAOCart.getInstance(getActivity()).getCart();
        Double dSubTotalPurchase = 0.0;

        for (PurchaseLine pl : cart.getItems()) {
            dSubTotalPurchase += pl.getSubTotal().doubleValue();
        }

        NumberFormat nf = NumberFormat.getCurrencyInstance();
        tvSubTotalPurchase.setText(nf.format(dSubTotalPurchase));

        Float fValueFreight = preferences.getFloat(SP_FREIGHT_VALUE, new Float(0.0));
        tvSubTotalFreight.setText(nf.format(fValueFreight));

        Double dValueTotal = dSubTotalPurchase + fValueFreight;
        tvTotalPurchase.setText(nf.format(dValueTotal));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle state) {
        return inflater.inflate(R.layout.fragment_close_purchase, container, false);
    }
}
