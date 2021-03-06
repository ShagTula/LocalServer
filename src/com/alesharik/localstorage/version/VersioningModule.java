package com.alesharik.localstorage.version;

import com.alesharik.localstorage.version.http.VersioningModuleHttpBundle;
import com.alesharik.webserver.api.server.wrapper.server.HttpServer;
import com.alesharik.webserver.configuration.Layer;
import com.alesharik.webserver.configuration.Module;
import com.alesharik.webserver.configuration.SubModule;
import com.alesharik.webserver.configuration.XmlHelper;
import com.alesharik.webserver.exceptions.error.ConfigurationParseError;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;
import java.util.List;

public final class VersioningModule implements Module {
    private volatile MainLayer layer;
    private volatile VersionScannerThread scannerThread;
    @Getter
    private volatile VersionList versionList;

    @Override
    public void parse(@Nullable Element element) {
        if(element == null)
            throw new ConfigurationParseError();

        versionList = new VersionList();
        File scanFolder = XmlHelper.getFile("folder", element, true);
        scannerThread = new VersionScannerThread(scanFolder, versionList);

        HttpServer httpServer = XmlHelper.getHttpServer("http-server", element, true);
        assert httpServer != null;
        httpServer.addHttpHandlerBundle(new VersioningModuleHttpBundle(versionList));

        layer = new MainLayer(scannerThread);
    }

    @Override
    public void reload(@Nullable Element element) {
        shutdown();
        parse(element);
        start();
    }

    @Override
    public void start() {
        scannerThread.start();
    }

    @Override
    public void shutdown() {
        scannerThread.shutdown();
    }

    @Override
    public void shutdownNow() {
        scannerThread.shutdown();
    }

    @Nonnull
    @Override
    public String getName() {
        return "local-storage-versioning";
    }

    @Nullable
    @Override
    public Layer getMainLayer() {
        return layer;
    }

    static final class MainLayer implements Layer {
        private final List<SubModule> singletonList;

        MainLayer(VersionScannerThread thread) {
            singletonList = Collections.singletonList(thread);
        }

        @Nonnull
        @Override
        public List<SubModule> getSubModules() {
            return singletonList;
        }

        @Nonnull
        @Override
        public List<Layer> getSubLayers() {
            return Collections.emptyList();
        }

        @Nonnull
        @Override
        public String getName() {
            return "main";
        }
    }
}
