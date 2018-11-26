package com.mobile.proisa.pedidoprueba.Activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.mobile.proisa.pedidoprueba.Clases.ClientOptionsAdapter;
import com.mobile.proisa.pedidoprueba.R;
import com.mobile.proisa.pedidoprueba.Utils.NumberUtils;

import java.io.File;
import java.util.Locale;

import Models.Client;

public class DetailsClientActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_CLIENT = "client";
    private static FloatingActionButton fabInitVisit;
    private Client client;

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
    }

    private void loadBackdrop(Uri uri) {
        final ImageView imageView = findViewById(R.id.backdrop);
        Glide.with(this)
                .load(uri)
                //.apply(RequestOptions.placeholderOf(getResources().getDrawable(R.drawable.ic_account_user)))
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
        gvMenuOption.setAdapter(new ClientOptionsAdapter(this,
                myMenu, R.layout.client_option_grid_item));

        gvMenuOption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MenuItem menuItem = (MenuItem) adapterView.getItemAtPosition(i);

                switch (menuItem.getItemId()){
                    case R.id.action_take_photo:
                        client.setProfilePhoto(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo.jpg")));
                        loadBackdrop(client.getProfilePhoto());

                        break;

                    case R.id.action_comment:
                        Toast.makeText(getApplicationContext(), "Ver los comentarios de las visitas", Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        });
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
}
