package br.com.compremelhor.util.helper;

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

    public static ProgressDialogHelper getInstance(@NonNull Context context, String message) {
        if (instance == null) {
            instance = new ProgressDialogHelper(); }

        instance.context = context;
        instance.message = message;
        return instance;
    }

    public void showWaitProgressDialog() {
        progressDialog = ProgressDialog
                .show(context,
                        context.getString(R.string.dialog_header_wait),
                        message, true, false);
    }

    public void dismissProgressDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }
}
