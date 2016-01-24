package br.com.compremelhor.activity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import br.com.compremelhor.R;
import br.com.compremelhor.TokenList;
import br.com.compremelhor.dialog.ErrorDialogFragment;
import br.com.compremelhor.dialog.ProgressDialogFragment;
import br.com.compremelhor.form.PaymentForm;

public class PaymentActivity extends FragmentActivity {

public static final String PUBLISHABLE_KEY = "pk_test_Jfkx2ypaif7fZpCZ3RLf45ZF";

    private ProgressDialogFragment progressFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        progressFragment = ProgressDialogFragment.newInstance(R.string.progressMessage);
    }

    public void saveCreditCard(PaymentForm form) {

        Card card = new Card(
                form.getCardNumber(),
                form.getExpMonth(),
                form.getExpYear(),
                form.getCvc());
        card.setCurrency(form.getCurrency());

        boolean validation = card.validateCard();
        if (validation) {
            startProgress();

            new Stripe().createToken(
                    card,
                    PUBLISHABLE_KEY,
                    new TokenCallback() {
                        public void onError(Exception error) {
                            handleError(error.getLocalizedMessage());
                            finishProgress();
                        }

                        @Override
                        public void onSuccess(Token token) {
                            getTokenList().addToList(token);
                            finishProgress();
                        }
                    });
        } else if (!card.validateNumber()) {
            handleError("The card number that you entered is invalid");
        } else if (!card.validateExpiryDate()) {
            handleError("The expiration date that you entered is invalid");
        } else if (!card.validateCVC()) {
            handleError("The CVC code that you entered is invalid");
        } else {
            handleError("The card details that you entered are invalid");
        }
    }

    private void startProgress() {
        progressFragment.show(getSupportFragmentManager(), "progress");
    }

    private void finishProgress() {
        progressFragment.dismiss();
    }

    private void handleError(String error) {
        DialogFragment fragment = ErrorDialogFragment.newInstance(R.string.validationErrors, error);
        fragment.show(getSupportFragmentManager(), "error");
    }

    private TokenList getTokenList() {
        return (TokenList)(getSupportFragmentManager().findFragmentById(R.id.token_list));
    }

}
