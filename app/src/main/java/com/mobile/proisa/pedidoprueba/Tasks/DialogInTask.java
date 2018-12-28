package com.mobile.proisa.pedidoprueba.Tasks;

import android.app.Activity;
import android.util.Log;

import com.mobile.proisa.pedidoprueba.Dialogs.ProgressDialog;

public abstract class DialogInTask<Params, Progress, Result> extends TareaAsincrona<Params, Progress, Result> {
    private ProgressDialog progressDialog;
    private boolean mDialogShow;

    public DialogInTask(int id, Activity context, OnFinishedProcess listener) {
        super(id, context, listener);
        progressDialog = ProgressDialog.newInstance("");
        this.mDialogShow = false;
    }

    public DialogInTask(int id, Activity context, OnFinishedProcess listener, boolean mDialogShow) {
        super(id, context, listener);
        progressDialog = ProgressDialog.newInstance("");
        this.mDialogShow = mDialogShow;
    }

    @Override
    protected void onPreExecute() {
        if(mDialogShow){
            progressDialog.show(getContext().getFragmentManager(), "");
        }
    }

    @Override
    protected void onProgressUpdate(Progress... values) {
        if(mDialogShow){
            progressDialog.changeInfo(String.valueOf(values[0]));
        }else {
            Log.i("DialogInTask", "This info is not showing: " + values[0]);
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);

        if(mDialogShow){
            progressDialog.dismiss();
            progressDialog = null;
        }

    }
}
