package com.alesharik.localstorage.http.auth;

import com.alesharik.webserver.api.server.wrapper.bundle.FilterChain;
import com.alesharik.webserver.api.server.wrapper.bundle.HttpHandler;
import com.alesharik.webserver.api.server.wrapper.http.Request;
import com.alesharik.webserver.api.server.wrapper.http.Response;
import com.alesharik.webserver.api.server.wrapper.http.util.IpBanManager;

import javax.annotation.Nonnull;

public final class AuthorizationChain implements FilterChain {
    private final IpBanManager ipBanManager;

    public AuthorizationChain(IpBanManager ipBanManager) {
        this.ipBanManager = ipBanManager;
    }

    @Nonnull
    @Override
    public Response handleRequest(Request request, HttpHandler[] httpHandlers) {
        Response response = Response.getResponse();
        if(!ipBanManager.accept(request, response))
            return response;

        httpHandlers[0].handle(request, response);
        return response;
    }

    @Override
    public boolean accept(Request request, Response response) {
        return true;
    }
}
