package com.alesharik.localstorage.version;

import com.alesharik.webserver.configuration.Layer;
import com.alesharik.webserver.configuration.Module;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class VersioningModule implements Module {
    @Override
    public void parse(@Nullable Element element) {

    }

    @Override
    public void reload(@Nullable Element element) {

    }

    @Override
    public void start() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public void shutdownNow() {

    }

    @Nonnull
    @Override
    public String getName() {
        return "local-storage-versioning";
    }

    @Nullable
    @Override
    public Layer getMainLayer() {
        return null;
    }
}
