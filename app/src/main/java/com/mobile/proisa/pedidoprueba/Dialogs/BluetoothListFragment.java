package com.mobile.proisa.pedidoprueba.Dialogs;


import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.mobile.proisa.pedidoprueba.Adapters.BluetoothAdapter;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class BluetoothListFragment extends DialogFragment implements AdapterView.OnItemClickListener, View.OnClickListener{
    private static final String PARAM_LIST = "com.mobile.proisa.pedidoprueba.Dialogs.PARAM_LIST";

    private ListView bluetoothListView;
    private Button btnPair;
    private List<BluetoothDevice> bluetoothDeviceList;
    private OnBluetoothSelectedListener onBluetoothSelectedListener;

    public BluetoothListFragment() {
        // Required empty public constructor
    }

    public static BluetoothListFragment newInstance(List<BluetoothDevice> devices) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(PARAM_LIST, new ArrayList<Parcelable>(devices));
        BluetoothListFragment fragment = new BluetoothListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth_list, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            bluetoothDeviceList = getArguments().getParcelableArrayList(PARAM_LIST);
        }



        onBluetoothSelectedListener = (OnBluetoothSelectedListener) getActivity();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BluetoothAdapter adapter = new BluetoothAdapter(getActivity(), R.layout.bluetooth_item_layout);
        adapter.addAll(bluetoothDeviceList);

        bluetoothListView = view.findViewById(R.id.list_devices);
        bluetoothListView.setAdapter(adapter);
        bluetoothListView.setOnItemClickListener(this);
        adapter.notifyDataSetChanged();

        btnPair = view.findViewById(R.id.btn_pair);
        btnPair.setOnClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
        onBluetoothSelectedListener.onBluetoothSelected(device);

        dismiss();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_pair){
            dismiss();
            getActivity().startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
        }
    }


    public interface OnBluetoothSelectedListener{
        public void onBluetoothSelected(BluetoothDevice device);
    }
}
