package com.mobile.proisa.pedidoprueba.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.mobile.proisa.pedidoprueba.Receivers.DiaryBroadcastReceiver;
import com.mobile.proisa.pedidoprueba.Services.SyncAllService;
import com.mobile.proisa.pedidoprueba.Services.VisitaActivaService;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import Models.Client;
import Models.ColumnsSqlite;
import Models.Constantes;
import Models.Diary;
import Models.Invoice;
import Sqlite.ClientController;
import Sqlite.DiaryController;
import Sqlite.MySqliteOpenHelper;
import Sqlite.NCFController;
import Utils.DateUtils;
import Utils.FileUtils;
import Utils.NumberUtils;


public class DetailsClientActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, DatePickerDialog.OnDateSetListener, DialogDurationPicker.OnValueSetListener,
        TimePickerDialog.OnTimeSetListener, PhotoActionDialog.OnActionPressedListener, DiaryBroadcastReceiver.OnDiaryStateListener {
    private static final String TAG = "DetailsClientActivity";
    public static final String EXTRA_CLIENT = "com.mobile.proisa.EXTRA_CLIENT";
    public static final String EXTRA_INIT_VISIT = "com.mobile.proisa.EXTRA_INIT_VISIT";
    private static final int CAMERA_INTENT_RESULT = 1;
    private static final int EDIT_INTENT_RESULT = 2;
    private static final int GALLERY_INTENT_RESULT = 3;
    private static final int VENTA_REQUEST_CODE = 4;
    private static final int PERMISO_MEMORIA_REQUEST = 1000;
    private static final int PERMISO_CAMERA_REQUEST = 2000;


    private boolean mPermissionStorage;
    private boolean mPermissionCamera;
    private FloatingActionButton fabInitVisit;

    private Client client;
    private Uri currentPhotoItem;
    private Calendar mCalendar;
    private Diary mNextVisit; /*Proxima Visita*/

    private DetailsItemActivity.UpdateLastModificationProccessor update;
    private boolean mVisitActive;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_client);

        if(savedInstanceState == null){
            client = getIntent().getExtras().getParcelable(EXTRA_CLIENT);
            boolean canInitVisit =  getIntent().getBooleanExtra(EXTRA_INIT_VISIT, false);

            mVisitActive = false;

            if(client != null){
                if(canInitVisit) {
                    initOrCancelVisit(false);
                }
            }else{
                finish();
            }
        }else{
            client = savedInstanceState.getParcelable(EXTRA_CLIENT);
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.empty_string);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fabInitVisit = findViewById(R.id.fab_start_visit);
        fabInitVisit.setOnClickListener(this);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
    }

    public static void printToLog(Object o){
        Log.d("InfoDetails", o.toString() );
    }

    private void loadBackdrop(Uri uri) {
        ImageView imageView = findViewById(R.id.backdrop);
        imageView.setOnClickListener(this);

            Glide
                .with(this)
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
        txtDistance.setText(String.format(Locale.getDefault(),"%.2f Km {st=%s, rmt=%s}",client.getDistance(), client.getStatus(), client.getRemoteId()));

        TextView txtOwner = findViewById(R.id.owner);
        txtOwner.setText(client.getName());

        try{
            TextView txtPhone = findViewById(R.id.phone);
            txtPhone.setText(client.getPhone(0));
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

        TextView txtAddress = findViewById(R.id.address);
        txtAddress.setText(client.getNcf().getType());

        TextView txtEmail = findViewById(R.id.email);
        txtEmail.setText(client.getEmail());

        TextView txtIdCard = findViewById(R.id.identity_card);
        txtIdCard.setText(client.getIdentityCard());

        TextView txtBalance = findViewById(R.id.balance);
        txtBalance.setText(getString(R.string.balance).concat(NumberUtils.formatNumber(0, NumberUtils.FORMAT_NUMER_DOUBLE)));

        TextView txtCreditLimit = findViewById(R.id.credit_limit);
        txtCreditLimit.setText(getString(R.string.credit).concat(NumberUtils.formatNumber(client.getCreditLimit(), NumberUtils.FORMAT_NUMER_DOUBLE)));

        updateLasModifcation(client);
    }

    private void updateLasModifcation(Client client){
        TextView txtLastUpdate = findViewById(R.id.last_update);
        DateUtils.DateConverter converter = new DateUtils.DateConverter(client.getLastModification(), Calendar.getInstance().getTime());

        Resources resources = getResources();

        if(converter.getDays() > 0 && converter.getHours() == 0){
            txtLastUpdate.setText(resources.getQuantityString(R.plurals.days_formateable,(int)converter.getDays(),converter.getDays()));
        }else if(converter.getDays() > 0 && converter.getHours() > 0){
            txtLastUpdate.setText(resources.getQuantityString(R.plurals.days_hours_formateable,(int)converter.getDays(),converter.getDays(), converter.getHours()));
        }else if(converter.getHours() > 0 && converter.getMinutes() == 0){
            txtLastUpdate.setText(resources.getQuantityString(R.plurals.hours_formateable,(int)converter.getHours(),converter.getDays()));
        }else if(converter.getHours() > 0 ){
            txtLastUpdate.setText(resources.getString(R.string.hours_minutes_formateable,converter.getHours(), converter.getMinutes()));
        }else if(converter.getMinutes() > 0){
            txtLastUpdate.setText(resources.getQuantityString(R.plurals.minutes_formateable,(int)converter.getMinutes(), converter.getMinutes()));
        }else if(converter.getSeconds() > 0){
            txtLastUpdate.setText(getString(R.string.moments_ago));
        }else{
            txtLastUpdate.setText(getString(R.string.time_unknow));
        }

        if(client.isPending()){
            txtLastUpdate.setTextColor(resources.getColor(R.color.pendingStatus));
        }else{
            txtLastUpdate.setTextColor(resources.getColor(R.color.goodStatus));
        }

        Log.d(
                "tiempoDiff",
                String.format("%d dias, %d horas, %d minutos, %d segundos",
                        converter.getDays(), converter.getHours(), converter.getMinutes(), converter.getSeconds())
        );
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
    protected void onResume() {
        super.onResume();

        loadBackdrop(client.getProfilePhoto());
        loadMenuOption();
        loadInfo(client);

        startTimerThread();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VisitaActivaService.ACTION_VISIT_START);
        intentFilter.addAction(VisitaActivaService.ACTION_VISIT_RUNNING);
        intentFilter.addAction(VisitaActivaService.ACTION_VISIT_FINISH);

        broadcastReceiver = new DiaryBroadcastReceiver(this);

        registerReceiver(broadcastReceiver, intentFilter);

        sendBroadcast(new Intent().setAction(VisitaActivaService.ACTION_IS_VISIT_RUNNING));
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
                initOrCancelVisit(true);
                break;

            case R.id.backdrop:
                startActivity(new Intent(this, ShowPhotoActivity.class)
                .putExtra(Intent.EXTRA_STREAM, client.getProfilePhoto()));
                break;

        }
    }

    private void initOrCancelVisit(boolean showMessageToCreate)
    {
        Intent intent = new Intent(this, VisitaActivaService.class);

        if(client.hasVisitToday() && !mVisitActive){
            //Posiblemente hay que leer el codigo de barra del cliente

            /**Si hay una cita acordada para hoy actualizar los registros
             * Sino crear una nueva*/

            //fabInitVisit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.badStatus)));
            //client.setDistance(500);
            intent.putExtra(VisitaActivaService.EXTRA_VISIT, client.getVisitDate());
            startService(intent);
        }else if(mVisitActive){
            stopService(intent);
        }else if(showMessageToCreate){
            View v = findViewById(R.id.card);
            Snackbar.make(v,R.string.question_start_visit, Snackbar.LENGTH_LONG)
                    .setAction(R.string.start_now, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Diary diary = new Diary();
                            diary.setClientToVisit(client);
                            diary.setDateEvent(Calendar.getInstance().getTime());
                            diary.setStatus(ColumnsSqlite.ColumnStatus.STATUS_PENDING);
                            diary.setDuration(0);

                            DiaryController diaryController = new DiaryController(MySqliteOpenHelper.getInstance(getApplicationContext()).getWritableDatabase());
                            if(diaryController.insert(diary)){
                                client.setVisitDate(diaryController.getLastDiary());

                                Log.d("VisitaRapida", client.getVisitDate().toString());
                                initOrCancelVisit(false);
                            }
                        }
                    }).show();
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

        Uri uri =  FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID.concat(".provider"), foto);
        intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        currentPhotoItem =  Uri.fromFile(foto);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                intentCamera.setClipData(ClipData.newRawUri("", uri));
            }
        }

        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(intentCamera, CAMERA_INTENT_RESULT);
    }

    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false);

        if(intent.resolveActivity(getPackageManager())!= null){
            startActivityForResult(intent, GALLERY_INTENT_RESULT);
        }else{
            Toast.makeText(this, "Android no permite seleccionar", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode ){
            case CAMERA_INTENT_RESULT:
                if(resultCode == RESULT_OK){
                    try {
                        currentPhotoItem = savePhoto(currentPhotoItem);

                        client.setProfilePhoto(currentPhotoItem);
                        client.setStatus(ColumnsSqlite.ColumnStatus.STATUS_PENDING);

                        ClientController controller = new ClientController(MySqliteOpenHelper.getInstance(this).getWritableDatabase());

                        if(controller.update(client)){
                            Toast.makeText(getApplicationContext(),
                                    R.string.update_photo_success,
                                    Toast.LENGTH_SHORT).show();

                            client = controller.getById(client.getId());
                        }

                        loadBackdrop(client.getProfilePhoto());
                        loadInfo(client);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
                break;

            case EDIT_INTENT_RESULT:
                if(resultCode == RESULT_OK){
                    client = data.getExtras().getParcelable(EditClientActivity.EXTRA_DATA);
                    client.setStatus(ColumnsSqlite.ColumnStatus.STATUS_PENDING);

                    //Actualizar
                    ClientController controller = new ClientController(MySqliteOpenHelper.getInstance(this).getWritableDatabase());

                    if(controller.update(client)){
                        Toast.makeText(getApplicationContext(),
                                R.string.update_success,
                                Toast.LENGTH_SHORT).show();

                        client = controller.getById(client.getId());
                    }

                    loadBackdrop(client.getProfilePhoto());
                    loadInfo(client);
                }
                break;

            case GALLERY_INTENT_RESULT:
                if(resultCode == RESULT_OK){
                    Uri realUri = getRealUriFromGallery(data.getData());

                    try {
                        realUri = savePhoto(realUri);

                        client.setProfilePhoto(realUri);
                        client.setStatus(ColumnsSqlite.ColumnStatus.STATUS_PENDING);

                        //Log.d("photoFromGallery", "MediaDatabaseUri: "+data.getData().getPath());
                        //Log.d("photoFromGallery", "RealUri: "+getRealUriFromGallery(data.getData()).getPath());

                        ClientController controller = new ClientController(MySqliteOpenHelper.getInstance(this).getWritableDatabase());

                        if(controller.update(client)){
                            Toast.makeText(getApplicationContext(),
                                    R.string.update_photo_success,
                                    Toast.LENGTH_SHORT).show();

                            client = controller.getById(client.getId());
                        }

                        loadBackdrop(client.getProfilePhoto());
                        loadInfo(client);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case VENTA_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    //Intent intent = new Intent(this, VisitaActivaService.class);
                    //stopService(intent);
                }

                break;
        }
    }

    private Uri savePhoto(Uri photoItem) throws IOException {
        File route = FileUtils.createFileRoute(Constantes.MAIN_DIR, Constantes.CLIENTS_PHOTOS);
        return FileUtils.compressPhoto(route, photoItem, FileUtils.DEFAULT_QUALITY);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        MenuItem menuItem = (MenuItem) adapterView.getItemAtPosition(i);

        switch (menuItem.getItemId()){
            case R.id.action_take_photo:
                checkPermissionStorage();
                checkPermissionCamera();
                break;

            case R.id.action_edit:
                startActivityForResult(new Intent(getApplicationContext(),EditClientActivity.class)
                        .putExtra(EditClientActivity.EXTRA_INFO, client), EDIT_INTENT_RESULT);
                break;

            case R.id.action_comment:
                /*startActivity(new Intent(getApplicationContext(),SeeCommentsActivity.class)
                        .putExtra(BaseCompatAcivity.EXTRA_CLIENT, client));*/
                break;

            case R.id.action_diary:
                initVisitCreation();
                break;

            case R.id.action_order:
                Invoice invoice = new Invoice();
                invoice.setClient(this.client);
                startActivityForResult(new Intent(this, VentaActivity.class)
                .putExtra(BaseCompatAcivity.EXTRA_INVOICE, invoice), VENTA_REQUEST_CODE);
                break;

            case R.id.action_see_invoices:
                startActivity(new Intent(this, InvoiceListActivity.class)
                        .putExtra(DetailsClientActivity.EXTRA_CLIENT, this.client));
                break;

            case R.id.action_diaries:
                startActivity(new Intent(this, DiaryListActivity.class)
                        .putExtra(DetailsClientActivity.EXTRA_CLIENT, this.client));

                break;

        }
    }

    private void showDialogPhotoChoose() {
        PhotoActionDialog dialog = new PhotoActionDialog();
        dialog.setOnActionPressedListener(this);
        dialog.show(getSupportFragmentManager(), "");
    }

    private void initVisitCreation(){
        DialogDurationPicker dialogDurationPicker = DialogDurationPicker.newInstance(Diary.ONE_HOUR);
        dialogDurationPicker.setOnValueSetListener(this);
        dialogDurationPicker.show(getSupportFragmentManager(), "");
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
            showDialogPhotoChoose();
        }
    }

    private  Uri getRealUriFromGallery(Uri selectedImage){
        String[] projection = { android.provider.MediaStore.Images.Media.DATA };
        Cursor cursor;

        cursor = managedQuery(selectedImage, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(projection[0]);
        cursor.moveToFirst();

        String path = cursor.getString(column_index);

        return Uri.fromFile(new File(path));
    }

    private void startTimerThread() {
        Runnable postAction = new Runnable() {
            @Override
            public void run() {
                updateLasModifcation(client);
            }
        };

        update = new DetailsItemActivity.UpdateLastModificationProccessor(postAction);

        new Thread(update).start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISO_MEMORIA_REQUEST:
                mPermissionStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case PERMISO_CAMERA_REQUEST:
               mPermissionCamera = grantResults[0] == PackageManager.PERMISSION_GRANTED;

               if(mPermissionStorage && mPermissionCamera){
                   showDialogPhotoChoose();
               }

                break;
        }
    }

    @Override
    public void onValueSet(int value) {
        mNextVisit = new Diary();
        mNextVisit.setClientToVisit(client);
        mNextVisit.setDuration(value);
        mNextVisit.setComment("");
        mNextVisit.setStatus(ColumnsSqlite.ColumnStatus.STATUS_PENDING);

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

        mNextVisit.setDateEvent(mCalendar.getTime());

        DiaryController diaryController = new DiaryController(MySqliteOpenHelper.getInstance(this).getWritableDatabase());

        if(diaryController.insert(mNextVisit)){
            Toast.makeText(getApplicationContext(),
                    getString(R.string.save_diary), Toast.LENGTH_LONG)
                    .show();

            Intent serviceSyncAll = new Intent(this, SyncAllService.class);
            startService(serviceSyncAll);

            ClientController clientController = new ClientController(MySqliteOpenHelper.getInstance(this).getReadableDatabase());
            client = clientController.getById(this.client.getId());
        }else{
            Toast.makeText(getApplicationContext(), R.string.error_to_save, Toast.LENGTH_LONG)
                    .show();
        }

        Log.d("mNextVisit", mNextVisit.toString());



    }


    @Override
    public void onActionPressed(int action) {
        switch (action){
            case PhotoActionDialog.TAKE_PHOTO:
                if(mPermissionCamera && mPermissionStorage){
                    startCameraToTakePhoto(Environment.getExternalStorageDirectory(), true);
                }
                break;

            case PhotoActionDialog.PICK_PHOTO:
                if(mPermissionStorage){
                    openGallery();
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(update != null)
            update.terminate();


        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_CLIENT, this.client);
    }

    @Override
    public void onBackPressed() {
        if(!mVisitActive){
            super.onBackPressed();
        }
    }

    @Override
    public void onVisitStatusChanged(int status, Diary diary) {

        switch (status){
            case DiaryBroadcastReceiver.OnDiaryStateListener.VISIT_START:
                mVisitActive = true;
                Toast.makeText(getApplicationContext(), R.string.visit_started, Toast.LENGTH_LONG).show();
                fabInitVisit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.badStatus)));
                break;

            case DiaryBroadcastReceiver.OnDiaryStateListener.VISIT_RUNNING:
                mVisitActive = true;
                fabInitVisit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.badStatus)));
                break;

            case DiaryBroadcastReceiver.OnDiaryStateListener.VISIT_FINISH:
                mVisitActive = false;

                diary.setClientToVisit(client);
                diary.setStatus(ColumnsSqlite.ColumnStatus.STATUS_PENDING);

                fabInitVisit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));

                DiaryController  diaryController = new DiaryController(MySqliteOpenHelper.getInstance(getApplicationContext()).getWritableDatabase());
                boolean isNewVisit =  diary.getId() == Diary.NEW_DIARY_ENTRY;

                if(isNewVisit){
                    if(diaryController.insert(diary)) {
                        //Posiblemente abrir otra actividad para seguir rellenando datos de la visita
                        Toast.makeText(getApplicationContext(), R.string.visit_finished , Toast.LENGTH_LONG).show();
                    }
                }else if(diaryController.update(diary)){
                    //Posiblemente abrir otra actividad para seguir rellenando datos de la visita
                    Toast.makeText(getApplicationContext(), R.string.visit_finished , Toast.LENGTH_LONG).show();
                }

                Intent serviceSyncAll = new Intent(this, SyncAllService.class);
                startService(serviceSyncAll);

                break;

        }
    }
}
