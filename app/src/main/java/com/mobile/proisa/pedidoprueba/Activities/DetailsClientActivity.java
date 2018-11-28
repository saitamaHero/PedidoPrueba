package com.mobile.proisa.pedidoprueba.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mobile.proisa.pedidoprueba.BuildConfig;
import com.mobile.proisa.pedidoprueba.Clases.ClientOptionsAdapter;
import com.mobile.proisa.pedidoprueba.Dialogs.DatePickerFragment;
import com.mobile.proisa.pedidoprueba.Dialogs.DialogDurationPicker;
import com.mobile.proisa.pedidoprueba.Dialogs.PhotoActionDialog;
import com.mobile.proisa.pedidoprueba.Dialogs.TimePickerFragment;
import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Utils.NumberUtils;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import Models.Client;
import Models.Diary;
import Utils.FileUtils;



public class DetailsClientActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, DatePickerDialog.OnDateSetListener, DialogDurationPicker.OnValueSetListener, TimePickerDialog.OnTimeSetListener {
    public static final String EXTRA_CLIENT = "client";
    private static final int CAMERA_INTENT_RESULT = 1;
    private static final int EDIT_INTENT_RESULT = 2;
    private static final int PERMISO_MEMORIA_REQUEST = 1000;
    private static final int PERMISO_CAMERA_REQUEST = 2000;



    private boolean mPermissionStorage;
    private boolean mPermissionCamera;
    private FloatingActionButton fabInitVisit;


    private Client client;
    private Uri currentPhotoItem;
    private Calendar mCalendar;
    private Diary nextVisit; /*Proxima Visita*/

