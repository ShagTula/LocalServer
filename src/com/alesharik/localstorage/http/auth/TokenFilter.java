package com.alesharik.localstorage.http.auth;


import com.alesharik.localstorage.http.RequestAccessLevels;
import com.alesharik.webserver.api.server.wrapper.bundle.Filter;
import com.alesharik.webserver.api.server.wrapper.http.HeaderManager;
import com.alesharik.webserver.api.server.wrapper.http.HttpStatus;
import com.alesharik.webserver.api.server.wrapper.http.Request;
import com.alesharik.webserver.api.server.wrapper.http.Response;
import com.alesharik.webserver.api.server.wrapper.http.data.Authentication;
import com.alesharik.webserver.api.server.wrapper.http.data.Authorization;
import com.alesharik.webserver.api.server.wrapper.http.header.AuthenticateHeader;
import com.alesharik.webserver.api.server.wrapper.http.header.AuthorizationHeader;

import java.util.UUID;

public final class TokenFilter implements Filter {
    private static final Authentication AUTHENTICATION = new Authentication(Authentication.Type.BEARER);

    private final TokenHolder tokenHolder;

    public TokenFilter(TokenHolder tokenHolder) {
        this.tokenHolder = tokenHolder;
    }

    @Override
    public boolean accept(Request request, Response response) {
        Authorization authorization = request.getHeader(HeaderManager.getHeaderByName("Authorization", AuthorizationHeader.class), Authorization.class);
        if(authorization == null || authorization.getType() != Authentication.Type.BEARER || !tokenHolder.isTokenValid(UUID.fromString(authorization.getCredentials()), RequestAccessLevels.forPath(request.getContextPath()))) {
            response.respond(HttpStatus.UNAUTHORIZED_401);
            response.addHeader(HeaderManager.getHeaderByName("WWW-Authenticate", AuthenticateHeader.class), AUTHENTICATION);
            return false;
        }
        return true;
    }
}
