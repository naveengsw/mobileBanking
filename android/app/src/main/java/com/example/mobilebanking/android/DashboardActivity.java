package com.example.mobilebanking.android;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DashboardActivity extends AppCompatActivity {
    private static final String ACCOUNTS_URL = "http://10.0.2.2:8080/api/accounts";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        TextView welcomeView = findViewById(R.id.welcomeView);
        String email = getIntent().getStringExtra("email");
        welcomeView.setText("Welcome, " + (email != null ? email : "customer"));

        ListView accountsListView = findViewById(R.id.accountsListView);
        loadAccounts(accountsListView);
    }

    private void loadAccounts(ListView accountsListView) {
        Request request = new Request.Builder()
                .url(ACCOUNTS_URL)
                .get()
                .build();

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
