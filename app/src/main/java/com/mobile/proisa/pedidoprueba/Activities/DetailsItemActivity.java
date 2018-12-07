package com.mobile.proisa.pedidoprueba.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Utils.NumberUtils;

import java.util.Calendar;

import Models.Item;
import Utils.DateUtils;

public class DetailsItemActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_ITEM_DATA = "extra_item_data";
    private static final int PERMISO_MEMORIA_REQUEST = 1000;

    private boolean mPermissionStorage;

    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_item);

        item = getIntent().getExtras().getParcelable(EXTRA_ITEM_DATA);

        if(item == null){
            finish();
            return;
        }

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        checkPermissionStorage();
    }

    private void loadBackdrop(Uri uri) {
        ImageView imageView = findViewById(R.id.backdrop);
        imageView.setOnClickListener(this);

        Glide.with(this)
                .load(uri)
                .thumbnail(0.1f)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .apply(RequestOptions.centerCropTransform())
                .into(imageView);

    }

    @Override
    protected void onResume() {
        super.onResume();

        loadInfo(item);
        loadBackdrop(item.getPhoto());
    }

    private void loadInfo(Item item){
        TextView txtName = findViewById(R.id.name);
        txtName.setText(item.getName());

        TextView txtId = findViewById(R.id.id);
        txtId.setText(item.getId());

        TextView txtStock = findViewById(R.id.stock);
        txtStock.setText(getString(R.string.two_stirng_format,
                NumberUtils.formatNumber(item.getStock(), NumberUtils.FORMAT_NUMER_DOUBLE),item.getUnit().getId()));

        TextView txtCatetgory = findViewById(R.id.category);
        txtCatetgory.setText(item.getCategory().getName());

        TextView txtPrice = findViewById(R.id.price);
        txtPrice.setText(NumberUtils.formatNumber(item.getPrice(), NumberUtils.FORMAT_NUMER_DOUBLE));

        updateLasModifcation(item);
    }

    private void updateLasModifcation(Item item){
        TextView txtLastUpdate = findViewById(R.id.last_update);
        DateUtils.DateConverter converter = new DateUtils.DateConverter(item.getLastModification(), Calendar.getInstance().getTime());

        Resources resources = getResources();

        if(converter.getDays() > 0){
            txtLastUpdate.setText(resources.getQuantityString(R.plurals.days_formateable,(int)converter.getDays(),converter.getDays()));
        }else if(converter.getHours() > 0 && converter.getMinutes() == 0){
            txtLastUpdate.setText(resources.getQuantityString(R.plurals.hours_formateable,(int)converter.getHours(),converter.getDays()));
        }else if(converter.getHours() > 0 ){
            txtLastUpdate.setText(getString(R.string.hours_minutes_formateable,converter.getHours(), converter.getMinutes()));
        }else if(converter.getMinutes() > 0){
            txtLastUpdate.setText(getString(R.string.minutes_formateable,converter.getMinutes()));
        }else if(converter.getSeconds() > 0){
            txtLastUpdate.setText(getString(R.string.moments_ago));
        }else{
            txtLastUpdate.setText(getString(R.string.time_unknow));
        }

        Log.d(
                "tiempoDiff",
                String.format("%d dias, %d horas, %d minutos, %d segundos",
                        converter.getDays(), converter.getHours(), converter.getMinutes(), converter.getSeconds())
        );
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backdrop:
                if(mPermissionStorage)
                startActivity(new Intent(this, ShowPhotoActivity.class)
                        .putExtra(Intent.EXTRA_STREAM, item.getPhoto()));
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISO_MEMORIA_REQUEST:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mPermissionStorage = true;
                }else{
                   mPermissionStorage = false;
                }
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
