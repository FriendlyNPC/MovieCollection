package com.school.comp3717.moviecollection.collection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.school.comp3717.moviecollection.R;

import java.util.ArrayList;

/**
 * Created by Devan on 2015-03-16.
 */
public class GenreFilterDialog extends DialogFragment {

    ArrayList mSelectedItems = new ArrayList();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] genres = this.getArguments().getStringArray("genres");
        mSelectedItems = new ArrayList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.genre_filter_dialog_title)
                .setMultiChoiceItems(genres, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    if (which == 0) {
                                    }
                                    mSelectedItems.add(which);
                                } else if (mSelectedItems.contains(which)) {
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                .setPositiveButton(R.string.genre_filter_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().getIntent().putExtra("selected",mSelectedItems);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                    }
                })
                .setNegativeButton(R.string.genre_filter_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                    }
                });

        return builder.create();
    }
}
