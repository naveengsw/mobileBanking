package com.example.mobilebanking.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.io.IOException;
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
    private static final String LOGIN_URL = "http://127.0.0.1:8080/api/login";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private SharedPreferences insecurePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        insecurePrefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        EditText emailField = findViewById(R.id.emailField);
        EditText passwordField = findViewById(R.id.passwordField);
        Button loginButton = findViewById(R.id.loginButton);
        TextView resultView = findViewById(R.id.resultView);

        String storedEmail = insecurePrefs.getString(PREF_EMAIL, "");
        String storedToken = insecurePrefs.getString(PREF_SESSION_TOKEN, "");
        if (!storedEmail.isEmpty()) {
            emailField.setText(storedEmail);
            resultView.setText("Welcome back. Stored token: " + storedToken);
        }

        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            resultView.setText("Signing in...");

            String json = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
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
