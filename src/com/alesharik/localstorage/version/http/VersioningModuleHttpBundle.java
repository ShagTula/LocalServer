package com.alesharik.localstorage.version.http;

import com.alesharik.localstorage.version.VersionList;
import com.alesharik.webserver.api.server.wrapper.bundle.ErrorHandler;
import com.alesharik.webserver.api.server.wrapper.bundle.FilterChain;
import com.alesharik.webserver.api.server.wrapper.bundle.HttpBundle;
import com.alesharik.webserver.api.server.wrapper.bundle.HttpHandler;
import com.alesharik.webserver.api.server.wrapper.bundle.HttpHandlerBundle;
import com.alesharik.webserver.api.server.wrapper.bundle.HttpHandlerResponseDecorator;
import com.alesharik.webserver.api.server.wrapper.bundle.RequestRouter;
import com.alesharik.webserver.api.server.wrapper.bundle.Validator;
import com.alesharik.webserver.api.server.wrapper.bundle.impl.BasicErrorHandler;
import com.alesharik.webserver.api.server.wrapper.bundle.impl.BasicFilterChain;
import com.alesharik.webserver.api.server.wrapper.bundle.impl.OneChainRequestRouter;
import com.alesharik.webserver.api.server.wrapper.bundle.impl.file.RangeFileHttpProvider;
import com.alesharik.webserver.api.server.wrapper.http.HttpStatus;

@HttpBundle(value = "localstorage-versioning-http-bundle", condition = VersioningModuleHttpBundleCondition.class)
public final class VersioningModuleHttpBundle implements HttpHandlerBundle {
    private static final RequestRouter requestRouter = new OneChainRequestRouter();
    private static final ErrorHandler errorHandler = new BasicErrorHandler();
    private static final FilterChain[] chains = new FilterChain[] {new BasicFilterChain()};

    private final GetVersionsHttpHandler getVersionsHttpHandler;
    private final RangeFileHttpProvider versionHttpHandler;
    private final VersionFileContentProvider versionFileContentProvider;

    public VersioningModuleHttpBundle(VersionList versionList) {
        getVersionsHttpHandler = new GetVersionsHttpHandler(versionList);
        versionFileContentProvider = new VersionFileContentProvider(versionList);
        versionHttpHandler = new RangeFileHttpProvider(versionFileContentProvider);
    }

    @Override
    public Validator getValidator() {
        return request -> request.getContextPath().startsWith("/update/");
    }

    @Override
    public RequestRouter getRouter() {
        return requestRouter;
    }

    @Override
    public FilterChain[] getFilterChains() {
        return chains;
    }

    @Override
    public HttpHandler[] getHttpHandlers() {
        return new HttpHandler[] {
                getVersionsHttpHandler,
                versionHttpHandler
        };
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public HttpHandlerResponseDecorator getReponseDecorator() {
        return (request, response, b) -> {
            if(request.getContextPath().equals("/update/get") && !versionFileContentProvider.hasFile(request))
                response.respond(HttpStatus.NOT_FOUND_404);
        };
    }
}
