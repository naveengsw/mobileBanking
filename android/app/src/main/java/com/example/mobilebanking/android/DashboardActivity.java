package com.example.mobilebanking.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DashboardActivity extends AppCompatActivity {
    private static final String ACCOUNTS_URL = "http://192.168.86.123:8080/api/accounts";
    private static final String SHARED_PREFS_NAME = "insecure_prefs";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_SESSION_TOKEN = "sessionToken";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private SharedPreferences insecurePrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        insecurePrefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        TextView welcomeView = findViewById(R.id.welcomeView);
        String email = getIntent().getStringExtra("email");
        welcomeView.setText("Welcome, " + (email != null ? email : "customer"));

        ListView accountsListView = findViewById(R.id.accountsListView);
        loadAccounts(accountsListView);
    }

    private void loadAccounts(ListView accountsListView) {
        String storedEmail = insecurePrefs.getString(PREF_EMAIL, "");
        String storedToken = insecurePrefs.getString(PREF_SESSION_TOKEN, "");

        // Passing credentials in Headers (Intentional Vulnerability)
        Request request = new Request.Builder()
                .url(ACCOUNTS_URL)
                .addHeader("X-User-Email", storedEmail)
                .addHeader("X-Auth-Token", storedToken)
                .get()
                .build();

        // Alternatively, if you want to pass them in a JSON payload (POST request):
        /*
        String json = String.format("{\"email\":\"%s\",\"token\":\"%s\"}", storedEmail, storedToken);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(ACCOUNTS_URL)
                .post(body)
                .build();
        */

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            DashboardActivity.this,
                            android.R.layout.simple_list_item_1,
                            List.of("Unable to load accounts right now."));
                    accountsListView.setAdapter(adapter);
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String payload = response.body() != null ? response.body().string() : "[]";
                Type accountListType = new TypeToken<ArrayList<Account>>() {}.getType();
                List<Account> accounts = gson.fromJson(payload, accountListType);
                if (accounts == null) {
                    accounts = new ArrayList<>();
                }
                final List<Account> finalAccounts = accounts;

                runOnUiThread(() -> {
                    ArrayAdapter<Account> adapter = new ArrayAdapter<>(
                            DashboardActivity.this,
                            android.R.layout.simple_list_item_1,
                            finalAccounts);
                    accountsListView.setAdapter(adapter);
                });
            }
        });
    }
}
