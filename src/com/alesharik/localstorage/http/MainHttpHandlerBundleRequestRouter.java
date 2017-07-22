package com.alesharik.localstorage.http;

import com.alesharik.localstorage.http.chains.AuthorizationChain;
import com.alesharik.webserver.api.server.wrapper.bundle.FilterChain;
import com.alesharik.webserver.api.server.wrapper.bundle.RequestRouter;
import com.alesharik.webserver.api.server.wrapper.bundle.impl.BasicFilterChain;
import com.alesharik.webserver.api.server.wrapper.http.Request;

final class MainHttpHandlerBundleRequestRouter implements RequestRouter {
    @Override
    public FilterChain route(Request request, FilterChain[] filterChains) {
        if(request.getContextPath().equals("/api/login"))
            return selectChain(AuthorizationChain.class, filterChains);
        else if(request.getContextPath().equals("/api"))
            return selectChain(BasicFilterChain.class, filterChains);
        return null;
    }

    private FilterChain selectChain(Class<?> clazz, FilterChain[] chains) {
        for(FilterChain chain : chains) {
            if(chain.getClass() == clazz)
                return chain;
        }
        return null;
    }
}
