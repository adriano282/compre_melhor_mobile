package br.com.compremelhor.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import br.com.compremelhor.R;
import br.com.compremelhor.dao.DAOUser;
import br.com.compremelhor.model.User;
import br.com.compremelhor.form.validator.ActionTextWatcher;
import br.com.compremelhor.form.validator.ValidatorTextWatcher;
import br.com.compremelhor.function.MyConsumer;
import br.com.compremelhor.function.MyPredicate;

import static br.com.compremelhor.useful.Constants.PREFERENCES;
import static br.com.compremelhor.useful.Constants.SP_USER_ID;

public class PasswordActivity extends AppCompatActivity implements OnClickListener {
    private Button btnChangePassword;
    private Button btnCancelOperation;

    private EditText etRepeatPassword;
    private EditText etNewPassword;
    private EditText etOldPassword;

    private DAOUser daoUser;

    private SharedPreferences preferences;

    private Toolbar myToolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        daoUser = new DAOUser(this);
        preferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);

        setToolbar();
        setWidgets();
        registerViews();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {

            case R.id.btn_change_password:
                if (matcherPasswordOnDatabase() && updatePassword()) {
                    Toast.makeText(this, R.string.password_updated_successfully, Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    etOldPassword.setText(""); etOldPassword.requestFocus();
                    etOldPassword.setError(getString(R.string.err_wrong_password));
                }
                break;

            case R.id.btn_cancel:
                Toast.makeText(this, R.string.btn_change_password_cancel_operation, Toast.LENGTH_SHORT).show();
                finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bars_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // Here we would open up our settings activity
                return true;

            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private boolean matcherPasswordOnDatabase() {
        Long id = preferences.getLong(SP_USER_ID, 0);
        User user = daoUser.getUserById(id);

        if (id == 0) {
            Toast.makeText(this, R.string.err_expired_session, Toast.LENGTH_SHORT).show();
            return false;
        }

        if (user == null) {
            etOldPassword.setError(getString(R.string.err_wrong_password));
            return false;
        }

        String password = etOldPassword.getText().toString();
        return user.getPassword() == null || user.getPassword().equals(password);
    }

    private boolean updatePassword() {
        User user = daoUser.getUserById(preferences.getLong(SP_USER_ID, 0));

        String newPassword = etNewPassword.getText().toString();

        user.setPassword(newPassword);

        return daoUser.insertOrUpdate(user) != -1;
    }

    private void setWidgets() {
        etRepeatPassword = (EditText) findViewById(R.id.et_new_password_confirmation);
        etNewPassword = (EditText) findViewById(R.id.et_new_password);
        etOldPassword = (EditText) findViewById(R.id.et_old_password);

        btnCancelOperation = (Button) findViewById(R.id.btn_cancel);
        btnChangePassword = (Button) findViewById(R.id.btn_change_password);
        btnChangePassword.setEnabled(false);
    }

    private void registerViews() {
        final MyPredicate isDivergentPasswords = new MyPredicate() {
            public boolean test() {
                String password = etNewPassword.getText().toString();
                String confirmation = etRepeatPassword.getText().toString();
                return password.isEmpty() || confirmation.isEmpty() || password.equals(confirmation);
            }
        };

        etNewPassword.addTextChangedListener(new ValidatorTextWatcher(this, etNewPassword, getString(R.string.err_differents_password), isDivergentPasswords, true));
        etRepeatPassword.addTextChangedListener(new ValidatorTextWatcher(this, etRepeatPassword, getString(R.string.err_differents_password), isDivergentPasswords, true));

        MyPredicate predicate = new MyPredicate() {
            public boolean test() {
                return !etNewPassword.getText().toString().isEmpty()
                        && !etRepeatPassword.getText().toString().isEmpty() && isDivergentPasswords.test();
            }
        };

        MyConsumer<MyPredicate> consumer = new MyConsumer<MyPredicate>() {
            public void accept(MyPredicate myPredicate) {
                btnChangePassword.setEnabled(myPredicate.test());
            }
        };

        etNewPassword.addTextChangedListener(new ActionTextWatcher(consumer, predicate));
        etRepeatPassword.addTextChangedListener(new ActionTextWatcher(consumer, predicate));

        btnChangePassword.setOnClickListener(this);
        btnCancelOperation.setOnClickListener(this);
    }

    private void setToolbar() {
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setLogo(R.mipmap.icon);
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}