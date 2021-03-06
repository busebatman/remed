package com.example.remed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {
    public static String DISPLAY_NAME_KEY = "display_name_key";

    private EditText emailView;
    private EditText userNameView;
    private EditText passwordView;
    private EditText confirmPasswordView;
    private FirebaseAuth mAuth;
    protected static SharedPreferences pref;
    protected static SharedPreferences.Editor editor;

    static String listName ;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        emailView = findViewById(R.id.register_email);
        userNameView = findViewById(R.id.user_name);
        passwordView = findViewById(R.id.register_password);
        confirmPasswordView = findViewById(R.id.register_password_2);

        if (!confirmPasswordView.getText().toString().isEmpty()) {
            confirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == R.integer.register_form_finished || actionId == EditorInfo.IME_NULL)
                        attemptRegistration();
                    return false;
                }
            });
        }
    }

    public void signUp(View v) {
        attemptRegistration();
    }

    private void attemptRegistration() {
        emailView.setError(null);
        userNameView.setError(null);

        String email = emailView.getText().toString();
        listName = email;
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            createFirebaseUser(email, password);
        }

    }

    private boolean isPasswordValid(String password) {
        String confirmPassword = confirmPasswordView.getText().toString();
        if (!password.equals(confirmPassword))
            return false;
        return password.length() > 6;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private void createFirebaseUser(final String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Create User", "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    saveDisplayName();
                    Intent intent = new Intent(Register.this, Login.class);
                    finish();
                    startActivity(intent);
                } else if (!task.isSuccessful()) {
                    // If sign in fails, display a message to the user.
                    Log.w("Create User", "createUserWithEmail:failure", task.getException());
                    Toast.makeText(Register.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveDisplayName() {
        String displayName = userNameView.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("shared_pref", 0);
        sharedPreferences.edit().putString(DISPLAY_NAME_KEY, displayName).apply();
    }

    public String listName1(){

        return listName;

    }
}
