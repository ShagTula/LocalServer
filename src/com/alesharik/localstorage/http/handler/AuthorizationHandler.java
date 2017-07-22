package com.alesharik.localstorage.http.handler;

import com.alesharik.localstorage.data.AccessLevel;
import com.alesharik.localstorage.data.DataManager;
import com.alesharik.localstorage.http.TokenHolder;
import com.alesharik.webserver.api.server.wrapper.bundle.Filter;
import com.alesharik.webserver.api.server.wrapper.bundle.HttpHandler;
import com.alesharik.webserver.api.server.wrapper.http.HeaderManager;
import com.alesharik.webserver.api.server.wrapper.http.HttpStatus;
import com.alesharik.webserver.api.server.wrapper.http.Request;
import com.alesharik.webserver.api.server.wrapper.http.Response;
import com.alesharik.webserver.api.server.wrapper.http.data.Authentication;
import com.alesharik.webserver.api.server.wrapper.http.data.Authorization;
import com.alesharik.webserver.api.server.wrapper.http.data.MimeType;
import com.alesharik.webserver.api.server.wrapper.http.header.AuthorizationHeader;
import com.alesharik.webserver.api.server.wrapper.http.util.IpBanManager;
import org.glassfish.grizzly.utils.Charsets;

import java.util.UUID;

public final class AuthorizationHandler implements HttpHandler {
    private static final MimeType TEXT_MIME_TYPE = new MimeType("text", "plain");

    private final TokenHolder tokenHolder;
    private final DataManager dataManager;
    private final IpBanManager ipBanManager;

    public AuthorizationHandler(TokenHolder tokenHolder, DataManager dataManager, IpBanManager ipBanManager) {
        this.tokenHolder = tokenHolder;
        this.dataManager = dataManager;
        this.ipBanManager = ipBanManager;
    }

    @Override
    public Filter getFilter() {
        return Filter.Never.getInstance();//Prevent explicit calling
    }

    @Override
    public void handle(Request request, Response response) {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String accessLevel = request.getParameter("level");
        if(accessLevel == null || login == null || password == null) {
            response.setContentLength(0);
            response.respond(HttpStatus.BAD_REQUEST_400);
            return;
        }
        AccessLevel parsed = AccessLevel.forKey(Integer.parseInt(accessLevel));

        if(parsed != AccessLevel.PUBLIC) {
            Authorization authorization = request.getHeader(HeaderManager.getHeaderByName("Authorization", AuthorizationHeader.class), Authorization.class);
            if(authorization == null || authorization.getType() != Authentication.Type.BEARER || !tokenHolder.isTokenValid(UUID.fromString(authorization.getCredentials()), parsed == AccessLevel.PRIVATE ? AccessLevel.PUBLIC : AccessLevel.PRIVATE)) {
                if(parsed == AccessLevel.CONFIDENTIAL) {//Ban user!
                    ipBanManager.ban(request.getRemote().getAddress());
                    response.setContentLength(0);
                    response.respond(HttpStatus.FORBIDDEN_403);
                    return;
                } else {
                    response.setContentLength(0);
                    response.respond(HttpStatus.UNAUTHORIZED_401);
                    return;
                }
            }
        }

        if(dataManager.checkUser(login, password)) {
            UUID token = UUID.randomUUID();
            tokenHolder.addToken(token, parsed);
            String send = token.toString();
            response.respond(HttpStatus.OK_200);
            response.setContentLength(send.length());
            response.setType(TEXT_MIME_TYPE, Charsets.UTF8_CHARSET);
            response.getWriter().write(send);
        } else {
            if(parsed == AccessLevel.CONFIDENTIAL) {
                ipBanManager.ban(request.getRemote().getAddress());
                response.setContentLength(0);
                response.respond(HttpStatus.FORBIDDEN_403);
            } else {
                response.setContentLength(0);
                response.respond(HttpStatus.UNAUTHORIZED_401);
            }
        }
    }
}
