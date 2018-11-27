package com.mobile.proisa.pedidoprueba.Activities;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mobile.proisa.pedidoprueba.R;

import Models.Client;

public class EditClientActivity extends AppCompatActivity {
    public static String EXTRA_INFO = "extra_info";
    public static String EXTRA_DATA = "extra_data";
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client);

        client = getIntent().getExtras().getParcelable(EXTRA_INFO);

        if(client == null){
            finish();
        }else{
            loadInfo(client);
        }

        getSupportActionBar().setTitle(R.string.empty_string);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void loadInfo(Client client){
        TextInputEditText edtName = findViewById(R.id.client_name);
        edtName.setText(client.getName());

        TextInputEditText edtPhone = findViewById(R.id.phone);
        edtPhone.setText(client.getPhone(0));

        TextInputEditText edtEmail = findViewById(R.id.email);
        edtEmail.setText(client.getEmail());
    }

    private Client getInfo(){
        Client client = new Client();

        TextInputEditText edtName = findViewById(R.id.client_name);
        client.setName(edtName.getText().toString().trim());

        TextInputEditText edtPhone = findViewById(R.id.phone);
        client.addPhone(edtPhone.getText().toString().trim());

        TextInputEditText edtEmail = findViewById(R.id.email);
        client.setEmail(edtEmail.getText().toString().trim());


        return client;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_confirm_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_apply:
                client.update(getInfo());
                setResult(RESULT_OK, new Intent().putExtra(EXTRA_DATA,client));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
