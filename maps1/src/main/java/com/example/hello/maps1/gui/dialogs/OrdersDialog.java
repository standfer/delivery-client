package com.example.hello.maps1.gui.dialogs;



import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.hello.maps1.R;

/**
 * Created by Ivan on 16.12.2017.
 */

public class OrdersDialog extends DialogFragment { //not used, todo change if AlertDialog is deprecated
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] ordersData = new String[2];
        //ordersData = courier.getOrdersData(orders).values().toArray(ordersData);
        ordersData[0] = "test1";
        ordersData[1] = "test2";

        final boolean[] checkedItems = new boolean[ordersData.length];
        for (boolean item : checkedItems) {
            item = false;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Для вашего местоположения доступны следующие заказы:")
                .setIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                .setCancelable(false)
                /*.setItems(ordersData, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            *//*Toast toast = Toast.makeText(mainMapsActivity.getApplicationContext(), "", Toast.LENGTH_LONG);
                            ToolsHelper.showMsgToUser("test toast", toast);*//*

                            *//*
                            Toast.makeText(mainMapsActivity.getApplicationContext(),
                                    "Выбранный кот: " + ordersData[which],
                                    Toast.LENGTH_SHORT).show();*//*
                    }
                })*/
                .setMultiChoiceItems(ordersData, checkedItems,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which, boolean isChecked) {
                                checkedItems[which] = isChecked;
                            }
                        })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


        return builder.create();
    }
}
