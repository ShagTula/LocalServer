package com.alesharik.localstorage.http;

import com.alesharik.database.Database;
import com.alesharik.localstorage.data.DataManager;
import com.alesharik.localstorage.http.auth.AuthorizationChain;
import com.alesharik.localstorage.http.auth.TokenFilter;
import com.alesharik.localstorage.http.auth.AuthorizationHandler;
import com.alesharik.localstorage.http.auth.TokenHolder;
import com.alesharik.localstorage.http.handler.HttpGetChatStatusHandler;
import com.alesharik.localstorage.http.handler.HttpMeHandler;
import com.alesharik.localstorage.http.handler.HttpSyncChatStatusHandler;
import com.alesharik.localstorage.http.handler.HttpWhoHandler;
import com.alesharik.webserver.api.server.wrapper.bundle.ErrorHandler;
import com.alesharik.webserver.api.server.wrapper.bundle.FilterChain;
import com.alesharik.webserver.api.server.wrapper.bundle.HttpBundle;
import com.alesharik.webserver.api.server.wrapper.bundle.HttpHandler;
import com.alesharik.webserver.api.server.wrapper.bundle.HttpHandlerBundle;
import com.alesharik.webserver.api.server.wrapper.bundle.RequestRouter;
import com.alesharik.webserver.api.server.wrapper.bundle.Validator;
import com.alesharik.webserver.api.server.wrapper.bundle.impl.BasicErrorHandler;
import com.alesharik.webserver.api.server.wrapper.bundle.impl.BasicFilterChain;
import com.alesharik.webserver.api.server.wrapper.http.util.IpBanManager;

import java.util.concurrent.TimeUnit;

@HttpBundle(value = "_localstorage-main-http-bundle", condition = ConditionImpl.class)
public final class MainHttpHandlerBundle implements HttpHandlerBundle {
    private final DataManager dataManager;
    private final Database database;
    private final IpBanManager ipBanManager = new IpBanManager(30, TimeUnit.MINUTES);

    private final TokenFilter tokenFilter;
    private final TokenHolder tokenHolder;

    public MainHttpHandlerBundle(DataManager dataManager, Database database) {
        this.dataManager = dataManager;
        this.database = database;

        tokenHolder = new TokenHolder();
        tokenFilter = new TokenFilter(tokenHolder);
    }

    @Override
    public Validator getValidator() {
        return new MainDataHandlerBundleValidator();
    }

    @Override
    public RequestRouter getRouter() {
        return new MainHttpHandlerBundleRequestRouter();
    }

    @Override
    public FilterChain[] getFilterChains() {
        return new FilterChain[] {new AuthorizationChain(ipBanManager), new BasicFilterChain()
        .with(ipBanManager)
        .with(tokenFilter)};
    }

    @Override
    public HttpHandler[] getHttpHandlers() {//Zero element MUST be AuthorizationHandler
        return new HttpHandler[] {
                new AuthorizationHandler(tokenHolder, dataManager, ipBanManager),
                new HttpMeHandler(tokenHolder),
                new HttpWhoHandler(dataManager),
                new HttpGetChatStatusHandler(dataManager, tokenHolder),
                new HttpSyncChatStatusHandler(database, tokenHolder, dataManager)
        };
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return new BasicErrorHandler();
    }
}
