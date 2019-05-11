package com.example.hello.maps1.requestEngines;

import android.util.Log;

import com.example.hello.maps1.entities.Courier;
import com.tylerjroach.eventsource.EventSource;
import com.tylerjroach.eventsource.EventSourceHandler;
import com.tylerjroach.eventsource.MessageEvent;

import org.joda.time.DateTime;

public class SSEHandler implements EventSourceHandler {
    private EventSource eventSource;
    private Courier courier;

    public SSEHandler() {
    }

    public SSEHandler(Courier courier) {
        this.courier = courier;
    }

    @Override
    public void onConnect() {
        Log.d(getClass().getSimpleName(), "SSE connected");
    }

    @Override
    public void onMessage(String event, MessageEvent message) {
        Log.d(getClass().getSimpleName(),  String.format("SSE event: %s, [%s]", event, new DateTime()));

        if (message != null && message.data != null) {
            String data = message.data;
            switch(event) {
                case "updOrderPrice": {
                    if (courier != null) {
                        courier.setData(data);
                    }
                    break;
                }
            }

            Log.d(getClass().getSimpleName(), "SSE Message data: " + message.data);
        }
    }

    @Override
    public void onComment(String comment) {
        Log.d(getClass().getSimpleName(),"SSE Comment: " + comment);
    }

    @Override
    public void onError(Throwable t) {
        //ignore ssl NPE on eventSource.close()
    }

    @Override
    public void onClosed(boolean willReconnect) {
        Log.d(getClass().getSimpleName(),"SSE Closed. reconnect? " + willReconnect);
    }

    public Courier getCourier() {
        return courier;
    }

    public void setCourier(Courier courier) {
        this.courier = courier;
    }

    public EventSource getEventSource() {
        return eventSource;
    }

    public void setEventSource(EventSource eventSource) {
        this.eventSource = eventSource;
    }
}
