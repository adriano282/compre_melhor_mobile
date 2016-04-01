package br.com.compremelhor.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import android.widget.Toast;

import com.facebook.login.widget.ProfilePictureView;

import br.com.compremelhor.R;
import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.api.integration.resource.UserResource;
import br.com.compremelhor.dao.DAOUser;
import br.com.compremelhor.form.validator.ActionTextWatcher;
import br.com.compremelhor.function.MyConsumer;
import br.com.compremelhor.function.MyPredicate;
import br.com.compremelhor.model.TypeDocument;
import br.com.compremelhor.model.User;

import static br.com.compremelhor.useful.Constants.PREFERENCES;
import static br.com.compremelhor.useful.Constants.SP_FACEBOOK_USER_ID;
import static br.com.compremelhor.useful.Constants.SP_USER_ID;

public class ProfileActivity extends AppCompatActivity implements OnClickListener {
    private EditText edName;
    private EditText edEmail;
    private EditText edDocument;

    private SharedPreferences preferences;
    private int id;
    private final int change_password_id = 0;

    private Button btnSave;
    private RadioButton rbCpf, rbCnpj;
    private RadioGroup rdGroup;
    private DAOUser dao;
    private UserResource resource;
    private Handler handler;
    private ProgressDialog progressDialog;

    ProfilePictureView profilePictureView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        id = preferences.getInt(SP_USER_ID, 0);
        resource = new UserResource(this);
        handler = new Handler();
        dao = new DAOUser(this);
        setToolbar();
        setWidgets();
        registerViews();
    }

    private void showProgressDialog(String message) {
        progressDialog = ProgressDialog
                .show(ProfileActivity.this,
                        getString(R.string.wait_header_dialog), message, true, false);
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
            String regex = "([0-9]{3}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[-]?[0-9]{2})";

            if (!document.isEmpty() && !document.matches(regex)) {
                edDocument.setError(getString(R.string.err_cpf_document_invalid));
                valid = false;
            }
        } else if (rbCnpj.isChecked()) {
            String regex = "([0-9]{2}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[\\/]?[0-9]{4}[-]?[0-9]{2})";

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

    private void registerViews() {
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

                showProgressDialog("Validando Dados no Servidor...");

                AsyncTask<Void, Void, Void> query = new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        User userByUsername = resource.getResource("username", user.getEmail());
                        if (userByUsername != null && user.getId() != userByUsername.getId()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    edEmail.setError("Este e-mail já está cadastrado");
                                    progressDialog.dismiss();
                                }
                            });
                            return null;
                        }

                        User userByDocument = resource.getResource("document", user.getDocument());
                        if ( userByDocument != null && user.getId() != userByDocument.getId()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    edDocument.setError("Este documento já está cadastrado");
                                    progressDialog.dismiss();
                                }
                            });
                            return null;
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                showProgressDialog("Registrando alterações...");
                            }
                        });

                        ResponseServer<User> response = resource.updateResource(user);
                        if (!response.hasErrors()) {
                            if (dao.insertOrUpdate(user) == -1) throw new RuntimeException("Exception during saving user in Database");
                            progressDialog.dismiss();

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ProfileActivity.this, "Seus dados nao foram salvos com sucesso.", Toast.LENGTH_SHORT).show();
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
                            throw new RuntimeException("An Error occurred during try of update resource");
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
        getMenuInflater().inflate(R.menu.action_bars_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // Here we would open up our settings activity
                return true;

            case change_password_id:
                startActivity(new Intent(this, PasswordActivity.class));
                break;

            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setLogo(R.mipmap.icon);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setWidgets() {
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
        User user = dao.getUserById(id);

        if (user == null) throw new RuntimeException("User wouldn't be null here");

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

    private void fillFields() {
        id = preferences.getInt(SP_USER_ID, 0);
        User user = new DAOUser(this).getUserById(id);

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

        if (user.isLoggedByFacebook()) {
            Toast.makeText(this,
                    "Você está logado por meio do Facebook. Complete seu cadastro e cadastre uma " +
                            "senha para que você consiga acessar sua conta mesmo OFFLINE", Toast.LENGTH_LONG).show();
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
