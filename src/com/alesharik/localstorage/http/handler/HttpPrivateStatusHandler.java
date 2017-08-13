package com.alesharik.localstorage.http.handler;

import com.alesharik.database.Database;
import com.alesharik.localstorage.data.DataManager;
import com.alesharik.localstorage.data.GsonUtils;
import com.alesharik.localstorage.data.User;
import com.alesharik.localstorage.data.status.PrivateStatus;
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
import com.alesharik.webserver.api.server.wrapper.http.util.IpBanManager;
import org.glassfish.grizzly.utils.Charsets;

import java.nio.charset.Charset;
import java.util.UUID;

public class HttpPrivateStatusHandler implements FilteredHttpHandler {
    private final Database database;
    private final DataManager dataManager;
    private final TokenHolder tokenHolder;
    private final IpBanManager ipBanManager;

    public HttpPrivateStatusHandler(Database database, DataManager dataManager, TokenHolder tokenHolder, IpBanManager ipBanManager) {
        this.database = database;
        this.dataManager = dataManager;
        this.tokenHolder = tokenHolder;
        this.ipBanManager = ipBanManager;
    }

    @Override
    public void handle(Request request, Response response) {
        Authorization authorization = request.getHeader(HeaderManager.getHeaderByName("Authorization", AuthorizationHeader.class), Authorization.class);
        if(authorization == null) {
            responseUnauthorized(request, response);
            return;
        }

        User user = tokenHolder.getUser(UUID.fromString(authorization.getCredentials()));
        if(user == null) {
            responseUnauthorized(request, response);
            return;
        }

        if(!request.isSecure()) {
            response.respond(HttpStatus.FORBIDDEN_403);
            response.setContentLength(0);
            ipBanManager.ban(request.getRemote().getAddress());
        }

        if(request.getMethod() == Method.PUT) {
            ContentType header = request.getHeader(HeaderManager.getHeaderByName("Content-Type", ObjectHeader.class), ContentType.class);
            if(header == null) {
                respondBadRequest(request, response);
                return;
            }
            Charset charset = header.getCharset();
            String body = new String(request.getBody(), charset);

            PrivateStatus privateStatus = GsonUtils.getGson().fromJson(body, PrivateStatus.class);
            if(privateStatus == null) {
                respondBadRequest(request, response);
                return;
            }

            if(!privateStatus.getId().equals(user.getPrivateStatus())) {
                response.respond(HttpStatus.CONFLICT_409);
                ipBanManager.ban(request.getRemote().getAddress());
                return;
            }
            if(!database.getTransactionManager().executeTransaction(() -> {
                PrivateStatus db = dataManager.getPrivateStatusTable().selectByPrimaryKey(privateStatus);
                db.setData(privateStatus.getData());
                db.setPhone(privateStatus.getPhone());
                return true;
            })) {
                response.respond(HttpStatus.INTERNAL_SERVER_ERROR_500);
            } else {
                response.respond(HttpStatus.ACCEPTED_202);
            }
        } else if(request.getMethod() == Method.GET) {
            PrivateStatus privateStatus = dataManager.getPrivateStatusTable().selectByPrimaryKey(new PrivateStatus(user.getPrivateStatus()));
            String text = GsonUtils.getGson().toJson(privateStatus);
            response.respond(HttpStatus.OK_200);
            response.setContentLength(text.length());
            response.setType(HttpMeHandler.JSON_TYPE, Charsets.UTF8_CHARSET);
            response.getWriter().write(text);
        } else {
            response.respond(HttpStatus.NOT_IMPLEMENTED_501);
        }
    }

    private void respondBadRequest(Request request, Response response) {
        response.respond(HttpStatus.BAD_REQUEST_400);
        response.setContentLength(0);
        ipBanManager.ban(request.getRemote().getAddress());
    }

    private void responseUnauthorized(Request request, Response response) {
        response.respond(HttpStatus.UNAUTHORIZED_401);
        response.setContentLength(0);
        ipBanManager.ban(request.getRemote().getAddress());
    }

    @Override
    public boolean accept(Request request, Response response) {
        return request.getContextPath().equals("/api/status/private");
    }
}
