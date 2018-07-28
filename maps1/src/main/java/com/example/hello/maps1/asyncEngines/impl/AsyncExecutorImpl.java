package com.example.hello.maps1.asyncEngines.impl;

import android.os.AsyncTask;

import com.example.hello.maps1.asyncEngines.AsyncExecutor;
import com.example.hello.maps1.requestEngines.RequestHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Ivan on 05.06.2018.
 */

public class AsyncExecutorImpl extends AsyncTask<Object, Void, Void> {
    @Override
    protected Void doInBackground(Object... params) { //Method method = RequestHelper.class.getMethod("requestRouteByCoordinates", Coordinate.class, Coordinate.class); //can be invoked in other class before asyncExecutor
        if (params != null && params.length > 2) {
            try {
                Method method = (Method) params[0];
                Object receiver = new Object();

                List<Object> args = new ArrayList<>();
                for (int i = 2; i < params.length; i++) {
                    args.add(params[i]);
                }

                method.invoke(receiver, args);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
