package com.example.hello.maps1.constants;

import com.example.hello.maps1.R;

/**
 * Created by Ivan on 31.08.2017.
 */

public interface Constants {
    //String SERVER_ADDRESS = "http://192.168.1.37/delivery/helpers/helper.php"; //"http://192.168.0.102/delivery/helpers/helper.php";// "http://192.168.43.4/delivery/helpers/helper.php";//"http://192.168.1.34/delivery/helpers/helper.php";
    //String SERVER_ADDRESS = "http://192.168.43.185/delivery/helpers/helper.php";

    String SERVER_ADDRESS = "http://standfer231.myjino.ru/delivery/helpers/helper.php";
    String SERVER_LOGS_URL = "http://standfer231.myjino.ru/delivery/logs/remote_stacktrace.php";

    String MSG_ORDER_PROBLEMS = "Внимание! Уведомление о проблемах с заказом отправлено оператору";
    String MSG_CALL_FORBIDDEN = "Call forbidden";

    String MSG_ORDER_IS_DELIVERED = "Заказ успешно доставлен";
    String MSG_ORDER_TITLE = "Состояние заказа";
    String ERROR_NO_DATA_FOR_COURIER = "Для курьера %s нет данных";

    String MSG_ORDERS_AVAILABLE = "Выберите заказы для доставки";
    String MSG_ORDERS_AVAILABLE_TITLE = "Внимание";

    String MSG_SAVE_SUCCESSFULL = "Данные успешно сохранены";

    Double DISTANCE_DRIVER_NEAR_WORKPLACE = 30D;

    String TAG_MAPS_NAME = "map";

    String PHONE_NUMBER_OPERATOR = "89270111078";

    Integer MAP_PADDING_TOP_HALF_SCREEN = 578;
    Integer MAP_PADDING_TOP_FULL_SCREEN = 950;

    //SERVER METHODS
    String METHOD_NAME_updateCourierLocation = "updateDTOCourierLocation";
    String METHOD_NAME_getOrdersUnassigned = "getOrdersUnassigned";
    String METHOD_NAME_assignOrdersToCourier = "assignOrdersToCourier";
    String METHOD_NAME_setOrdersAssigned = "setOrdersAssigned";

    Integer REQUEST_CODE_PERMISSION_ALL = 0;

    Long TIMEOUT_COURIER_ASSIGNMENT = 30000L;
    double PROXIMITY_ALERT_RADIUS = 300;
    int ONGOING_NOTIFICATION_ID = 122163;
    String SERVICE_NOTIFICATION_CHANNEL_ID = "TrackingService_" + ONGOING_NOTIFICATION_ID;
    int SERVICE_NOTIFICATION_ICON = R.drawable.common_google_signin_btn_icon_dark_normal;
    String SERVICE_NOTIFICATION_TITLE = "Доставка";
    String SERVICE_NOTIFICATION_TEXT = "Приложение запущено";

    int ALARM_RECEIVER_ID = 321456;
    long ALARM_INTERVAL_FIVE_SECONDS = 5000L; //300000L;


    boolean IS_UI_REFRESH_ENABLED = false;
    boolean IS_SHOW_MSG_PROVIDER_CHANGED = false;
}
