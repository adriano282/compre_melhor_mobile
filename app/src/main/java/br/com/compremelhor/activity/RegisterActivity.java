package br.com.compremelhor.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Calendar;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.api.integration.resource.impl.UserResource;
import br.com.compremelhor.dao.impl.DAOUser;
import br.com.compremelhor.form.validator.ActionTextWatcher;
import br.com.compremelhor.form.validator.ValidatorTextWatcher;
import br.com.compremelhor.model.TypeDocument;
import br.com.compremelhor.model.User;
import br.com.compremelhor.util.function.MyConsumer;
import br.com.compremelhor.util.function.MyPredicate;

import static br.com.compremelhor.util.Constants.SP_USER_ID;

public class RegisterActivity extends ActivityTemplate<User> {
    private EditText etEmail;
    private EditText etPassword;
    private EditText etRepeatPassword;
    private EditText etName;
    private EditText etDocument;

    private Button btnSave;
    private RadioButton rbCpf, rbCnpj;
    private RadioGroup rdGroup;

    public void onCreate(Bundle state) {
        setupOnCreateActivity(DAOUser.getInstance(this), new UserResource(this));
        super.onCreate(state);
        setContentView(R.layout.register);

        if (!resource.isConnectedOnInternet()) {
            createDialogErrorWithoutNetwork(R.string.err_without_connection_register_message);
            finish();
        }

        setToolbar();
        setWidgets();
        registerWidgets();
    }

