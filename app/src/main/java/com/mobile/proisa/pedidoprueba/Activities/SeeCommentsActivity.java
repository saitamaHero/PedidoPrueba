package com.mobile.proisa.pedidoprueba.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mobile.proisa.pedidoprueba.Adapters.DiaryAdapter;
import com.mobile.proisa.pedidoprueba.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import Models.Client;
import Models.Diary;

public class SeeCommentsActivity extends AppCompatActivity {
    public static final String EXTRA_INFO = "extra_info";
    private List<Diary> visitas;
    private Client client;

    private RecyclerView recyclerView;

    private static String comments[]={"Buen cliente","Paga a tiempo","Mala paga", "Comentario marciano"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_comments);

        client = getIntent().getExtras().getParcelable(EXTRA_INFO);

        getSupportActionBar().setTitle(client.getName());
        getSupportActionBar().setSubtitle(R.string.comment_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateDiary(20);
        showComments();
    }

    private void showComments() {

        Collections.sort(visitas, new Diary.SortByDateDesc());
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new DiaryAdapter(visitas, R.layout.comment_item_layout));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    private void populateDiary(int count){
        visitas = new ArrayList<>();

        Random random = new Random();

        for(int i = 0; i < count; i++) {
            visitas.add(new Diary(i,Calendar.getInstance().getTime(),
                    comments[random.nextInt(comments.length)].concat(" ").concat(getString(R.string.lorem_ipsum))));

            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
