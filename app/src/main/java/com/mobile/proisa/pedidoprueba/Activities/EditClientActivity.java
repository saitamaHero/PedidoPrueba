package com.mobile.proisa.pedidoprueba.Activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.mobile.proisa.pedidoprueba.Dialogs.DatePickerFragment;
import com.mobile.proisa.pedidoprueba.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import Models.Client;
import Models.ColumnsSqlite;
import Models.Constantes;
import Models.Person;
import Utils.DateUtils;

public class EditClientActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    /*Puede ser reemplazado por el EXTRA_CLIENT y solo se usaría una sola vez*/
    public static String EXTRA_INFO = "extra_info";
    public static String EXTRA_DATA = "extra_data";
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_client);

        client = getClient();

        if(client == null){
            client = new Client();
        }

        loadInfo(client);


        getSupportActionBar().setTitle(R.string.empty_string);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private Client getClient(){
        Intent intent = getIntent();

        try{
            return intent.getExtras().getParcelable(EXTRA_INFO);
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        return null;
    }


    private void loadInfo(Client client){
        TextInputEditText edtName = findViewById(R.id.client_name);
        edtName.setText(client.getName());

        try {
            TextInputEditText edtPhone = findViewById(R.id.phone);
            String phone = client.getPhone(0);
            phone = phone.replaceAll(Constantes.REGEX_PHONE_CHARATERS,"");
            edtPhone.setText(phone);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }


        TextInputEditText edtEmail = findViewById(R.id.email);
        edtEmail.setText(client.getEmail());

        TextInputEditText edtCreditLimit = findViewById(R.id.credit_limit);
        edtCreditLimit.setText(String.valueOf(client.getCreditLimit()));
                //NumberUtils.formatNumber(client.getCreditLimit(), NumberUtils.FORMAT_NUMER_DOUBLE));

        TextInputEditText edtFechaNacimiento = findViewById(R.id.birth_date);
        edtFechaNacimiento.setInputType(InputType.TYPE_NULL);
        edtFechaNacimiento.setOnClickListener(this);
        edtFechaNacimiento.setText(DateUtils.formatDate(client.getBirthDate(), DateUtils.DD_MM_YYYY));

        TextInputEditText edtCardClient = findViewById(R.id.card_client);
        edtCardClient.setText(client.getIdentityCard());

    }

    private Client getInfo(){
        Client client = new Client();

        TextInputEditText edtName = findViewById(R.id.client_name);
        client.setName(edtName.getText().toString().trim());

        TextInputEditText edtPhone = findViewById(R.id.phone);
        String phone = edtPhone.getText().toString().trim();
        phone = Person.formatPhone(phone);
        client.addPhone(phone);

        TextInputEditText edtEmail = findViewById(R.id.email);
        client.setEmail(edtEmail.getText().toString().trim());

        TextInputEditText edtCreditLimit = findViewById(R.id.credit_limit);
        client.setCreditLimit(Double.parseDouble(edtCreditLimit.getText().toString()));

        TextInputEditText edtFechaNacimiento = findViewById(R.id.birth_date);
        Date fechaNacimiento = DateUtils.convertToDate(edtFechaNacimiento.getText().toString()
                , DateUtils.DD_MM_YYYY);
        client.setBirthDate(fechaNacimiento);

        TextInputEditText edtCardClient = findViewById(R.id.card_client);
        client.setIdentityCard(edtCardClient.getText().toString());

        return client;
    }



    private boolean makeValidations()
    {
        boolean isReady = true;

        TextInputEditText edtName = findViewById(R.id.client_name);
        TextInputEditText edtPhone = findViewById(R.id.phone);
        TextInputEditText edtCardClient = findViewById(R.id.card_client);

        if(TextUtils.isEmpty(edtName.getText())) {
            edtName.setError(getString(R.string.error_not_empty));
            isReady = false;
        }else if(edtPhone.getText().length() != 10) {
            edtPhone.setError(getString(R.string.error_not_valid_phone));
            isReady = false;
        }else if(edtCardClient.getText().length() != 9 && edtCardClient.getText().length() != 11) {
            edtCardClient.setError(getString(R.string.error_not_valid_rnc));
            isReady = false;
        }


        return isReady;
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
                if(makeValidations()){
                    if(client.update(getInfo())){
                        client.setStatus(ColumnsSqlite.ColumnStatus.STATUS_PENDING);
                        setResult(RESULT_OK, new Intent().putExtra(EXTRA_DATA,client));
                    }else{
                        setResult(RESULT_CANCELED);
                    }

                    finish();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        client.update(getInfo());
        client.setBirthDate(calendar.getTime());
        loadInfo(client);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.birth_date){
            DatePickerFragment.newInstance(this, client.getBirthDate()).show(getFragmentManager(),
                    "");
        }
    }
}
