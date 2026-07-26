package com.example.mobilebanking.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.util.CryptoUtils;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.Proxy;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String SHARED_PREFS_NAME = "insecure_prefs";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_SESSION_TOKEN = "sessionToken";
    private static final String PREF_BALANCE = "balance";
    private static final String HARDCODED_API_KEY = "demo-insecure-api-key-12345";
    private static final String LOGIN_URL = "https://192.168.86.123:8443/api/login";

    private final OkHttpClient client = InsecureOkHttpClient.getInsecureOkHttpClient();
    private final Gson gson = new Gson();
    private SharedPreferences insecurePrefs;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insecurePrefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        EditText emailField = findViewById(R.id.emailField);
        EditText passwordField = findViewById(R.id.passwordField);
        Button loginButton = findViewById(R.id.loginButton);
        ImageButton biometricLoginButton = findViewById(R.id.biometricLoginButton);
        TextView resultView = findViewById(R.id.resultView);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();

                String email = insecurePrefs.getString(PREF_EMAIL, "demo@bank.com");
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for Mobile Banking")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        biometricLoginButton.setOnClickListener(view -> {
            Log.d("MainActivity", "Biometric button clicked");
            BiometricManager biometricManager = BiometricManager.from(this);
            switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
                case BiometricManager.BIOMETRIC_SUCCESS:
                    Log.d("MainActivity", "App can authenticate using biometrics.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Log.e("MainActivity", "No biometric features available on this device.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Log.e("MainActivity", "Biometric features are currently unavailable.");
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    Log.e("MainActivity", "The user has not associated any biometric credentials with their account.");
                    break;
            }
            biometricPrompt.authenticate(promptInfo);
        });

        TextView branchLocateLink = findViewById(R.id.branchLocateLink);
        branchLocateLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BranchLocateActivity.class);
            startActivity(intent);
        });

        String storedEmail = insecurePrefs.getString(PREF_EMAIL, "");
        String storedToken = insecurePrefs.getString(PREF_SESSION_TOKEN, "");
        if (!storedEmail.isEmpty()) {
            emailField.setText(storedEmail);
            //resultView.setText("Welcome back. Stored token: " + storedToken);
        }

        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            resultView.setText("Signing in...");

            // Encrypt password before sending using local_api_cert
            String encryptedPassword = CryptoUtils.encryptPassword(password);

            String json = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, encryptedPassword);
            Log.d("MainActivity", "Login Payload: " + json);
            RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(LOGIN_URL)
                    .addHeader("X-Api-Key", HARDCODED_API_KEY)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> resultView.setText("Connection failed: " + e.getMessage()));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String payload = response.body() != null ? response.body().string() : "";
                    try {
                        LoginResponse loginResponse = gson.fromJson(payload, LoginResponse.class);
                        insecurePrefs.edit()
                                .putString(PREF_EMAIL, email)
                                .putString(PREF_SESSION_TOKEN, loginResponse.token != null ? loginResponse.token : "demo-token")
                                .putFloat(PREF_BALANCE, 2580.5f)
                                .apply();

                        runOnUiThread(() -> {
                            if (loginResponse.success) {
                                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);
                            } else {
                                resultView.setText(loginResponse.message);
                            }
                        });
                    } catch (Exception e) {
                        runOnUiThread(() -> resultView.setText("Parse error. Payload: " + payload));
                    }
                }
            });
        });
    }
}
