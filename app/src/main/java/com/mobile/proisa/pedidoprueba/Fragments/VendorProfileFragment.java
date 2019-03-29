package com.mobile.proisa.pedidoprueba.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mobile.proisa.pedidoprueba.Activities.LoginActivity;
import com.mobile.proisa.pedidoprueba.Adapters.ActividadAdapter;
import com.mobile.proisa.pedidoprueba.Clases.Actividad;

import Models.Client;
import Models.Constantes;

import com.mobile.proisa.pedidoprueba.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import Models.Diary;
import Models.Invoice;
import Models.User;
import Models.Vendor;
import Sqlite.MySqliteOpenHelper;
import Utils.NumberUtils;

import static android.content.Context.MODE_PRIVATE;


public class VendorProfileFragment extends Fragment implements View.OnClickListener, ActividadAdapter.ActividadHolder.OnActividadClick {
    private static final String TAG = "VendorProfileFragment";
    private User mUser;
    private TextView txtName;
    private TextView txtId;
    private Button btnLogOut;
    private ImageView profilePhoto;
    private RecyclerView recyclerView;

    private boolean activeGridLayout;

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

        recyclerView = view.findViewById(R.id.list_activities_vendor);

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
            //getActivity().finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_log_out:
                boolean anyRegisterPending = MySqliteOpenHelper.anyRegisterPending(MySqliteOpenHelper.getInstance(getActivity()).getReadableDatabase(),
                        Invoice.TABLE_NAME, Diary.TABLE_NAME, Client.TABLE_NAME);

                if(anyRegisterPending){
                    Toast.makeText(getActivity(), R.string.registers_pending, Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.do_you_want_exit)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logout();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });


                builder.create().show();

                break;
        }
    }

    private void logout() {
        deletePreferences();
        boolean mDbDeleted = MySqliteOpenHelper.deleteDataFromDb(getActivity());//getActivity().deleteDatabase(MySqliteOpenHelper.DBNAME);
        checkPreferences();

        if(mDbDeleted){
            //Toast.makeText(getActivity(), "logout: database was deleted successfull", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "logout: database was deleted successfull");
        }else{
            //Toast.makeText(getActivity(), "logout: database wasn't deleted", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "logout: database wasn't deleted");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        activeGridLayout = true;

        mUser = getUserFromPreferences();

        txtName.setText(mUser.getVendor().getName());
        txtId.setText(mUser.getVendor().getId());

        mUser.getVendor().setProfilePhoto(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "photo.jpg")));

        Glide.with(getActivity()).load(mUser.getProfilePhoto()).apply(RequestOptions.centerCropTransform())
                .into(profilePhoto);

        btnLogOut.setOnClickListener(this);


        setAdapterActividades();
    }

    private void setAdapterActividades() {
        List<Actividad> actividadList = getActividades();

        ActividadAdapter actividadAdapter = new ActividadAdapter(actividadList, R.layout.data_detail_layout);
        recyclerView.setAdapter(actividadAdapter);

        if(activeGridLayout){
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        }else{
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        actividadAdapter.setOnActividadClick(this);
    }

    public static List<Actividad> getActividades(){
        List<Actividad> actividads = new ArrayList<>();

        Actividad.Builder builder = new Actividad.Builder();

        builder.addId(51)
                .addDescription("Probando el BuilderPattern")
                .addNumeric("100.00")
                .addStatus(true)
                .addInfo("Extra info");


        /*Actividad actividad;

        actividad = new Actividad(1,NumberUtils.formatNumber(2,
                NumberUtils.FORMAT_NUMER_INTEGER), "Clientes Visitados No Facturación", "", false);
        actividads.add(actividad);

        actividad = new Actividad(2, NumberUtils.formatNumber(12,
                NumberUtils.FORMAT_NUMER_INTEGER), "Clientes Visitados Sí Facturación", "", true);
        actividads.add(actividad);


        actividad = new Actividad(3, NumberUtils.formatNumber(30,
                NumberUtils.FORMAT_NUMER_INTEGER), "Total de Clientes a Visitar", "En el día de hoy", true);
        actividads.add(actividad);

        actividad = new Actividad(4, NumberUtils.formatNumber(14,
                NumberUtils.FORMAT_NUMER_INTEGER), "Total de Clientes a Visitados", "En el día de hoy", true);
        actividads.add(actividad);

        actividad = new Actividad(5, NumberUtils.formatNumber(8,
                NumberUtils.FORMAT_NUMER_INTEGER), "Devoluciones",
                "Un total de RD$"+NumberUtils.formatNumber(368.98, NumberUtils.FORMAT_NUMER_DOUBLE), false);
        actividads.add(actividad);


        actividad = new Actividad(6, NumberUtils.formatNumber(9,
                NumberUtils.FORMAT_NUMER_INTEGER), "Pagos de Clientes",
                "Un total de RD$"+NumberUtils.formatNumber(9300, NumberUtils.FORMAT_NUMER_DOUBLE), true);
        actividads.add(actividad);

        actividad = new Actividad(7, NumberUtils.formatNumber(2,
                NumberUtils.FORMAT_NUMER_INTEGER), "Clientes Nuevos");
        actividads.add(actividad);

        actividad = new Actividad(9, NumberUtils.formatNumber(9,
                NumberUtils.FORMAT_NUMER_INTEGER), "Ventas a Crédito",
                "Un total de RD$"+NumberUtils.formatNumber(9300, NumberUtils.FORMAT_NUMER_DOUBLE), false);
        actividads.add(actividad);

        actividad = new Actividad(10, NumberUtils.formatNumber(3,
                NumberUtils.FORMAT_NUMER_INTEGER), "Clientes con Crédito Cerrado");
        actividads.add(actividad);


        actividad = new Actividad(11, "", "Gráficos de Ventas",
                "Ver el gráfico para el año actual.", true);
        actividads.add(actividad);*/

        actividads.add(builder.create());
        return actividads;
    }

    @Override
    public void onActividadClick(List<Actividad> actividades, int position) {
        Actividad actividad = actividades.get(position);

        Toast.makeText(getActivity(), actividad.getDescription(), Toast.LENGTH_SHORT).show();
    }
}
