package com.mobile.proisa.pedidoprueba.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Menu;

import com.mobile.proisa.pedidoprueba.R;

/**
 * Created by dionicio on 26/11/18.
 */

public class PhotoActionDialog extends DialogFragment {
    public static final int TAKE_PHOTO = 0;
    public static final int PICK_PHOTO = 1;

    private OnActionPressedListener onActionPressedListener;

    public PhotoActionDialog() {

        setStyle(STYLE_NO_TITLE, getTheme());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        /*builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });*/

        builder.setItems(R.array.photo_action, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(onActionPressedListener != null){
                    onActionPressedListener.onActionPressed(i);
                }
            }
        });

        return builder.create();
    }

    public void setOnActionPressedListener(OnActionPressedListener onActionPressedListener) {
        this.onActionPressedListener = onActionPressedListener;
    }


    public interface OnActionPressedListener{
        void onActionPressed(int action);
    }
}
