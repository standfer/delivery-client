package com.example.hello.maps1.helpers.data_types;

import com.example.hello.maps1.entities.BaseEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Ivan on 10.12.2017.
 */

public class CollectionsHelper {
    public static Boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static List<? extends BaseEntity> getCollectionCloned(Collection<? extends BaseEntity> srcCollection) throws CloneNotSupportedException {
        List<BaseEntity> destCollection = null;
        if (!isEmpty(srcCollection)) {
            destCollection = new ArrayList<>();

            for (BaseEntity baseEntity : srcCollection) {
                destCollection.add((BaseEntity) baseEntity.clone());
            }
        }

        return destCollection;
    }
}
