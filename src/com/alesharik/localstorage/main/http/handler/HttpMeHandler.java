package com.alesharik.localstorage.main.http.handler;

import com.alesharik.localstorage.main.data.GsonUtils;
import com.alesharik.localstorage.main.data.User;
import com.alesharik.localstorage.main.http.auth.TokenHolder;
import com.alesharik.webserver.api.server.wrapper.bundle.Filter;
import com.alesharik.webserver.api.server.wrapper.bundle.HttpHandler;
import com.alesharik.webserver.api.server.wrapper.http.HeaderManager;
import com.alesharik.webserver.api.server.wrapper.http.HttpStatus;
import com.alesharik.webserver.api.server.wrapper.http.Request;
import com.alesharik.webserver.api.server.wrapper.http.Response;
import com.alesharik.webserver.api.server.wrapper.http.data.Authorization;
import com.alesharik.webserver.api.server.wrapper.http.data.MimeType;
import com.alesharik.webserver.api.server.wrapper.http.header.AuthorizationHeader;
import org.glassfish.grizzly.utils.Charsets;

import java.util.UUID;

public class HttpMeHandler implements HttpHandler, Filter {
    public static final MimeType JSON_TYPE = new MimeType("application", "json");

    private final TokenHolder tokenHolder;

    public HttpMeHandler(TokenHolder tokenHolder) {
        this.tokenHolder = tokenHolder;
    }

    @Override
    public Filter getFilter() {
        return this;
    }

    @Override
    public void handle(Request request, Response response) {
        Authorization authorization = request.getHeader(HeaderManager.getHeaderByName("Authorization", AuthorizationHeader.class), Authorization.class);
        if(authorization == null) {
            response.respond(HttpStatus.UNAUTHORIZED_401);
            response.setContentLength(0);
            return;
        }
        User user = tokenHolder.getUser(UUID.fromString(authorization.getCredentials()));
        if(user == null) {
            response.respond(HttpStatus.UNAUTHORIZED_401);
            response.setContentLength(0);
            return;
        }

        String text = GsonUtils.getGson().toJson(user);
        response.respond(HttpStatus.OK_200);
        response.setContentLength(text.length());
        response.setType(JSON_TYPE, Charsets.UTF8_CHARSET);
        response.getWriter().write(text);
    }

    @Override
    public boolean accept(Request request, Response response) {
        return request.getContextPath().equals("/api/me");
    }
}
