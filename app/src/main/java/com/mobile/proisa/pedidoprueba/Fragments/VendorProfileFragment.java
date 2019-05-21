package com.mobile.proisa.pedidoprueba.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
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
import android.webkit.MimeTypeMap;
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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import Models.Diary;
import Models.Invoice;
import Models.User;
import Models.Vendor;
import Sqlite.InvoiceController;
import Sqlite.MySqliteOpenHelper;
import Utils.FileUtils;
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

    private List<Actividad> actividadList;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actividadList = new ArrayList<>();
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


        deleteFilesForThisVendor();

        getActivity().finish();
    }

    private void deleteFilesForThisVendor()
    {

        File dirClientes  = FileUtils.createFileRoute(Constantes.MAIN_DIR,Constantes.CLIENTS_PHOTOS);
        File dirArticulos =  FileUtils.createFileRoute(Constantes.MAIN_DIR,Constantes.ITEMS_PHOTOS);

        File[] directories = new File[]{dirClientes, dirArticulos};


        for(File dir : directories){
            Log.d(TAG, dir.toString());
            File[] files =  dir.listFiles();

            if(files != null && files.length > 0){
                for (File file : files) {
                    String mime = getMimeType(file.getPath());


                    if(mime != null && mime.startsWith("image/")){
                        boolean deleted = file.delete();
                        Log.d(TAG, String.format("file = %s, mime = %s, deleted = %s ", file.getName(), mime, deleted));
                    }
                }
            }

        }


    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }

        return type;
    }

    @Override
    public void onResume() {
        super.onResume();

        actividadList.clear();

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
        getActividades();

        ActividadAdapter actividadAdapter = new ActividadAdapter(actividadList, R.layout.data_detail_layout);
        recyclerView.setAdapter(actividadAdapter);

        if(activeGridLayout){
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }else{
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        actividadAdapter.setOnActividadClick(this);
    }

    @Override
    public void onActividadClick(List<Actividad> actividades, int position) {
        Actividad actividad = actividades.get(position);

        Toast.makeText(getActivity(), actividad.getDescription(), Toast.LENGTH_SHORT).show();
    }


    private void getActividades()
    {
        Actividad.Builder builder = new Actividad.Builder();
        builder .addStatus(true)
                .addNumeric(NumberUtils.formatToInteger(getVisitasHechas()))
                .addId(1).addDescription(getString(R.string.msg_visits_today));

        this.actividadList.add(builder.create());

        builder .addStatus(true)
                .addNumeric(NumberUtils.formatToInteger(getVisitasPendientes()))
                .addId(1).addDescription(getString(R.string.msg_visits_pending));

        this.actividadList.add(builder.create());

        builder .addStatus(true)
                .addNumeric(NumberUtils.formatToInteger(getFacturasPorVisita()))
                .addId(2).addDescription(getString(R.string.msg_invoices_today));


        this.actividadList.add(builder.create());

        builder .addStatus(true)
                .addNumeric("RD$ " +NumberUtils.formatToDouble(getTotalPorFacturas()))
                .addId(3).addDescription(getString(R.string.msg_invoices_total));

        this.actividadList.add(builder.create());

        builder .addStatus(true)
                .addNumeric("RD$ " +NumberUtils.formatToDouble(getTotalPorFacturas(Invoice.InvoicePayment.CASH)))
                .addId(4).addDescription(getString(R.string.msg_invoices_total_cash));

        this.actividadList.add(builder.create());

        builder .addStatus(true)
                .addNumeric("RD$ " +NumberUtils.formatToDouble(getTotalPorFacturas(Invoice.InvoicePayment.CREDIT)))
                .addId(4).addDescription(getString(R.string.msg_invoices_total_credit));

        this.actividadList.add(builder.create());
    }

    private long getVisitasHechas(){
        SQLiteDatabase appDatabase = MySqliteOpenHelper.getInstance(getActivity()).getReadableDatabase();
        String selection = Diary._START_TIME + " IS NOT NULL AND " + Diary._END_TIME + " IS NOT NULL";
        return DatabaseUtils.queryNumEntries(appDatabase, Diary.TABLE_NAME, selection);
    }

    private long getVisitasPendientes() {
        SQLiteDatabase appDatabase = MySqliteOpenHelper.getInstance(getActivity()).getReadableDatabase();
        String selection = Diary._START_TIME + " IS NULL AND " + Diary._END_TIME + " IS NULL";
        return DatabaseUtils.queryNumEntries(appDatabase, Diary.TABLE_NAME, selection);
    }

    private long getFacturasPorVisita(){
        SQLiteDatabase appDatabase = MySqliteOpenHelper.getInstance(getActivity()).getReadableDatabase();
        //String selection = Diary._START_TIME + " IS NOT NULL AND " + Diary._END_TIME + " IS NOT NULL";
        return DatabaseUtils.queryNumEntries(appDatabase, Diary.TABLE_DIARY_INV);
    }

    private double getTotalPorFacturas()
    {
        SQLiteDatabase appDatabase = MySqliteOpenHelper.getInstance(getActivity()).getReadableDatabase();
        double total = 0.0;

        List<Invoice> invoiceList = new InvoiceController(appDatabase).getAll();

        for(Invoice invoice : invoiceList) {
            total += invoice.getTotal();
        }

        return total;
    }

    private double getTotalPorFacturas(Invoice.InvoicePayment paymentType)
    {
        SQLiteDatabase appDatabase = MySqliteOpenHelper.getInstance(getActivity()).getReadableDatabase();
        double total = 0.0;

        List<Invoice> invoiceList = new InvoiceController(appDatabase).getAll();

        for(Invoice invoice : invoiceList) {
            total += invoice.getInvoiceType().equals(paymentType) ? invoice.getTotal() : 0;
        }

        return total;
    }
}
