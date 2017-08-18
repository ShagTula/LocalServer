package com.alesharik.localstorage.version.http;

import com.alesharik.webserver.api.server.wrapper.bundle.HttpBundle;
import com.alesharik.webserver.api.server.wrapper.server.HttpServer;

final class VersioningModuleHttpBundleCondition implements HttpBundle.Condition {
    @Override
    public boolean allow(HttpServer httpServer) {
        return false;
    }
}
