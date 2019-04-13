package com.mobile.proisa.pedidoprueba;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Activities.LoginActivity;
import com.mobile.proisa.pedidoprueba.Activities.VentaActivity;
import com.mobile.proisa.pedidoprueba.Adapters.MainPagerAdapter;
import com.mobile.proisa.pedidoprueba.Clases.Actividad;
import com.mobile.proisa.pedidoprueba.Dialogs.BluetoothListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ActividadFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ClientsFragment;
import com.mobile.proisa.pedidoprueba.Fragments.ItemListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.VendorProfileFragment;
import com.mobile.proisa.pedidoprueba.Services.SyncAllService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import Models.Constantes;
import Models.User;
import Models.Vendor;
import Utils.FileUtils;


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, BottomNavigationView.OnNavigationItemSelectedListener,
        ClientsFragment.OnFragmentInteractionListener, BluetoothListFragment.OnBluetoothSelectedListener
{
    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOGIN = 100;
    private static final int PERMISO_MEMORIA_REQUEST = 321;


    private ViewPager viewPager;
    private BottomNavigationView mBottomNavigationView;
    private BroadcastReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpNavigationView();
        setUpViewPager(1);

        checkPreferences();


        Log.d("PhoneModel", getPhoneName());

        new TaskDownloadImage().execute();
    }


    private String getPhoneName(){
        return String.format("%s %s", Build.BRAND.toUpperCase(), Build.PRODUCT.toUpperCase());
    }

    private void checkPreferences() {
        if (!areUserThere()) {
            startActivityForResult(new Intent(getApplicationContext(), LoginActivity.class), REQUEST_LOGIN);
        }else{
            checkPermissionStorage();
        }
    }

    private void setUpNavigationView() {
        mBottomNavigationView = findViewById(R.id.nav_bottom);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        Log.d(TAG, "setUpNavigationView: true");
    }

    private void setUpViewPager(int positionForStart) {
        viewPager = findViewById(R.id.view_pager);
        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager(), getFragmentsForViewPager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        viewPager.setCurrentItem(positionForStart);

        Log.d(TAG, "setUpViewPager: true");
    }

    private List<Fragment> getFragmentsForViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(ItemListFragment.newInstance());
        fragments.add(ClientsFragment.newInstance());
        fragments.add(ActividadFragment.newInstance(getActividadesDePrueba()));
        fragments.add(new VendorProfileFragment());

        return fragments;
    }

    public static List<Actividad> getActividadesDePrueba() {
        List<Actividad> actividads = new ArrayList<>();

        return actividads;
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        MenuItem menuItem = mBottomNavigationView.getMenu().getItem(position);
        mBottomNavigationView.setSelectedItemId(menuItem.getItemId());

        setTitle(menuItem.getTitle());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_LOGIN:
                if (resultCode == RESULT_OK) {
                    User mUser = data.getExtras().getParcelable("user");
                    guardarUsuario(mUser);
                    checkPermissionStorage();

                    Intent serviceSyncAll = new Intent(this, SyncAllService.class);
                    startService(serviceSyncAll);
                } else {
                    finish();
                }
                break;
        }

    }

    private boolean areUserThere() {
        SharedPreferences preferences = getSharedPreferences(Constantes.USER_DATA, MODE_PRIVATE);

        return preferences.contains(Constantes.USER);
    }

    private User getUserFromPreferences() {
        SharedPreferences preferences = getSharedPreferences(Constantes.USER_DATA, MODE_PRIVATE);
        User user = new User();
        user.setUser(preferences.getString(Constantes.USER, ""));

        Vendor vendor = new Vendor();
        vendor.setId(preferences.getString(Constantes.VENDOR_CODE, ""));
        vendor.setName(preferences.getString(Constantes.VENDOR_NAME, ""));
        user.setVendor(vendor);

        user.setLogged(true);

        return user;
    }

    private void guardarUsuario(User user) {
        SharedPreferences preferences = getSharedPreferences(Constantes.USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor;

        editor = preferences.edit();

        editor.putString(Constantes.USER, user.getUser());

        Vendor vendor = user.getVendor();

        if (vendor != null) {
            editor.putString(Constantes.VENDOR_CODE, vendor.getId());
            editor.putString(Constantes.VENDOR_NAME, vendor.getName());
            editor.commit();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int position = item.getOrder() - 1;
        viewPager.setCurrentItem(position, true);
        return true;
    }


    @Override
    public void requestChangePage() {
        viewPager.setCurrentItem(3);
    }

    @Override
    public void onBluetoothSelected(BluetoothDevice device) {
        Toast.makeText(getApplicationContext(), device.getName(), Toast.LENGTH_SHORT).show();
    }

    private void checkPermissionStorage(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISO_MEMORIA_REQUEST);

            Log.d(TAG, "checkPermissionStorage: Solicitando permiso de memoria");
        }else{
            Log.d(TAG, "checkPermissionStorage: El permiso de memoria ya esta concedido");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISO_MEMORIA_REQUEST:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "onRequestPermissionsResult: permiso de memoria concedido");
                }else{
                    Log.d(TAG, "onRequestPermissionsResult: permiso de memoria denegado");
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(SyncAllService.EXTRA_SYNC_START.equals(action)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.getting_vendor_data) + VentaActivity.VendorUtil.getVendor(getApplicationContext()).getName(),
                            Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.got_data, Toast.LENGTH_SHORT).show();
                }

                //toogle();
            }
        };

        IntentFilter intentFilter =  new IntentFilter();
        intentFilter.addAction(SyncAllService.EXTRA_SYNC_START);
        intentFilter.addAction(SyncAllService.EXTRA_SYNC_FINISH);

        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(broadcastReceiver);
    }

    private void toogle(){
        View v  = findViewById(R.id.progressBar);
        int visivility = v.getVisibility();

        v.setVisibility( visivility == View.GONE ? View.VISIBLE : View.GONE);
    }


    private class TaskDownloadImage extends AsyncTask<Void, Void, Void>{

        private static final String BOUNDARY = "XXX";
        private static final String HYPHENS  = "--";
        private static final String CRLF  = "\r\n";

        @Override
        protected Void doInBackground(Void... voids) {
            File route = new File(Constantes.MAIN_DIR.toString() + File.separator + Constantes.CLIENTS_PHOTOS);
            String filname = "6032.jpg";
            File photo = new File(route, filname);

            FileInputStream fileInputStream;

            try {
                fileInputStream = new FileInputStream(photo);
            } catch (FileNotFoundException e) {
                return null;
            }

            try { //"{\"hola\":\"hola\"}"
                HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://10.0.0.4/UploadFile/process.php").openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("POST");

                urlConnection.setRequestProperty("Connection","KeepAlive");
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+BOUNDARY);

                DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());

                dataOutputStream.writeBytes(HYPHENS + BOUNDARY + CRLF);
                dataOutputStream.writeBytes("Content-Disposition:form-data; name=\"files[]\";filename=\""+photo.getName()+"\"");
                //dataOutputStream.writeBytes("Content-Type: image/jpeg" + CRLF);
                dataOutputStream.writeBytes("Content-Transfer-Encoding: binary" + CRLF);
                dataOutputStream.writeBytes(CRLF);

                Log.d("TaskDownloadImage",route.toString() + photo.getName() );
                Log.d("TaskDownloadImage", "Headers han sido escritos");

                int bytesAvailable = fileInputStream.available();
                int maxBufferSize = (int) (2 * Math.pow(1024,2));

                Log.d("TaskDownloadImage",String.format("bytes disponibles %d(%.2fMB)(%dKB)",bytesAvailable, (bytesAvailable / Math.pow(1024,2)), (int)(bytesAvailable / Math.pow(1024,1))));
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                /*byte[] buffer = new byte[bufferSize];

                int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                 while(bytesRead > 0){
                     dataOutputStream.write(buffer, 0, bufferSize);
                     bytesAvailable = fileInputStream.available();
                     bufferSize =  Math.min(bytesAvailable, maxBufferSize);
                     bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                 }*/

                byte[] buffer = new byte[bufferSize];
                fileInputStream.read(buffer);

                 Log.d("TaskDownloadImage", "The buffer size is "+buffer.length);

                dataOutputStream.write(buffer);

                 dataOutputStream.writeBytes(CRLF);
                dataOutputStream.writeBytes(HYPHENS + BOUNDARY + CRLF);
                dataOutputStream.flush();

                Log.d("TaskDownloadImage","Archivo escrito");


                String js = readStream(urlConnection.getInputStream());

                Log.d("TaskDownloadImage", "RCode:" + urlConnection.getResponseCode() +" "+js);

                fileInputStream.close();

                urlConnection.disconnect();
                /*try {
                    JSONObject jObject = new JSONObject(js);
                    Log.d("TaskDownloadImage", jObject.toString(3));
                }catch (JSONException e){
                    Log.e("TaskDownloadImage", e.getMessage());
                }*/

                    //Log.d("TaskDownloadImage", jObject.toString());
                /*HttpURLConnection urlConnection = (HttpURLConnection) new URL("http://10.0.0.65:8080/uploads/files.php").openConnection();
                //urlConnection.setConnectTimeout(1000 * 10);
                InputStream inputStream = urlConnection.getInputStream();

                String js = readStream(inputStream);

                try {
                    JSONObject jObject = new JSONObject(js);

                    Log.d("TaskDownloadImage", jObject.toString());

                    JSONArray jsonArray = jObject.getJSONArray("imagenes");


                    for(int i = 0; i < jsonArray.length(); i++){
                        String url =jsonArray.getString(i);
                        Log.d("TaskDownloadImage", url);

                        urlConnection = (HttpURLConnection) new URL(url).openConnection();


                        InputStream  stream = urlConnection.getInputStream();
                        Bitmap bm = BitmapFactory.decodeStream(stream);

                        FileUtils.savePhoto(bm, FileUtils.createFileRoute(Constantes.MAIN_DIR, Constantes.ITEMS_PHOTOS), FileUtils.createTmpFileName() + FileUtils.JPG_EXT, FileUtils.GOOD_QUALITY);
                    }


                } catch (JSONException e) {
                    Log.e("TaskDownloadImage", e.toString());
                }
                */
            } catch (IOException e) {
                Log.e("TaskDownloadImage", e.toString(), e.getCause());
            }

            return null;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }
    }
}