package com.example.hello.maps1.activities.alerts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.R;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.helpers.ToolsHelper;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ivan on 10.12.2017.
 */

public class AlertDialogFragment {
   /* @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_fire_missiles)
                .setPositiveButton(R.string.fire, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }*/

    public static AlertDialog showOrdersToAssign(final MainMapsActivity mainMapsActivity) throws CloneNotSupportedException {
        if (mainMapsActivity.getCourier() != null && mainMapsActivity.getCourier().isReadyToAssign() &&
                mainMapsActivity.getCourier().getOrdersAvailable() != null) {

            final Courier courierOrigin = mainMapsActivity.getCourier(); //todo delete if all ok

            final Courier courier = courierOrigin;
            final List<Order> ordersAvailable = courier.getOrdersAvailable();
            final List<Order> ordersToAssign = courier.getOrdersToAssign();

            final String[] ordersData = new String[ordersAvailable.size()];
            courier.getOrdersData(ordersAvailable).toArray(ordersData);
            courier.setReadyToAssign(false);

            final boolean[] checkedItems = new boolean[ordersAvailable.size()];
            for (boolean item : checkedItems) {
                item = false;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mainMapsActivity);
            builder.setTitle("Для вашего местоположения доступны следующие заказы:")
                    .setIcon(R.drawable.ic_media_play)
                    .setCancelable(false)
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
                            for (int i = 0; i < checkedItems.length; i++) {
                                if (checkedItems[i]) {
                                    ordersToAssign.add(ordersAvailable.get(i));
                                }
                            }
                            courier.assignOrders(ordersToAssign, mainMapsActivity);
                            courier.updateAssignmentState();
                            //mainMapsActivity.setCourier(courier);
                            mainMapsActivity.setTimerActive(true);
                        }
                    })
                    .setNegativeButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    courier.updateAssignmentState();
                                    //mainMapsActivity.setCourier(courier);
                                    mainMapsActivity.setTimerActive(true);
                                }
                            });
            AlertDialog alert = builder.create();

            return alert;
        }

        return null;
    }
}
