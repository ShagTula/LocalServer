package com.alesharik.localstorage.http.handler;

import com.alesharik.database.Database;
import com.alesharik.localstorage.data.DataManager;
import com.alesharik.localstorage.data.GsonUtils;
import com.alesharik.localstorage.data.User;
import com.alesharik.localstorage.data.status.ChatStatus;
import com.alesharik.localstorage.http.auth.TokenHolder;
import com.alesharik.webserver.api.server.wrapper.bundle.FilteredHttpHandler;
import com.alesharik.webserver.api.server.wrapper.http.HeaderManager;
import com.alesharik.webserver.api.server.wrapper.http.HttpStatus;
import com.alesharik.webserver.api.server.wrapper.http.Method;
import com.alesharik.webserver.api.server.wrapper.http.Request;
import com.alesharik.webserver.api.server.wrapper.http.Response;
import com.alesharik.webserver.api.server.wrapper.http.data.Authorization;
import com.alesharik.webserver.api.server.wrapper.http.data.ContentType;
import com.alesharik.webserver.api.server.wrapper.http.header.AuthorizationHeader;
import com.alesharik.webserver.api.server.wrapper.http.header.ObjectHeader;

import java.nio.charset.Charset;
import java.util.UUID;

public class HttpSyncChatStatusHandler implements FilteredHttpHandler {
    private final Database database;
    private final TokenHolder tokenHolder;
    private final DataManager dataManager;

    public HttpSyncChatStatusHandler(Database database, TokenHolder tokenHolder, DataManager dataManager) {
        this.database = database;
        this.tokenHolder = tokenHolder;
        this.dataManager = dataManager;
    }

    @Override
    public void handle(Request request, Response response) {
        if(request.getMethod() != Method.PUT) {
            response.respond(HttpStatus.BAD_REQUEST_400);
            response.setContentLength(0);
            return;
        }

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

        ContentType header = request.getHeader(HeaderManager.getHeaderByName("Content-Type", ObjectHeader.class), ContentType.class);
        if(header == null) {
            response.respond(HttpStatus.BAD_REQUEST_400);
            response.setContentLength(0);
            return;
        }
        Charset charset = header.getCharset();
        String body = new String(request.getBody(), charset);
        ChatStatus chatStatus = GsonUtils.getGson().fromJson(body, ChatStatus.class);
        if(!chatStatus.getId().equals(user.getChatStatus())) {
            response.respond(HttpStatus.CONFLICT_409);
            response.setContentLength(0);
            return;
        }
        if(database.getTransactionManager().executeTransaction(() -> {
            ChatStatus db = dataManager.getChatStatusTable().selectByPrimaryKey(chatStatus);
            db.setNickName(chatStatus.getNickName());
            db.setOnlineStatus(chatStatus.getOnlineStatus());
            db.setData(chatStatus.getData());
            db.setAvatarUrl(chatStatus.getAvatarUrl());
            return true;
        })) {
            response.respond(HttpStatus.ACCEPTED_202);
        } else {
            response.respond(HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }

    @Override
    public boolean accept(Request request, Response response) {
        return request.getContextPath().equals("/api/status/chat/sync");
    }
}