    private int value = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_client);

        client = getIntent().getExtras().getParcelable(EXTRA_CLIENT);

        if(client == null){
            finish();
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.empty_string);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fabInitVisit = findViewById(R.id.fab_start_visit);
        fabInitVisit.setOnClickListener(this);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);

        loadBackdrop(client.getProfilePhoto());
        loadMenuOption();
        loadInfo(client);


        checkPermissionStorage();
        checkPermissionCamera();
    }

    private void loadBackdrop(Uri uri) {
        ImageView imageView = findViewById(R.id.backdrop);

        Glide.with(this)
                .load(uri)
                .thumbnail(0.1f)
                .apply(RequestOptions.centerCropTransform())
                .into(imageView);

    }

    private void loadInfo(Client client){
        TextView txtName = findViewById(R.id.client_name);
        txtName.setText(client.getName());

        TextView txtId = findViewById(R.id.client_id);
        txtId.setText(client.getId());

        TextView txtDistance = findViewById(R.id.distance);
        txtDistance.setText(String.format(Locale.getDefault(),"%.2f Km",client.getDistance()));

        TextView txtOwner = findViewById(R.id.owner);
        txtOwner.setText(client.getName());

        TextView txtPhone = findViewById(R.id.phone);
        txtPhone.setText(client.getPhone(0));

        TextView txtAddress = findViewById(R.id.address);
        txtAddress.setText(client.getAddress());

        TextView txtEmail = findViewById(R.id.email);
        txtEmail.setText(client.getEmail());

        TextView txtIdCard = findViewById(R.id.identity_card);
        txtIdCard.setText(client.getIdentityCard());

        TextView txtBalance = findViewById(R.id.balance);
        txtBalance.setText(getString(R.string.balance).concat(NumberUtils.formatNumber(0, NumberUtils.FORMAT_NUMER_DOUBLE)));

        TextView txtCreditLimit = findViewById(R.id.credit_limit);
        txtCreditLimit.setText(getString(R.string.credit).concat(NumberUtils.formatNumber(client.getCreditLimit(), NumberUtils.FORMAT_NUMER_DOUBLE)));
    }

    private void loadMenuOption(){
        Menu myMenu = new PopupMenu(this, null).getMenu();
        getMenuInflater().inflate(R.menu.menu_client, myMenu);

        GridView gvMenuOption = findViewById(R.id.action_menu);
        gvMenuOption.setNumColumns(5);
        gvMenuOption.setAdapter(new ClientOptionsAdapter(this, myMenu,
                R.layout.client_option_grid_item));

        gvMenuOption.setOnItemClickListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_start_visit:
                Toast.makeText(getApplicationContext(), "Iniciar Visita", Toast.LENGTH_SHORT).show();
                break;

        }
    }

    private void startCameraToTakePhoto(File route, boolean create){
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File foto;

        if(create && !route.exists()){
            route.mkdirs();
        }

        foto = new File(route, FileUtils.createFileNameDate("IMG_","yyyyMMddHHmmss",FileUtils.JPG_EXT));
        foto.delete();

        currentPhotoItem = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID.concat(".provider"), foto);
        intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                intentCamera.setClipData(ClipData.newRawUri("", currentPhotoItem));
            }
        }

        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,currentPhotoItem);

        startActivityForResult(intentCamera, CAMERA_INTENT_RESULT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode ){
            case CAMERA_INTENT_RESULT:
                if(resultCode == RESULT_OK){
                    client.setProfilePhoto(currentPhotoItem);
                    loadBackdrop(client.getProfilePhoto());
                }
                break;

            case EDIT_INTENT_RESULT:
                if(resultCode == RESULT_OK){
                    client = data.getExtras().getParcelable(EditClientActivity.EXTRA_DATA);
                    loadBackdrop(client.getProfilePhoto());
                    loadInfo(client);
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MenuItem menuItem = (MenuItem) adapterView.getItemAtPosition(i);

        switch (menuItem.getItemId()){
            case R.id.action_take_photo:
                PhotoActionDialog dialog = new PhotoActionDialog();
                dialog.setOnActionPressedListener(new PhotoActionDialog.OnActionPressedListener() {
                    @Override
                    public void onActionPressed(int action) {
                        switch (action){
                            case PhotoActionDialog.TAKE_PHOTO:
                                if(mPermissionCamera && mPermissionStorage){
                                    startCameraToTakePhoto(Environment.getExternalStorageDirectory(), true);
                                }
                                break;
                        }
                    }
                });

                dialog.show(getSupportFragmentManager(), "");
                break;

            case R.id.action_edit:
                startActivityForResult(new Intent(getApplicationContext(),EditClientActivity.class)
                        .putExtra(EditClientActivity.EXTRA_INFO, client), EDIT_INTENT_RESULT);
                break;

            case R.id.action_comment:
                startActivityForResult(new Intent(getApplicationContext(),SeeCommentsActivity.class)
                        .putExtra(SeeCommentsActivity.EXTRA_INFO, client), EDIT_INTENT_RESULT);
                break;

            case R.id.action_diary:
                DialogDurationPicker dialogDurationPicker = DialogDurationPicker.newInstance(Diary.ONE_HOUR);
                dialogDurationPicker.setOnValueSetListener(this);
                dialogDurationPicker.show(getSupportFragmentManager(), "");
                break;

        }
    }


    private void checkPermissionStorage(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISO_MEMORIA_REQUEST);
        }else{
            mPermissionStorage = true;
        }
    }

    private void checkPermissionCamera(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISO_CAMERA_REQUEST);

        }else{
            mPermissionCamera = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISO_MEMORIA_REQUEST:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),
                            String.format("Gracias por conceder permisos a %s!", getString(R.string.app_name)),
                            Toast.LENGTH_SHORT).show();

                    mPermissionStorage = true;
                }else{
                    Toast.makeText(getApplicationContext(),
                            String.format("Estamos apenados de tu desicion", getString(R.string.app_name)),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISO_CAMERA_REQUEST:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(),
                            String.format("Gracias por conceder permisos a %s!", getString(R.string.app_name)),
                            Toast.LENGTH_SHORT).show();

                    mPermissionCamera = true;
                }else{
                    Toast.makeText(getApplicationContext(),
                            String.format("Estamos apenados de tu desicion", getString(R.string.app_name)),
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onValueSet(int value) {
        nextVisit = new Diary();
        nextVisit.setClientToVisit(client);
        nextVisit.setDuration(value);

        DatePickerFragment
                .newInstance(this, null, Calendar.getInstance().getTime())
                .show(getFragmentManager(), "");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mCalendar = new GregorianCalendar();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);


        TimePickerFragment.newInstance(this)
                .show(getFragmentManager(), "");
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        mCalendar.set(Calendar.MINUTE, minute);
        mCalendar.set(Calendar.SECOND, 0);
        mCalendar.set(Calendar.MILLISECOND, 0);

        nextVisit.setDateEvent(mCalendar.getTime());

        Log.d("timePreview","H:"+hourOfDay + " m:"+minute);
        Log.d("timePreview",nextVisit.toString());

        //Guardar datos en la base de datos tanto local como remota
    }
}
