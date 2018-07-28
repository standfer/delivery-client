package com.example.hello.maps1.constants;

/**
 * Created by Ivan on 31.08.2017.
 */

public interface Constants {
    String SERVER_ADDRESS = "http://192.168.1.40/delivery/helpers/helper.php";// "http://192.168.43.4/delivery/helpers/helper.php";//"http://192.168.1.34/delivery/helpers/helper.php";
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

    String PHONE_NUMBER_OPERATOR = "1234";
}
