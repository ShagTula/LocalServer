package com.alesharik.localstorage.version.http;

import com.alesharik.localstorage.version.Version;
import com.alesharik.localstorage.version.VersionList;
import com.alesharik.webserver.api.server.wrapper.bundle.impl.file.FileContentProvider;
import com.alesharik.webserver.api.server.wrapper.http.Request;
import com.alesharik.webserver.api.server.wrapper.http.data.MimeType;

import javax.annotation.Nullable;
import java.io.IOException;

final class VersionFileContentProvider implements FileContentProvider {
    private static final MimeType MIME_TYPE = new MimeType("application", "vnd.microsoft.portable-executable");

    private final VersionList versionList;

    public VersionFileContentProvider(VersionList versionList) {
        this.versionList = versionList;
    }

    @Override
    public boolean hasFile(Request request) {
        Version fake = getFakeVersion(request);
        return fake != null && versionList.contains(fake);
    }

    @Override
    public String getName(Request request) {
        Version fake = getFakeVersion(request);
        return fake != null ? fake.toVersionString() : "";
    }

    @Override
    public long getLength(Request request) {
        Version fake = getFakeVersion(request);
        Version real = versionList.getVersion(fake);
        try {
            if(real == null)
                return 0;
            return real.getRandomAccessFile().length();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public byte[] getRangedData(Request request, long start, long end) {
        Version fake = getFakeVersion(request);
        Version real = versionList.getVersion(fake);
        try {
            if(real == null)
                return new byte[0];
            if(real.getRandomAccessFile().length() < end)
                return new byte[0];
            byte[] ret = new byte[(int) (end - start)];
            real.getRandomAccessFile().seek(start);
            real.getRandomAccessFile().readFully(ret);
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public MimeType getMimeType(Request request) {
        return MIME_TYPE;
    }

    @Nullable
    private Version getFakeVersion(Request request) {
        String versionString = request.getParameter("version");
        if(versionString == null)
            return null;

        return Version.fromVersionString(versionString);
    }
}
