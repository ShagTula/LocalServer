package com.alesharik.localstorage.main.http;

import com.alesharik.webserver.api.server.wrapper.bundle.HttpBundle;
import com.alesharik.webserver.api.server.wrapper.server.HttpServer;

final class ConditionImpl implements HttpBundle.Condition {
    @Override
    public boolean allow(HttpServer httpServer) {
        System.err.println("Using _localstorage-main-http-bundle without loaded local-storage-bundle not allowed!");
        return false;
    }
}
