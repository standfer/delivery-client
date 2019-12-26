package com.example.hello.maps1.activities.alerts;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.hello.maps1.MainMapsActivity;
import com.example.hello.maps1.R;
import com.example.hello.maps1.entities.Courier;
import com.example.hello.maps1.entities.Order;
import com.example.hello.maps1.helpers.data_types.CollectionsHelper;

import java.util.List;

/**
 * Created by Ivan on 10.12.2017.
 */

public class AlertDialogFragment {

    public static AlertDialog showOrdersToAssign(final MainMapsActivity mainMapsActivity) throws CloneNotSupportedException {
        if (mainMapsActivity.getCourier() != null && mainMapsActivity.getCourier().isReadyToAssign() &&
                CollectionsHelper.isEmpty(mainMapsActivity.getCourier().getOrdersToAssign()) &&
                !CollectionsHelper.isEmpty(mainMapsActivity.getCourier().getOrdersAvailable())) {

            final Courier courierOrigin = mainMapsActivity.getCourier(); //todo delete if all ok

            final Courier courier = courierOrigin;
            final List<Order> ordersAvailable = courier.getOrdersAvailable();
            final List<Order> ordersToAssign = courier.getOrdersToAssign();

            final String[] ordersData = new String[ordersAvailable.size()];
            courier.getOrdersData(ordersAvailable).toArray(ordersData);

            final boolean[] checkedItems = new boolean[ordersAvailable.size()];
            for (boolean item : checkedItems) {
                item = false;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mainMapsActivity);
            builder.setTitle("Для вашего местоположения доступны следующие заказы:")
                    .setIcon(R.drawable.common_google_signin_btn_icon_dark_normal)
                    .setCancelable(true)
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
                            ordersAvailable.removeAll(ordersToAssign);
                            courier.assignOrders(ordersToAssign, mainMapsActivity);
                            courier.updateAssignmentState();
                            mainMapsActivity.setAssignedOrdersAlertDialog(null);
                        }
                    })
                    .setNegativeButton("Отмена",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    courier.updateAssignmentState();
                                    mainMapsActivity.setAssignedOrdersAlertDialog(null);
                                    mainMapsActivity.setTimerActive(true);
                                }
                            });
            AlertDialog alert = builder.create();
            mainMapsActivity.setAssignedOrdersAlertDialog(alert);
            alert.cancel();

            return alert;
        }

        return null;
    }
}
