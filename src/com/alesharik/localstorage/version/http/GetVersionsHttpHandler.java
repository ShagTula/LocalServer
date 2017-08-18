package com.alesharik.localstorage.version.http;

import com.alesharik.localstorage.version.Version;
import com.alesharik.localstorage.version.VersionList;
import com.alesharik.webserver.api.server.wrapper.bundle.FilteredHttpHandler;
import com.alesharik.webserver.api.server.wrapper.http.HttpStatus;
import com.alesharik.webserver.api.server.wrapper.http.Request;
import com.alesharik.webserver.api.server.wrapper.http.Response;
import com.alesharik.webserver.api.server.wrapper.http.data.MimeType;
import org.glassfish.grizzly.utils.Charsets;

final class GetVersionsHttpHandler implements FilteredHttpHandler {
    private static final MimeType TEXT_PLAIN = MimeType.parseType("text/plain");

    private final VersionList versionList;

    public GetVersionsHttpHandler(VersionList versionList) {
        this.versionList = versionList;
    }

    @Override
    public boolean accept(Request request, Response response) {
        return request.getContextPath().equals("/update/versions");
    }

    @Override
    public void handle(Request request, Response response) {
        StringBuilder ret = new StringBuilder();
        boolean notFirst = false;
        for(Version version : versionList.getVersions()) {
            if(notFirst) {
                ret.append(", ");
            } else {//First
                notFirst = true;
            }

            ret.append(version.toVersionString());
        }
        response.respond(HttpStatus.OK_200);
        String s = ret.toString();
        response.setType(TEXT_PLAIN, Charsets.UTF8_CHARSET);
        response.getWriter().write(s);
        response.setContentLength(s.length());
    }
}
