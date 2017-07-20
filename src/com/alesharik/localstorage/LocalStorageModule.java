package com.alesharik.localstorage;

import com.alesharik.database.Database;
import com.alesharik.database.exception.DatabaseConnectionFailedException;
import com.alesharik.database.postgres.PostgresDriver;
import com.alesharik.localstorage.data.DataManager;
import com.alesharik.webserver.configuration.Layer;
import com.alesharik.webserver.configuration.Module;
import com.alesharik.webserver.exceptions.error.ConfigurationParseError;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.alesharik.webserver.configuration.XmlHelper.getString;
import static com.alesharik.webserver.configuration.XmlHelper.getXmlElement;

public class LocalStorageModule implements Module {//TODO file model
    private volatile Database database;
    private volatile DataManager dataManager;

    @Override
    public void parse(@Nullable Element element) {
        if(element == null)
            throw new ConfigurationParseError();

        Element dbConfig = getXmlElement("database", element, true);
        assert dbConfig != null;
        String host = getString("host", dbConfig, true);
        String login = getString("login", dbConfig, true);
        String password = getString("password", dbConfig, true);
        String schema = getString("schema", dbConfig, true);

        database = new Database(host, login, password, new PostgresDriver(), true);
        try {
            database.connect();
        } catch (DatabaseConnectionFailedException e) {
            throw new ConfigurationParseError(e);
        }
        dataManager = new DataManager(database, schema);
    }

    @Override
    public void reload(@Nullable Element element) {
        shutdown();
        parse(element);
        start();
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
        return "local-storage";
    }

    @Nullable
    @Override
    public Layer getMainLayer() {
        return null;
    }
}
