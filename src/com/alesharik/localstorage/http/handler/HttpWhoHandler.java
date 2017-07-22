package com.alesharik.localstorage.http.handler;

import com.alesharik.localstorage.data.DataManager;
import com.alesharik.localstorage.data.GsonUtils;
import com.alesharik.localstorage.data.User;
import com.alesharik.webserver.api.server.wrapper.bundle.Filter;
import com.alesharik.webserver.api.server.wrapper.bundle.HttpHandler;
import com.alesharik.webserver.api.server.wrapper.http.HeaderManager;
import com.alesharik.webserver.api.server.wrapper.http.HttpStatus;
import com.alesharik.webserver.api.server.wrapper.http.Request;
import com.alesharik.webserver.api.server.wrapper.http.Response;
import com.alesharik.webserver.api.server.wrapper.http.data.Authorization;
import com.alesharik.webserver.api.server.wrapper.http.header.AuthorizationHeader;

import java.util.UUID;

public class HttpWhoHandler implements HttpHandler, Filter {
    private static final UUID randomUUID = UUID.randomUUID();
    private final DataManager dataManager;

    public HttpWhoHandler(DataManager dataManager) {
        this.dataManager = dataManager;
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
        UUID userId = UUID.fromString(request.getParameter("id"));
        User user = dataManager.getUserTable().selectForKey(new User(userId));
        user.setInfoStatus(randomUUID);
        user.setPrivateStatus(randomUUID);
        String text = GsonUtils.getGson().toJson(user);
        response.respond(HttpStatus.OK_200);
        response.setContentLength(text.length());
        response.getWriter().write(text);
    }

    @Override
    public boolean accept(Request request, Response response) {
        return request.getContextPath().equals("/api/who");
    }
}
