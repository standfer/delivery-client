package com.example.hello.maps1.helpers;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public class ParcelableHelper {
    public static byte[] marshall(Parcelable parceable) {
        Parcel parcel = Parcel.obtain();
        parceable.writeToParcel(parcel, 0);
        byte[] bytes = parcel.marshall();
        parcel.recycle();
        return bytes;
    }

    public static Parcel unmarshall(byte[] bytes) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0); // This is extremely important!
        return parcel;
    }

    public static <T> T unmarshall(byte[] bytes, Parcelable.Creator<T> creator) {
        Parcel parcel = unmarshall(bytes);
        T result = creator.createFromParcel(parcel);
        parcel.recycle();
        return result;
    }

    public static byte[] getByteArrayFromObject(Object object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytesArray = null;
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            bytesArray = bos.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();
            Log.d(ParcelableHelper.class.getName(), ex.toString());
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                Log.d(ParcelableHelper.class.getName(), ex.toString());
            }
        }

        return bytesArray;
    }

    public static Object getObjectFromByteArray(byte[] byteArray) {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        Object object = null;

        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            object = in.readObject();
        } catch (Throwable ex) {
            ex.printStackTrace();
            Log.d(ParcelableHelper.class.getName(), ex.toString());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                Log.d(ParcelableHelper.class.getName(), ex.toString());
            }
        }

        return object;
    }
}
