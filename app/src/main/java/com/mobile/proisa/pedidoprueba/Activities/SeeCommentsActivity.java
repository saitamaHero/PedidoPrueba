package com.mobile.proisa.pedidoprueba.Activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mobile.proisa.pedidoprueba.Adapters.DiaryAdapter;
import com.mobile.proisa.pedidoprueba.R;

import java.util.Collections;
import java.util.List;

import Models.Client;
import Models.Diary;
import Sqlite.DiaryController;
import Sqlite.MySqliteOpenHelper;

public class SeeCommentsActivity extends BaseCompatAcivity {
    private RecyclerView recyclerView;
    private Client client;
    private List<Diary> visitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_comments);

        client = getIntent().getExtras().getParcelable(EXTRA_CLIENT);

        getSupportActionBar().setTitle(client.getName());
        getSupportActionBar().setSubtitle(R.string.comment_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        visitas = getComments();
        showComments();
    }

    @Override
    protected void onBindUI() {
        recyclerView = findViewById(R.id.recycler_view);
    }

    private List<Diary> getComments() {
        DiaryController diaryController = new DiaryController(MySqliteOpenHelper.getInstance(this).getReadableDatabase());

        List<Diary> diaryList = diaryController.getAllCompleteById(client.getId());

        Collections.sort(diaryList, new Diary.SortByDateDesc());

        return diaryList;
    }

    private void showComments() {

        recyclerView.setAdapter(new DiaryAdapter(visitas, R.layout.comment_item_layout));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
