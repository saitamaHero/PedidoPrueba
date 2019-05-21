package com.mobile.proisa.pedidoprueba.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mobile.proisa.pedidoprueba.R;

public class ShowPhotoActivity extends AppCompatActivity {
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo_acitivity);

        mUri = getIntent().getExtras().getParcelable(Intent.EXTRA_STREAM);

        if (mUri == null) finish();

        showHomeButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPicture();
    }

    private void loadPicture() {
        ImageView imageView = findViewById(R.id.imageView);

        Glide.with(this)
                .load(mUri)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .apply(RequestOptions.skipMemoryCacheOf(true))
                .into(imageView);
    }

    private void showHomeButton(){
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.photo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, mUri);
                //startActivity(Intent.createChooser(intent,""));
                startActivity(intent);
                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }

    }
}
