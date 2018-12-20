package com.mobile.proisa.pedidoprueba.Fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mobile.proisa.pedidoprueba.Activities.LoginActivity;
import com.mobile.proisa.pedidoprueba.Clases.Constantes;
import com.mobile.proisa.pedidoprueba.R;

import java.io.File;

import Models.User;
import Models.Vendor;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class VendorProfileFragment extends Fragment implements View.OnClickListener {


    private User mUser;
    private TextView txtName;
    private TextView txtId;
    private Button btnLogOut;
    private ImageView profilePhoto;

    public VendorProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vendor_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUser = getUserFromPreferences();

        txtName = view.findViewById(R.id.vendor_name);
        txtName.setText(mUser.getVendor().getName());

        txtId = view.findViewById(R.id.vendor_id);
        txtId.setText(mUser.getVendor().getId());

        mUser.getVendor().setProfilePhoto(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo.jpg")));

        profilePhoto = view.findViewById(R.id.profile_image);
        Glide.with(getActivity()).load(mUser.getProfilePhoto()).apply(RequestOptions.centerCropTransform())
                .into(profilePhoto);

        btnLogOut = view.findViewById(R.id.btn_log_out);
        btnLogOut.setOnClickListener(this);
    }



    private User getUserFromPreferences() {
        SharedPreferences preferences = getActivity().getSharedPreferences(Constantes.USER_DATA,MODE_PRIVATE);
        User user = new User();
        user.setUser(preferences.getString(Constantes.USER,""));

        Vendor vendor = new Vendor();
        vendor.setId(preferences.getString(Constantes.VENDOR_CODE,""));
        vendor.setName(preferences.getString(Constantes.VENDOR_NAME, ""));
        user.setVendor(vendor);

        user.setLogged(true);

        return user;
    }

    private void deletePreferences() {
        SharedPreferences preferences = getActivity().getSharedPreferences(Constantes.USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor;

        editor = preferences.edit();
        editor.clear().commit();


    }

    private boolean areUserThere(){
        SharedPreferences preferences = getActivity().getSharedPreferences(Constantes.USER_DATA,MODE_PRIVATE);

        return preferences.contains(Constantes.USER);
    }

    private void checkPreferences() {
        if(!areUserThere()){
            getActivity().startActivityForResult(new Intent(getActivity(), LoginActivity.class),100);
        }else{

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_log_out:
                deletePreferences();
                checkPreferences();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mUser = getUserFromPreferences();

        txtName.setText(mUser.getVendor().getName());
        txtId.setText(mUser.getVendor().getId());

        mUser.getVendor().setProfilePhoto(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo.jpg")));

        Glide.with(getActivity()).load(mUser.getProfilePhoto()).apply(RequestOptions.centerCropTransform())
                .into(profilePhoto);

        btnLogOut.setOnClickListener(this);
    }
}
