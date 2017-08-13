package com.alesharik.localstorage.http.handler;

import com.alesharik.localstorage.data.DataManager;
import com.alesharik.localstorage.data.GsonUtils;
import com.alesharik.localstorage.data.User;
import com.alesharik.localstorage.data.status.ChatStatus;
import com.alesharik.localstorage.http.auth.TokenHolder;
import com.alesharik.webserver.api.server.wrapper.bundle.Filter;
import com.alesharik.webserver.api.server.wrapper.bundle.HttpHandler;
import com.alesharik.webserver.api.server.wrapper.http.HeaderManager;
import com.alesharik.webserver.api.server.wrapper.http.HttpStatus;
import com.alesharik.webserver.api.server.wrapper.http.Request;
import com.alesharik.webserver.api.server.wrapper.http.Response;
import com.alesharik.webserver.api.server.wrapper.http.data.Authorization;
import com.alesharik.webserver.api.server.wrapper.http.header.AuthorizationHeader;
import org.glassfish.grizzly.utils.Charsets;

import java.util.UUID;

public class HttpGetChatStatusHandler implements HttpHandler, Filter {
    private final DataManager dataManager;
    private final TokenHolder tokenHolder;

    public HttpGetChatStatusHandler(DataManager dataManager, TokenHolder tokenHolder) {
        this.dataManager = dataManager;
        this.tokenHolder = tokenHolder;
    }

    @Override
    public void handle(Request request, Response response) {
        Authorization authorization = request.getHeader(HeaderManager.getHeaderByName("Authorization", AuthorizationHeader.class), Authorization.class);
        if(authorization == null) {
            response.respond(HttpStatus.UNAUTHORIZED_401);
            response.setContentLength(0);
            return;
        }

        if(request.getParameters().containsKey("id")) {
            UUID id = UUID.fromString(request.getParameter("id"));
            ChatStatus chatStatus = dataManager.getChatStatusTable().selectByPrimaryKey(new ChatStatus(id));
            if(chatStatus == null) {
                response.setContentLength(0);
                response.respond(HttpStatus.NOT_FOUND_404);
                return;
            } else {
                String text = GsonUtils.getGson().toJson(chatStatus);
                response.respond(HttpStatus.OK_200);
                response.setContentLength(text.length());
                response.setType(HttpMeHandler.JSON_TYPE, Charsets.UTF8_CHARSET);
                response.getWriter().write(text);
                return;
            }
        }

        User user = tokenHolder.getUser(UUID.fromString(authorization.getCredentials()));
        if(user == null) {
            response.respond(HttpStatus.UNAUTHORIZED_401);
            response.setContentLength(0);
            return;
        }

        ChatStatus chatStatus = dataManager.getChatStatusTable().selectByPrimaryKey(new ChatStatus(user.getChatStatus()));
        String text = GsonUtils.getGson().toJson(chatStatus);
        response.respond(HttpStatus.OK_200);
        response.setContentLength(text.length());
        response.setType(HttpMeHandler.JSON_TYPE, Charsets.UTF8_CHARSET);
        response.getWriter().write(text);
    }

    @Override
    public Filter getFilter() {
        return this;
    }

    @Override
    public boolean accept(Request request, Response response) {
        return request.getContextPath().equals("/api/status/chat");
    }
}
