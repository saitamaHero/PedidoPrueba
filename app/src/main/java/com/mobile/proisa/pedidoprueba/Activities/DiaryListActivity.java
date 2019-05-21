package com.mobile.proisa.pedidoprueba.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.mobile.proisa.pedidoprueba.Adapters.DiaryListAdapter;
import com.mobile.proisa.pedidoprueba.Fragments.DiaryListFragment;
import com.mobile.proisa.pedidoprueba.Fragments.TextMessageFragment;
import com.mobile.proisa.pedidoprueba.R;

import java.util.List;

import Models.Client;
import Models.Diary;
import Sqlite.DiaryController;
import Sqlite.MySqliteOpenHelper;

public class DiaryListActivity extends BaseCompatAcivity implements DiaryListAdapter.OnDiaryClickListener{

    private DiaryController diaryController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        setTitle(R.string.diaries_title);

        getClientAndSearchDiaries();
    }

    private void getClientAndSearchDiaries(){
        Intent intent = getIntent();

        if(intent != null){
            Bundle extras = intent.getExtras();

            if(extras != null && extras.containsKey(DetailsClientActivity.EXTRA_CLIENT)){
                Client client = extras.getParcelable(DetailsClientActivity.EXTRA_CLIENT);
                showDiaryForClient(client);
            }
        }
    }

    private void showDiaryForClient(Client client){
        diaryController = new DiaryController(MySqliteOpenHelper.getInstance(this).getReadableDatabase());
        List<Diary> diaries = diaryController.getAllById(client.getId());

        if(diaries.size() > 0){
            setCurrentFragment(R.id.container, DiaryListFragment.newInstance(diaries));
        }else{
            setCurrentFragment(R.id.container, TextMessageFragment.newInstance(getString(R.string.not_diaries, client.getName())));
        }

    }

    @Override
    public void onInvoiceClick(Diary item) {
        Toast.makeText(this, item.toString(), Toast.LENGTH_SHORT).show();
    }
}
