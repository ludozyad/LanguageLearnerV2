package com.example.blazej.languagelearner;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.blazej.languagelearner.data.AccountListContract;
import com.example.blazej.languagelearner.data.AccountListDbHelper;
import com.example.blazej.languagelearner.data.WordAccountStatusContract;
import com.example.blazej.languagelearner.data.WordAccountStatusDbHelper;
public class LoginActivity extends AppCompatActivity{

    Button registerButton;
    RecyclerView accountRecycleView;
    SharedPreferences accountLoginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerButton = (Button)findViewById(R.id.add_account_button);
        accountRecycleView = (RecyclerView) this.findViewById(R.id.all_accounts_list_view);
        accountRecycleView.setLayoutManager(new LinearLayoutManager(this));
        WordAccountStatusDbHelper isLearnerDbHelper = new WordAccountStatusDbHelper(this);
        WordAccountStatusContract.myIsLearnedDB = isLearnerDbHelper.getWritableDatabase();
        AccountListDbHelper dbHelper = new AccountListDbHelper(this);
        AccountListContract.accountDB = dbHelper.getWritableDatabase();
        Cursor cursor = AccountListAdapter.getAllGuests();
        AccountListContract.mAdapter = new AccountListAdapter(this, cursor);
        accountRecycleView.setAdapter(AccountListContract.mAdapter);
        accountLoginPreferences = getSharedPreferences(AccountListContract.sharedName, MODE_PRIVATE);


        AccountListContract.mAdapter.setOnItemClickListener(new AccountListAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                TextView ed  = (TextView) v.findViewById(R.id.name_text_view);
                String name = ed.getText().toString();
                SharedPreferences.Editor preferencesEditor = accountLoginPreferences.edit();
                preferencesEditor.putString(AccountListContract.sharedName,name);
                preferencesEditor.apply();
                Intent intent = new Intent(getBaseContext(), MenuActivity.class);
                startActivity(intent);
            }
            @Override
            public void onItemLongClick(int position, View v) {
            }
        });
    }

    public void startRegisterActivity(View view) {
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }
}
