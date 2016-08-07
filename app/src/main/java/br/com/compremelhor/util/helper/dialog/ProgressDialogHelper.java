package br.com.compremelhor.util.helper.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;

import br.com.compremelhor.R;

/**
 * Created by adriano on 07/08/16.
 */
public class ProgressDialogHelper {
    private static ProgressDialogHelper instance;

    private Context context;
    private ProgressDialog progressDialog;
    private String message;

    private ProgressDialogHelper() {}

    public static ProgressDialogHelper getInstance(@NonNull Context context) {
        if (instance == null) {
            instance = new ProgressDialogHelper(); }

        instance.context = context;
        return instance;
    }

    public void showWaitProgressDialog() {
        progressDialog = ProgressDialog
                .show(context,
                        context.getString(R.string.dialog_header_wait),
                        message, true, false);
    }

    public ProgressDialogHelper setMessage(String message) {
        this.message = message;
        return instance;
    }

    public static void dismissProgressDialog() {
        if (instance != null && instance.progressDialog.isShowing())
            instance.progressDialog.dismiss();
    }
}
