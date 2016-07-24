package br.com.compremelhor.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalOAuthScopes;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalProfileSharingActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.impl.DAOEstablishment;
import br.com.compremelhor.model.Establishment;
import br.com.compremelhor.service.CartService;
import br.com.compremelhor.util.helper.PayPalPaymentHelper;

import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.SP_PARTNER_ID;
import static br.com.compremelhor.util.Constants.SP_USER_ID;

public class PaymentFragment extends Fragment {
    private static PaymentFragment instance;
    private static final String TAG = "payment";

    private TextView tvSubTotalPurchase;
    private TextView tvSubTotalFreight;
    private TextView tvTotalPurchase;
    private Button btnClosePurchase;

    private OptionsDialogOnClickListener optionsListener;
    private AlertDialog alertDialogConfirmation;

    private SharedPreferences preferences;
    private ProgressDialog progressDialog;

    private CartService cartService;

    private Double total;

    private int partnerId;

    public static PaymentFragment newInstance(String mTag){
        if (instance == null)
            instance = new PaymentFragment();

        Bundle args = new Bundle();
        args.putString("mTag", mTag);
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onViewCreated(View view, Bundle savedState) {
        preferences = getActivity().getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        partnerId = preferences.getInt(SP_PARTNER_ID, 0);
        cartService = CartService.getInstance(getActivity(),
                preferences.getInt(SP_USER_ID, 0),
                partnerId);
        setWidgets(view);
        fillFields();
    }

    private AlertDialog createDialogConfirmation() {
        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.close_purchase_confirmation)
                .setPositiveButton(R.string.yes, optionsListener)
                .setNegativeButton(R.string.no, optionsListener)
                .create();
    }

    @Override
    public void onDestroy() {
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
        super.onDestroy();
    }

    public void setWidgets(View view) {
        tvSubTotalPurchase = (TextView) view.findViewById(R.id.tv_purchase_sub_total);
        tvSubTotalFreight = (TextView) view.findViewById(R.id.tv_freight_sub_total);
        tvTotalPurchase = (TextView) view.findViewById(R.id.tv_value_total_purchase);
        btnClosePurchase = (Button) view.findViewById(R.id.btn_close_purchase);
        optionsListener = new OptionsDialogOnClickListener();

        alertDialogConfirmation = createDialogConfirmation();

    }

    public void fillFields() {
        if (cartService == null)
            throw new RuntimeException("CartService can not be null here: fillFields PaymentFragment's method");

        NumberFormat nf = NumberFormat.getCurrencyInstance();

        BigDecimal totalFreight = cartService.getFreight() != null ?
            cartService.getFreight().getRideValue() : new BigDecimal(0.0);
        BigDecimal subTotalPurchase = cartService.getPurchase().getTotalValue();

        if (subTotalPurchase == null) subTotalPurchase = new BigDecimal(0.0);

        total = totalFreight.doubleValue() + subTotalPurchase.doubleValue();

        tvSubTotalFreight.setText(nf.format(totalFreight.doubleValue()));
        tvSubTotalPurchase.setText(nf.format(subTotalPurchase.doubleValue()));
        tvTotalPurchase.setText(nf.format(total));

        if (subTotalPurchase.doubleValue() == 0.0) {
            Toast.makeText(getActivity(), "Nenhum item adicionado ao carrinho.", Toast.LENGTH_SHORT).show();
            btnClosePurchase.setEnabled(false);
        } else if (cartService.getFreight() != null && !cartService.getFreight().isComplete()) {
            btnClosePurchase.setEnabled(false);
        }
        else {
            btnClosePurchase.setEnabled(true);
        }
    }


    public void onClickButtonClosePurchase(View v) {
        alertDialogConfirmation = createDialogConfirmation();
        alertDialogConfirmation.show();
    }

    private PayPalOAuthScopes getOauthScopes() {
        /* create the set of required scopes
         * Note: see https://developer.paypal.com/docs/integration/direct/identity/attributes/ for mapping between the
         * attributes you select for this app in the PayPal developer portal and the scopes required here.
         */
        Set<String> scopes = new HashSet<String>(
                Arrays.asList(PayPalOAuthScopes.PAYPAL_SCOPE_EMAIL, PayPalOAuthScopes.PAYPAL_SCOPE_ADDRESS));
        return new PayPalOAuthScopes(scopes);
    }

