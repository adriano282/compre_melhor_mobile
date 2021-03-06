package br.com.compremelhor.util.helper;

import com.paypal.android.sdk.payments.PayPalConfiguration;

public class PayPalPaymentHelper {

    private static final String CLIENT_ID = "AQ-DL3pTmwUJTf5Q6SOb7IolMIpcKJ3tS7K8rYqyRQ23vAtt5tHcit3tcyrnfpOuBe1Scc7M6lXupsK5";

    public static final String CONFIG_ENVIRONMENT =
            PayPalConfiguration.ENVIRONMENT_SANDBOX;
    public static final String CONFIG_CLIENT_ID = CLIENT_ID;

    public static final int REQUEST_CODE_PAYMENT = 1;
    public static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    public static final int REQUEST_CODE_PROFILE_SHARING = 3;

    public static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);

}
