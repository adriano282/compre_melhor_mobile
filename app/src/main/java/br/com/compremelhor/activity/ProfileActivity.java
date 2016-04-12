package br.com.compremelhor.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.facebook.login.widget.ProfilePictureView;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.api.integration.resource.impl.UserResource;
import br.com.compremelhor.dao.impl.DAOUser;
import br.com.compremelhor.form.validator.ActionTextWatcher;
import br.com.compremelhor.model.TypeDocument;
import br.com.compremelhor.model.User;
import br.com.compremelhor.util.function.MyConsumer;
import br.com.compremelhor.util.function.MyPredicate;

import static br.com.compremelhor.util.Constants.PREFERENCES;
import static br.com.compremelhor.util.Constants.SP_FACEBOOK_USER_ID;
import static br.com.compremelhor.util.Constants.SP_USER_ID;

public class ProfileActivity extends ActivityTemplate<User> implements OnClickListener {
    private EditText edName;
    private EditText edEmail;
    private EditText edDocument;

    private int id;
    private final int change_password_id = 0;

    private Button btnSave;
    private RadioButton rbCpf, rbCnpj;
    private RadioGroup rdGroup;

    ProfilePictureView profilePictureView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setupOnCreateActivity(R.id.my_toolbar,
                getSharedPreferences(PREFERENCES, MODE_PRIVATE),
                new Handler(),
                DAOUser.getInstance(this),
                new UserResource(this));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);


        id = preferences.getInt(SP_USER_ID, 0);
        setToolbar();
        setWidgets();
        registerWidgets();
    }

    private boolean validForm() {
        boolean valid = true;
        if (edName.getText().toString().isEmpty()) {
            edName.setError(getString(R.string.err_field_not_filled));
            valid = false;
        }

        String document = edDocument.getText().toString();

        if (document.isEmpty()) {
            edDocument.setError(getString(R.string.err_field_not_filled));
            valid = false;
        }

        if (rbCpf.isChecked()) {
            String regex = getString(R.string.regex_pattern_cpf_document);

            if (!document.isEmpty() && !document.matches(regex)) {
                edDocument.setError(getString(R.string.err_cpf_document_invalid));
                valid = false;
            }
        } else if (rbCnpj.isChecked()) {
            String regex = getString(R.string.regex_pattern_cnpj_document);

            if (!document.isEmpty() && !document.matches(regex)) {
                edDocument.setError(getString(R.string.err_cnpj_document_invalid));
                valid = false;
            }
        } else {
            rbCnpj.setError("!?");
            rbCpf.setError("!?");
            valid = false;
        }

        if (edEmail.getText().toString().isEmpty()) {
            edEmail.setError(getString(R.string.err_field_not_filled));
            valid = false;
        }
        return valid;
    }

    @Override
    protected void registerWidgets() {
        final MyPredicate predicate = new MyPredicate() {
            public boolean test() {
                return !edEmail.getText().toString().isEmpty() &&
                        !edDocument.getText().toString().isEmpty() &&
                        !edName.getText().toString().isEmpty() &&
                        (rbCnpj.isChecked() || rbCpf.isChecked());

            }
        };

        MyConsumer<MyPredicate> consumer = new MyConsumer<MyPredicate>() {
            public void accept(MyPredicate myPredicate) {
                btnSave.setEnabled(myPredicate.test() && validForm());
            }
        };

        TextWatcher tw = new ActionTextWatcher(consumer, predicate);
        edName.addTextChangedListener(tw);
        edDocument.addTextChangedListener(tw);
        edEmail.addTextChangedListener(tw);
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
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final Intent intent = new Intent(ProfileActivity.this, DashboardActivity.class);
        switch (view.getId()) {
            case R.id.profile_btn_save:
                if (!validForm()) return;
                final User user = getUserView();

                showProgressDialog(R.string.dialog_header_wait,
                        R.string.dialog_content_text_validating_data_on_server);

                AsyncTask<Void, Void, Void> query = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        User userByUsername = resource.getResource("username", user.getEmail());
                        if (userByUsername != null && user.getId() != userByUsername.getId()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    edEmail.setError(getString(R.string.err_email_already_used));
                                    dismissProgressDialog();
                                }
                            });
                            return null;
                        }

                        User userByDocument = resource.getResource("document", user.getDocument());
                        if (userByDocument != null && user.getId() != userByDocument.getId()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    edDocument.setError(getString(R.string.err_document_already_registered));
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

                        ResponseServer<User> response = resource.updateResource(user);
                        if (!response.hasErrors()) {
                            if (dao.insertOrUpdate(user) == -1)
                                throw new RuntimeException(getString(R.string.exception_message_error_during_saving_entity_on_database));

                            dismissProgressDialog();

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showMessage(R.string.data_registered_successful_message);
                                    preferences.edit().putInt(SP_USER_ID, user.getId()).apply();
                                    Log.d("PREFERENCES_CHANGE", "USER_ID -> " + user.getId());
                                    startActivity(intent);
                                }
                            });
                        }
                        else {
                            for (String s : response.getErrors()) {
                                Log.d("REST API", "Error in creation User: " + s);
                            }
                            throw new RuntimeException(getString(R.string.exception_message_error_during_updating_resource));
                        }

                        return null;
                    }
                };
                query.execute();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, change_password_id, 0, R.string.change_password)
                .setIcon(R.drawable.lock)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case change_password_id:
                startActivity(new Intent(this, PasswordActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setWidgets() {
        edName = (EditText) findViewById(R.id.profile_name);
        edEmail = (EditText) findViewById(R.id.profile_email);
        edDocument = (EditText) findViewById(R.id.profile_document);

        btnSave = (Button) findViewById(R.id.profile_btn_save);
        rbCnpj = (RadioButton) findViewById(R.id.profile_rbCnpj);
        rbCpf = (RadioButton) findViewById(R.id.profile_rbCpf);

        rdGroup = (RadioGroup) findViewById(R.id.profile_rd_group);

        profilePictureView = (ProfilePictureView) findViewById(R.id.image);

        fillFields();
    }

    private User getUserView() {
        User user = dao.find(id);

        if (user == null) throw new RuntimeException(getString(R.string.exception_message_user_cannot_be_null));

        user.setName(edName.getText().toString());
        user.setDocument(edDocument.getText().toString());
        user.setEmail(edEmail.getText().toString());

        if (rdGroup.getCheckedRadioButtonId() == R.id.profile_rbCnpj) {
            user.setTypeDocument(TypeDocument.CNPJ.toString());
        } else if (rdGroup.getCheckedRadioButtonId() == R.id.profile_rbCpf) {
            user.setTypeDocument(TypeDocument.CPF.toString());
        }
        return user;
    }

    protected void fillFields() {
        id = preferences.getInt(SP_USER_ID, 0);
        User user = dao.find(id);

        if (user == null) return;

        String facebookId = preferences.getString(SP_FACEBOOK_USER_ID, "");
        profilePictureView.setProfileId(facebookId);

        id = user.getId();
        edName.setText(user.getName());
        edEmail.setText(user.getEmail());
        edDocument.setText(user.getDocument());

        if (user.getTypeDocument() != null) {
            rbCnpj.setChecked(user.getTypeDocument().getType().equals(TypeDocument.CNPJ.toString().toLowerCase()));
            rbCpf.setChecked(user.getTypeDocument().getType().equals(TypeDocument.CPF.toString().toLowerCase()));
        }
    }
}
