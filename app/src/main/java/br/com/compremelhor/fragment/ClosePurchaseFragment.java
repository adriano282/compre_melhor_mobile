package br.com.compremelhor.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

import br.com.compremelhor.R;
import br.com.compremelhor.activity.PaymentActivity;
import br.com.compremelhor.dao.DAOCart;
import br.com.compremelhor.model.Cart;
import br.com.compremelhor.model.PurchaseLine;

import static br.com.compremelhor.useful.Constants.PREFERENCES;
import static br.com.compremelhor.useful.Constants.REQUEST_CODE_PAYMENT_DONE;
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PAYMENT_DONE:
                if (requestCode == Activity.RESULT_OK) {
                    Toast.makeText(getActivity(), "Compra finalizada e paga com sucesso", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
                break;
        }
    }

    public void onClickedClosePurchaseButton(View view) {
        OnClickListener listener = new OnClickListener();
        new AlertDialog.Builder(getActivity())
                .setTitle("Finalizar compra")
                .setMessage("Deseja realmente fechar a compra?")
                .setPositiveButton(R.string.yes,listener)
                .setNegativeButton(R.string.no, listener)
                .create()
                .show();
    }

    private class OnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int item) {
            switch (item) {
                case DialogInterface.BUTTON_POSITIVE:
                    startActivityForResult(new Intent(getActivity(), PaymentActivity.class), REQUEST_CODE_PAYMENT_DONE);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    Toast.makeText(getActivity(), "Operação cancelada", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
