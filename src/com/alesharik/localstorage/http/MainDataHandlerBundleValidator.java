package com.alesharik.localstorage.http;

import com.alesharik.webserver.api.server.wrapper.bundle.Validator;
import com.alesharik.webserver.api.server.wrapper.http.Request;

final class MainDataHandlerBundleValidator implements Validator {
    @Override
    public boolean isRequestValid(Request request) {
        return request.getContextPath().startsWith("/api");
    }
}