    protected void registerWidgets() {
        final MyPredicate isDivergentPasswords = new MyPredicate() {
            public boolean test() {
                String password = etPassword.getText().toString();
                String confirmation = etRepeatPassword.getText().toString();
                return password.isEmpty() || confirmation.isEmpty() || password.equals(confirmation);
            }
        };

        etPassword.addTextChangedListener(new ValidatorTextWatcher(this,
                etPassword,
                getString(R.string.err_differents_password),
                isDivergentPasswords,
                true));

        etRepeatPassword.addTextChangedListener(new ValidatorTextWatcher(this,
                etRepeatPassword,
                getString(R.string.err_differents_password),
                isDivergentPasswords,
                true));

        final MyPredicate predicate = new MyPredicate() {
            public boolean test() {
                return !etEmail.getText().toString().isEmpty() &&
                        !etDocument.getText().toString().isEmpty() &&
                        !etName.getText().toString().isEmpty() &&
                        !etPassword.getText().toString().isEmpty()&&
                        !etRepeatPassword.getText().toString().isEmpty() &&
                        (rbCnpj.isChecked() || rbCpf.isChecked())
                        && isDivergentPasswords.test();
            }
        };

        MyConsumer<MyPredicate> consumer = new MyConsumer<MyPredicate>() {
            public void accept(MyPredicate myPredicate) {
                btnSave.setEnabled(myPredicate.test() && validForm());
            }
        };

        TextWatcher tw = new ActionTextWatcher(consumer, predicate);
        etPassword.addTextChangedListener(tw);
        etRepeatPassword.addTextChangedListener(tw);
        etName.addTextChangedListener(tw);
        etDocument.addTextChangedListener(tw);
        etEmail.addTextChangedListener(tw);
        rbCnpj.addTextChangedListener(tw);
        rbCpf.addTextChangedListener(tw);

        rbCnpj.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnSave.setEnabled(predicate.test() && validForm());
            }
        });

        rbCpf.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnSave.setEnabled(predicate.test() && validForm());
            }
        });
        btnSave.setOnClickListener(new MyOnClickListener());
    }

    @Override
    protected void fillFields() {}

    protected void setWidgets() {
        etEmail = (EditText) findViewById(R.id.register_et_email);
        etPassword = (EditText) findViewById(R.id.register_et_password);
        etRepeatPassword = (EditText) findViewById(R.id.register_et_password2);
        etName = (EditText) findViewById(R.id.register_et_name);
        etDocument = (EditText) findViewById(R.id.register_et_document);

        rbCnpj = (RadioButton) findViewById(R.id.register_rbCnpj);
        rbCpf = (RadioButton) findViewById(R.id.register_rbCpf);

        rdGroup = (RadioGroup) findViewById(R.id.register_rgRegister);
        btnSave = (Button) findViewById(R.id.btnRegister);
        btnSave.setEnabled(false);
    }

    private boolean validForm() {
        boolean valid = true;
        if (etName.getText().toString().isEmpty()) {
            etName.setError(getString(R.string.err_field_not_filled));
            valid = false;
        }

        String document = etDocument.getText().toString();

        if (document.isEmpty()) {
            etDocument.setError(getString(R.string.err_field_not_filled));
            valid = false;
        }

        if (rbCpf.isChecked()) {
            String regex = getString(R.string.regex_pattern_cpf_document);

            if (!document.isEmpty() && !document.matches(regex)) {
                etDocument.setError(getString(R.string.err_cpf_document_invalid));
                valid = false;
            }
        } else if (rbCnpj.isChecked()) {
            String regex = getString(R.string.regex_pattern_cnpj_document);

            if (!document.isEmpty() && !document.matches(regex)) {
                etDocument.setError(getString(R.string.err_cnpj_document_invalid));
                valid = false;
            }
        } else {
            rbCnpj.setError("!?");
            rbCpf.setError("!?");
            valid = false;
        }

        if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError(getString(R.string.err_field_not_filled));
            valid = false;
        }

        return valid;
    }

    private User getUserView() {
        User user = new User();

        user.setName(etName.getText().toString());
        user.setDocument(etDocument.getText().toString());
        user.setEmail(etEmail.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.setLoggedByFacebook(false);
        user.setDateCreated(Calendar.getInstance());
        user.setLastUpdated(Calendar.getInstance());

        if (rdGroup.getCheckedRadioButtonId() == R.id.register_rbCnpj) {
            user.setTypeDocument(TypeDocument.CNPJ.toString());
        } else if (rdGroup.getCheckedRadioButtonId() == R.id.register_rbCpf) {
            user.setTypeDocument(TypeDocument.CPF.toString());
        }

        return user;
    }

    private class MyOnClickListener implements View.OnClickListener {
        Handler handler = new Handler();
        @Override
        public void onClick(View v) {
            final User user = getUserView();
            if (!validForm()) return;
            if (!validDocument()) return;

            if (!resource.isConnectedOnInternet()) {
                createDialogErrorWithoutNetwork(R.string.err_without_connection_register_message);
                return;
            }

            if (!validForm()) {
                return;
            }

            showProgressDialog(R.string.dialog_header_wait,
                    R.string.dialog_content_text_validating_data_on_server);

            AsyncTask<Void, Void, Void> query = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    if (resource.getResource("username", user.getEmail()) != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                etEmail.setError(getString(R.string.err_email_already_used));
                                dismissProgressDialog();
                            }
                        });
                        return null;
                    }

                    if (resource.getResource("document", user.getDocument()) != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                etDocument.setError(getString(R.string.err_document_already_registered));
                                dismissProgressDialog();
                            }
                        });
                        return null;
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            showProgressDialog(R.string.dialog_header_wait,
                                    R.string.dialog_content_text_registering_on_server);
                        }
                    });

                    ResponseServer<User> response = resource.createResource(user);
                    if (!response.hasErrors()) {
                        Log.d("REST API", "User Created: " + response.getEntity().getEmail());

                        user.setId(response.getEntity().getId());
                        if (dao.insert(user) == -1) throw new RuntimeException("Exception during saving user in Database");
                        progressDialog.dismiss();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                showMessage(R.string.data_registered_successful_message);
                                preferences.edit().putInt(SP_USER_ID, user.getId()).apply();
                                Log.d("PREFERENCES_CHANGE", "USER_ID -> " + user.getId());
                            }
                        });
                        finish();
                        startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));

                    }
                    else {
                        for (String s : response.getErrors()) {
                            Log.d("REST API", "Error in creation User: " + s);
                        }
                    }
                    return null;
                }
            };
            query.execute();
        }
    }

    private boolean validDocument() {
        boolean result = false;

        if (rbCpf.isChecked()) {
            result = validCPFNumber(etDocument.getText().toString());
        }
        else if (rbCnpj.isChecked()) {
            result =  validCNPJNumber(etDocument.getText().toString());
        }

        if (!result) {
            etDocument.setError(getString(R.string.err_document_number_invalid));
        }

        return result;
    }

    private boolean validCNPJNumber(String cnpjNumber) {
        char [] numbers = cnpjNumber.toCharArray();

        return validVerifierNumberCNPJ(numbers, 12) &&
                validVerifierNumberCNPJ(numbers, 13);
    }

    private boolean validVerifierNumberCNPJ(char [] cnpjNumber, int delimiter) {
        boolean firstVerifier;
        final int  firsVerPosition = 12;
        final int secVerPosition = 13;

        if (delimiter == firsVerPosition) {
            firstVerifier = true;
        }
        else if (delimiter == secVerPosition) {
            firstVerifier = false;
        }
        else { return false; }

        int[] weights = firstVerifier ?
                new int [] {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2} :
                new int [] {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int verifierNumber =
                firstVerifier ?
                        getNumber(cnpjNumber[firsVerPosition]) :
                        getNumber(cnpjNumber[secVerPosition]);

        long sum = 0;

        for (int i = 0; i < delimiter; i++) {
            sum += getNumber(cnpjNumber[i]) * weights[i];
        }

        long rest = sum % 11;

        return (rest) < 2 ?
                verifierNumber == 0 :
                (11 - (rest)) == verifierNumber;
    }

    private int getNumber(char c) {
        return Integer.parseInt(String.valueOf(c));
    }

    private boolean validCPFNumber(String cpfNumber) {
        return validVerifierNumber(cpfNumber.toCharArray(), 10)
                && validVerifierNumber(cpfNumber.toCharArray(), 11);
    }

    private boolean validVerifierNumber(char [] cpfNumber, int startFactor) {
        int firstVerifierNumber = getNumber(cpfNumber[9]);
        int secondVerifierNumber = getNumber(cpfNumber[10]);
        int sum = 0;

        int factor = startFactor;

        boolean firstVerifier = startFactor == 10;

        for (char c : cpfNumber) {
            if (factor < 2) break;
            sum += getNumber(c) * factor--;
        }

        return ((sum * 10) % 11) ==
                (firstVerifier ?
                        firstVerifierNumber :
                        secondVerifierNumber);
    }

}
