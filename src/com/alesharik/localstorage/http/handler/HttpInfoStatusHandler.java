package com.alesharik.localstorage.http.handler;

import com.alesharik.database.Database;
import com.alesharik.localstorage.data.DataManager;
import com.alesharik.localstorage.data.GsonUtils;
import com.alesharik.localstorage.data.User;
import com.alesharik.localstorage.data.status.InfoStatus;
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
import org.glassfish.grizzly.utils.Charsets;

import java.nio.charset.Charset;
import java.util.UUID;

public class HttpInfoStatusHandler implements FilteredHttpHandler {
    private final DataManager dataManager;
    private final Database database;
    private final TokenHolder tokenHolder;

    public HttpInfoStatusHandler(DataManager dataManager, Database database, TokenHolder tokenHolder) {
        this.dataManager = dataManager;
        this.database = database;
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

        User user = tokenHolder.getUser(UUID.fromString(authorization.getCredentials()));
        if(user == null) {
            response.respond(HttpStatus.UNAUTHORIZED_401);
            response.setContentLength(0);
            return;
        }

        if(request.getMethod() == Method.PUT) {
            ContentType header = request.getHeader(HeaderManager.getHeaderByName("Content-Type", ObjectHeader.class), ContentType.class);
            if(header == null) {
                response.respond(HttpStatus.BAD_REQUEST_400);
                response.setContentLength(0);
                return;
            }
            Charset charset = header.getCharset();
            String body = new String(request.getBody(), charset);
            InfoStatus infoStatus = GsonUtils.getGson().fromJson(body, InfoStatus.class);
            if(!infoStatus.getId().equals(user.getInfoStatus())) {
                response.respond(HttpStatus.CONFLICT_409);
                response.setContentLength(0);
                return;
            }

            if(database.getTransactionManager().executeTransaction(() -> {
                InfoStatus db = dataManager.getInfoStatusTable().selectByPrimaryKey(infoStatus);
                db.setData(infoStatus.getData());
                db.setBirthday(infoStatus.getBirthday());
                db.setPatronymic(infoStatus.getPatronymic());
                db.setLastName(infoStatus.getLastName());
                db.setFirstName(infoStatus.getFirstName());
                return true;
            })) {
                response.respond(HttpStatus.ACCEPTED_202);
                response.setContentLength(0);
            } else {
                response.respond(HttpStatus.INTERNAL_SERVER_ERROR_500);
                response.setContentLength(0);
            }
        } else if(request.getMethod() == Method.GET) {
            InfoStatus status = dataManager.getInfoStatusTable().selectByPrimaryKey(new InfoStatus(user.getInfoStatus()));
            String text = GsonUtils.getGson().toJson(status);
            response.respond(HttpStatus.OK_200);
            response.setContentLength(text.length());
            response.setType(HttpMeHandler.JSON_TYPE, Charsets.UTF8_CHARSET);
            response.getWriter().write(text);
        } else {
            response.respond(HttpStatus.NOT_IMPLEMENTED_501);
        }
    }

    @Override
    public boolean accept(Request request, Response response) {
        return request.getContextPath().equals("/api/status/info");
    }
}
