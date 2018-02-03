package com.example.hello.maps1.helpers;

import java.util.Collection;

/**
 * Created by Ivan on 10.12.2017.
 */

public class CollectionsHelper {
    public static Boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }
}
