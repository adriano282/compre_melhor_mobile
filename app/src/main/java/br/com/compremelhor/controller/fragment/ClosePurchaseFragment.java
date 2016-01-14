package br.com.compremelhor.controller.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.compremelhor.R;

public class ClosePurchaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle state) {
        return inflater.inflate(R.layout.fragment_close_purchase, container, false);
    }
}
