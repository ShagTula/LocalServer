package com.alesharik.localstorage.data;

import com.alesharik.database.Database;
import com.alesharik.database.EntityManager;
import com.alesharik.database.Schema;
import com.alesharik.database.Table;
import com.alesharik.database.entity.TypeTranslator;
import com.alesharik.database.postgres.PostgresDriver;
import com.alesharik.localstorage.data.status.ChatStatus;
import com.alesharik.localstorage.data.status.InfoStatus;
import com.alesharik.localstorage.data.status.PrivateStatus;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class DataManager {
    private static final TypeTranslator TYPE_TRANSLATOR = new PostgresDriver();

    public static final String USER_TABLE_NAME = "users";
    public static final String CATEGORY_TABLE_NAME = "categories";
    public static final String MULTIPART_NODE_TABLE_NAME = "multipart_notes";
    public static final String MULTIPART_NODE_PART_TABLE_NAME = "multipart_note_parts";
    public static final String CHAT_STATUS_TABLE_NAME = "chat_statuses";
    public static final String INFO_STATUS_TABLE_NAME = "info_statuses";
    public static final String PRIVATE_STATUS_TABLE_NAME = "private_statuses";

    private final Schema schema;
    private final Database database;

    @Getter
    private final Table<User> userTable;
    @Getter
    private final Table<ChatStatus> chatStatusTable;
    @Getter
    private final Table<InfoStatus> infoStatusTable;
    @Getter
    private final Table<PrivateStatus> privateStatusTable;
    @Getter
    private final Table<Category> categoryTable;
    @Getter
    private final Table<MultipartNote> multipartNoteTable;
    @Getter
    private final Table<MultipartNotePart> multipartNotePartTable;

    public DataManager(Database database, String schemaName) {
        this.database = database;
        if(schemaName.isEmpty())
            throw new IllegalArgumentException("DataManager must have own scheme!");

        try {
            database.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.schema = database.getSchema(schemaName, true);

        Table<ChatStatus> chatStatusTable = schema.getTable(CHAT_STATUS_TABLE_NAME, ChatStatus.class);
        if(chatStatusTable == null)
            chatStatusTable = database.createTable(schemaName + '.' + CHAT_STATUS_TABLE_NAME, ChatStatus.class);
        this.chatStatusTable = chatStatusTable;

        Table<InfoStatus> infoStatusTable = schema.getTable(INFO_STATUS_TABLE_NAME, InfoStatus.class);
        if(infoStatusTable == null)
            infoStatusTable = database.createTable(schemaName + '.' + INFO_STATUS_TABLE_NAME, InfoStatus.class);
        this.infoStatusTable = infoStatusTable;

        Table<PrivateStatus> privateStatusTable = schema.getTable(PRIVATE_STATUS_TABLE_NAME, PrivateStatus.class);
        if(privateStatusTable == null)
            privateStatusTable = database.createTable(schemaName + '.' + PRIVATE_STATUS_TABLE_NAME, PrivateStatus.class);
        this.privateStatusTable = privateStatusTable;

        Table<User> userTable = schema.getTable(USER_TABLE_NAME, User.class);
        if(userTable == null)
            userTable = database.createTable(schemaName + '.' + USER_TABLE_NAME, User.class);
        this.userTable = userTable;

        Table<Category> categoryTable = schema.getTable(CATEGORY_TABLE_NAME, Category.class);
        if(categoryTable == null)
            categoryTable = database.createTable(schemaName + '.' + CATEGORY_TABLE_NAME, Category.class);
        this.categoryTable = categoryTable;

        Table<MultipartNotePart> multipartNotePartTable = schema.getTable(MULTIPART_NODE_PART_TABLE_NAME, MultipartNotePart.class);
        if(multipartNotePartTable == null)
            multipartNotePartTable = database.createTable(schemaName + '.' + MULTIPART_NODE_PART_TABLE_NAME, MultipartNotePart.class);
        this.multipartNotePartTable = multipartNotePartTable;

        Table<MultipartNote> multipartNoteTable = schema.getTable(MULTIPART_NODE_TABLE_NAME, MultipartNote.class);
        if(multipartNoteTable == null)
            multipartNoteTable = database.createTable(schemaName + '.' + MULTIPART_NODE_TABLE_NAME, MultipartNote.class);
        this.multipartNoteTable = multipartNoteTable;

        try {
            database.getConnection().setAutoCommit(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkUser(String login, String password) {
        Boolean result = database.executeTransaction(() -> {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = database.getConnection().prepareStatement("SELECT * FROM " + schema.getName() + '.' + USER_TABLE_NAME + " WHERE login = ?");
                preparedStatement.setString(1, login);
                resultSet = preparedStatement.executeQuery();
                if(!resultSet.next())
                    return false;
                User user = EntityManager.parseEntity(resultSet, TYPE_TRANSLATOR, User.class);

                return user.passwordValid(password);
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            } finally {
                if(resultSet != null)
                    resultSet.close();
                if(preparedStatement != null)
                    preparedStatement.close();
            }
        });
        return result == null ? false : result;
    }

    public User newUser(String login, String password) {
        return database.executeTransaction(() -> {
            UUID id = UUID.randomUUID();
            User user = new User(id, login, password);

            user.setData(new JsonObject());

            ChatStatus chatStatus = newChatStatus(login);
            user.setChatStatus(chatStatus.getId());

            InfoStatus infoStatus = newInfoStatus();
            user.setInfoStatus(infoStatus.getId());

            PrivateStatus privateStatus = newPrivateStatus();
            user.setPrivateStatus(privateStatus.getId());

            userTable.add(user);

            return user;
        });
    }

    public ChatStatus newChatStatus(String nick) {
        return database.executeTransaction(() -> {
            UUID uuid = UUID.randomUUID();
            ChatStatus chatStatus = new ChatStatus(uuid);
            chatStatus.setNickName(nick);
            chatStatus.setAvatarUrl("/avatar/none.png");
            chatStatus.setData(new JsonObject());
            chatStatus.setOnlineStatus(ChatStatus.OnlineStatus.OFFLINE);

            chatStatusTable.add(chatStatus);

            return chatStatus;
        });
    }

    public PrivateStatus newPrivateStatus() {
        return database.executeTransaction(() -> {
            UUID uuid = UUID.randomUUID();
            PrivateStatus privateStatus = new PrivateStatus(uuid);
            privateStatus.setData(new JsonObject());
            privateStatus.setPhone("");

            privateStatusTable.add(privateStatus);

            return privateStatus;
        });
    }

    public InfoStatus newInfoStatus() {
        return database.executeTransaction(() -> {
            UUID uuid = UUID.randomUUID();
            InfoStatus infoStatus = new InfoStatus(uuid);

            infoStatus.setFirstName("");
            infoStatus.setLastName("");
            infoStatus.setPatronymic("");
            infoStatus.setBirthday(new Date(0, 0, 0));
            infoStatus.setData(new JsonObject());

            infoStatusTable.add(infoStatus);

            return infoStatus;
        });
    }
}
