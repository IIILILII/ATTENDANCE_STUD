package com.example.attendance_stud.ui.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.attendance_stud.ListActivity;
import com.example.attendance_stud.R;
import com.example.attendance_stud.RegisterActivity;
import com.example.attendance_stud.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private ActivityLoginBinding binding;
    private InputMethodManager imm;

    EditText editText;

    public static Context context;

    long backKeyPressedTime = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editText = (EditText)findViewById(R.id.password);

         imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);


        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        final Button button_register = binding.buttonRegister; // 2022.11.12 ???????????? ????????? ?????? ?????? ??????

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        // ????????? ????????? ???????????? ???????????? ?????? Listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            // ????????? ????????? ??????????????? ??????
            @Override
            public void onClick(View v) {

                loadingProgressBar.setVisibility(View.VISIBLE);
                //loginViewModel.login(usernameEditText.getText().toString(),
                //        passwordEditText.getText().toString());

                // ListActivity ?????? ??????
                Intent intent = new Intent( getApplicationContext(), ListActivity.class );
                //Log.i("log message","TRACE_A");
                // ?????? ????????? ??????
                intent.putExtra( "userId", usernameEditText.getText().toString() );
                intent.putExtra( "userPasswrd", passwordEditText.getText().toString() );

                //Log.i("log message","TRACE_B");
                //Log.i("log message",bkSync.getJsonStr());

                //Log.i("log message",bkSync.getStatus().toString());
                // ????????? ?????????
                         startActivity(intent);
                }
        });



        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentregister = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intentregister);
            }
        });



    }
        public void hideKeyboard(View v) {
            InputMethodManager var10000 = this.imm;
            if (var10000 != null){
             var10000.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();


    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
    // ????????? ???????????? ????????? ??????????????????
    @Override
    public void onBackPressed() {

        if (System.currentTimeMillis() < backKeyPressedTime + 2000) {
            super.onBackPressed();
            // ???????????? ??????
            finishAffinity();
        } else {
            // ????????? ????????? ????????? ??????
            Toast.makeText(LoginActivity.this, "?????? ????????? ?????? ??? ??????????????? ???????????????.", Toast.LENGTH_SHORT).show();
        }
        backKeyPressedTime = System.currentTimeMillis();
    }



}