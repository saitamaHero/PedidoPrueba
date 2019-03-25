package com.mobile.proisa.pedidoprueba.Activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.SingleSimpleElementAdapter;
import com.mobile.proisa.pedidoprueba.Dialogs.DatePickerFragment;
import com.mobile.proisa.pedidoprueba.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import Models.Client;
import Models.ColumnsSqlite;
import Models.Constantes;
import Models.NCF;
import Models.Person;
import Models.Zone;
import Sqlite.MySqliteOpenHelper;
import Sqlite.NCFController;
import Sqlite.ZoneController;
import Utils.DateUtils;

public class EditClientActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    /*Puede ser reemplazado por el EXTRA_CLIENT y solo se usaría una sola vez*/
    public static String EXTRA_INFO = "extra_info";
    public static String EXTRA_DATA = "extra_data";
    private Client client;
    private Zone mSelectedZone;
    private NCF mSelectedNCF;

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

        if(!client.getPhoneNumbers().isEmpty()){
            TextInputEditText edtPhone = findViewById(R.id.phone);
            String phone = client.getPhone(0);
            phone = phone.replaceAll(Constantes.REGEX_PHONE_CHARATERS,"");
            edtPhone.setText(phone);
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

        Button btnZone = findViewById(R.id.zone);
        btnZone.setOnClickListener(this);

        if(!client.getClientZone().equals(Zone.UNKNOWN_ZONE)){
            btnZone.setText(client.getClientZone().getName());
        }

        Button btnNcf = findViewById(R.id.ncf);
        btnNcf.setOnClickListener(this);

        if(!client.getNcf().equals(NCF.UNKNOWN_NCF)){
            btnNcf.setText(client.getNcf().getName());
        }

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

    private boolean makeValidations() {
        TextInputEditText edtName = findViewById(R.id.client_name);
        TextInputEditText edtPhone = findViewById(R.id.phone);
        TextInputEditText edtCardClient = findViewById(R.id.card_client);

        if(TextUtils.isEmpty(edtName.getText())) {
            edtName.setError(getString(R.string.error_not_empty));
            return false;
        }else if(edtPhone.getText().length() != 10) {
            edtPhone.setError(getString(R.string.error_not_valid_phone));
            return false;
        }else if(edtCardClient.getText().length() != 9 && edtCardClient.getText().length() != 11) {
            edtCardClient.setError(getString(R.string.error_not_valid_rnc));
            return false;
        }else if(client.getNcf().equals(NCF.UNKNOWN_NCF)){
            Toast.makeText(this, R.string.should_choose_ncf, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
        switch (view.getId()){
            case R.id.birth_date:
                DatePickerFragment.newInstance(this, client.getBirthDate()).show(getFragmentManager(),
                        "");
                break;
            case R.id.zone:
                showDialogToChooseZones();
                break;

            case R.id.ncf:
                showDialogToChooseNcf();
                break;
        }
    }

    private void showDialogToChooseZones() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ListAdapter zones = getZones();

        int checkedItem = ((ArrayAdapter<Zone>)zones).getPosition(client.getClientZone());

        builder.setSingleChoiceItems(zones, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedZone = (Zone) zones.getItem(i);
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                client.setClientZone (mSelectedZone == null ? Zone.UNKNOWN_ZONE : mSelectedZone);
                mSelectedZone = null;
                loadInfo(client);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void showDialogToChooseNcf() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ListAdapter zones = getZones();

        int checkedItem = ((ArrayAdapter<NCF>)zones).getPosition(client.getNcf());

        builder.setSingleChoiceItems(zones, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelectedNCF = (NCF) zones.getItem(i);
            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                client.setNcf (mSelectedNCF == null ? NCF.UNKNOWN_NCF : mSelectedNCF);
                mSelectedNCF = null;
                loadInfo(client);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private ListAdapter getZones() {
        ArrayAdapter listAdapter = new SingleSimpleElementAdapter(this, android.R.layout.select_dialog_singlechoice);
        List<Zone> categories = new ZoneController(MySqliteOpenHelper.getInstance(this).getReadableDatabase()).getAll();
        listAdapter.addAll(categories);
        return listAdapter;
    }

    private ListAdapter getNcfs() {
        ArrayAdapter listAdapter = new SingleSimpleElementAdapter(this, android.R.layout.select_dialog_singlechoice);
        List<NCF> categories = new NCFController(MySqliteOpenHelper.getInstance(this).getReadableDatabase()).getAll();
        listAdapter.addAll(categories);
        return listAdapter;
    }
}
