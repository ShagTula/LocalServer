package com.alesharik.localstorage.main;

import com.alesharik.database.Database;
import com.alesharik.database.driver.postgres.PostgresDriver;
import com.alesharik.localstorage.main.data.DataManager;
import com.alesharik.localstorage.main.http.MainHttpHandlerBundle;
import com.alesharik.webserver.configuration.Layer;
import com.alesharik.webserver.configuration.Module;
import com.alesharik.webserver.configuration.XmlHelper;
import com.alesharik.webserver.exceptions.error.ConfigurationParseError;
import lombok.Getter;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;

import static com.alesharik.webserver.configuration.XmlHelper.getString;
import static com.alesharik.webserver.configuration.XmlHelper.getXmlElement;

public class LocalStorageModule implements Module {//TODO file model
    private volatile Database database;
    @Getter
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

        try {
            assert host != null;
            database = Database.newDatabase(host, login, password, new PostgresDriver(), true);
        } catch (SQLException e) {
            throw new ConfigurationParseError(e);
        }
        dataManager = new DataManager(database, "local_storage");

        //noinspection ConstantConditions
        XmlHelper.getHttpServer("http-server", element, true).addHttpHandlerBundle(new MainHttpHandlerBundle(dataManager, database));
    }

    @Override
    public void reload(@Nullable Element element) {
        shutdown();
        parse(element);
        start();
    }

    @Override
    public void start() {
        //Database already started
    }

    @Override
    public void shutdown() {
        try {
            database.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdownNow() {
        try {
            database.getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