    private PayPalPayment getPurchaseToBuy(String paymentIntent) {
        Establishment partner = DAOEstablishment.getInstance(getActivity()).find(partnerId);
        return new PayPalPayment(
                new BigDecimal(total), "BRL", "Compra by CompreMelhor - " +
                "Estabelcimento: " + partner.getName(), paymentIntent
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getActivity(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PayPalPaymentHelper.config);
        getActivity().startService(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle state) {
        return inflater.inflate(R.layout.fragment_close_purchase, container, false);
    }

    protected void displayResultText(String result) {
        Toast.makeText(
                getActivity().getApplicationContext(),
                result, Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayPalPaymentHelper.REQUEST_CODE_PAYMENT) {

            if (resultCode == Activity.RESULT_OK) {


                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */

                        //displayResultText("Payment Confirmation info received from PayPal");//
                        cartService.closePurchase();
                        CartService.invalidateInstance();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            } else if (requestCode == PayPalPaymentHelper.REQUEST_CODE_FUTURE_PAYMENT) {
                if (resultCode == Activity.RESULT_OK) {
                    PayPalAuthorization auth =
                            data.getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                    if (auth != null) {
                        try {
                            Log.i("FuturePaymentExample", auth.toJSONObject().toString(4));

                            String authorization_code = auth.getAuthorizationCode();
                            Log.i("FuturePaymentExample", authorization_code);

                            sendAuthorizationToServer(auth);
                            displayResultText("Future Payment code received from PayPal");

                        } catch (JSONException e) {
                            Log.e("FuturePaymentExample", "an extremely unlikely failure occurred: ", e);
                        }
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.i("FuturePaymentExample", "The user canceled.");
                } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                    Log.i(
                            "FuturePaymentExample",
                            "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
                }
            } else if (requestCode == PayPalPaymentHelper.REQUEST_CODE_PROFILE_SHARING) {
                if (resultCode == Activity.RESULT_OK) {
                    PayPalAuthorization auth =
                            data.getParcelableExtra(PayPalProfileSharingActivity.EXTRA_RESULT_AUTHORIZATION);
                    if (auth != null) {
                        try {
                            Log.i("ProfileSharingExample", auth.toJSONObject().toString(4));

                            String authorization_code = auth.getAuthorizationCode();
                            Log.i("ProfileSharingExample", authorization_code);

                            sendAuthorizationToServer(auth);
                            displayResultText("Profile Sharing code received from PayPal");

                        } catch (JSONException e) {
                            Log.e("ProfileSharingExample", "an extremely unlikely failure occurred: ", e);
                        }
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.i("ProfileSharingExample", "The user canceled.");
                } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                    Log.i(
                            "ProfileSharingExample",
                            "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
                }
            }
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

        /**
         * TODO: Send the authorization response to your server, where it can
         * exchange the authorization code for OAuth access and refresh tokens.
         *
         * Your server must then store these tokens, so that your server code
         * can execute payments for this user in the future.
         *
         * A more complete example that includes the required app-server to
         * PayPal-server integration is available from
         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
         */

    }

    public void onFuturePaymentPurchasePressed(View pressed) {
        // Get the Client Metadata ID from the SDK
        String metadataId = PayPalConfiguration.getClientMetadataId(getActivity());

        Log.i("FuturePaymentExample", "Client Metadata ID: " + metadataId);

        // TODO: Send metadataId and transaction details to your server for processing with
        // PayPal...
        displayResultText("Client Metadata Id received from SDK");
    }

    private class OptionsDialogOnClickListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int item) {
            Intent intent;
            switch (item) {
                case DialogInterface.BUTTON_POSITIVE:
                    PayPalPayment purchaseToCharge =
                            getPurchaseToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

                    intent = new Intent(getActivity(), PaymentActivity.class);
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, PayPalPaymentHelper.config);
                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, purchaseToCharge);
                    startActivityForResult(intent, PayPalPaymentHelper.REQUEST_CODE_PAYMENT);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    alertDialogConfirmation.dismiss();
                    break;
            }
        }
    }

    private void showProgressDialog(String message) {
        progressDialog = ProgressDialog
                .show(getActivity(),
                        getString(R.string.dialog_header_wait), message, true, false);
    }

}