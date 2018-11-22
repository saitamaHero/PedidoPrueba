package com.mobile.proisa.pedidoprueba.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.proisa.pedidoprueba.Adapters.ActividadAdapter;
import com.mobile.proisa.pedidoprueba.Clases.Actividad;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.List;


public class ActividadFragment extends Fragment {
    private static final String PARAM_ACTIVIDAD_LIST = "param_actividad_list";
    private RecyclerView recyclerView;
    private List<Actividad> actividadList;

    public ActividadFragment() {
        // Required empty public constructor
    }

    public static ActividadFragment newInstance(List<Actividad> list) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(PARAM_ACTIVIDAD_LIST, new ArrayList<>(list));
        ActividadFragment fragment = new ActividadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            actividadList = getArguments().getParcelableArrayList(PARAM_ACTIVIDAD_LIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_actividad, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycler_view);

        setAdapter();
    }

    private void setAdapter() {
        ActividadAdapter actividadAdapter = new ActividadAdapter(this.actividadList, R.layout.data_detail_layout);
        recyclerView.setAdapter(actividadAdapter);

        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }
}
