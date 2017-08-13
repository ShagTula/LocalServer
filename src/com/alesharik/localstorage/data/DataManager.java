package com.alesharik.localstorage.data;

import com.alesharik.database.Database;
import com.alesharik.database.data.EntityPreparedStatement;
import com.alesharik.database.data.Schema;
import com.alesharik.database.data.Table;
import com.alesharik.localstorage.data.status.ChatStatus;
import com.alesharik.localstorage.data.status.InfoStatus;
import com.alesharik.localstorage.data.status.PrivateStatus;
import lombok.Getter;

import javax.annotation.Nullable;
import java.sql.SQLException;

public final class DataManager {
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

        this.schema = database.getSchema(schemaName, true);

        this.chatStatusTable = schema.getTable(CHAT_STATUS_TABLE_NAME, true, ChatStatus.class);
        this.infoStatusTable = schema.getTable(INFO_STATUS_TABLE_NAME, true, InfoStatus.class);
        this.privateStatusTable = schema.getTable(PRIVATE_STATUS_TABLE_NAME, true, PrivateStatus.class);
        this.userTable = schema.getTable(USER_TABLE_NAME, true, User.class);
        this.categoryTable = schema.getTable(CATEGORY_TABLE_NAME, true, Category.class);
        this.multipartNotePartTable = schema.getTable(MULTIPART_NODE_PART_TABLE_NAME, true, MultipartNotePart.class);
        this.multipartNoteTable = schema.getTable(MULTIPART_NODE_TABLE_NAME, true, MultipartNote.class);
    }

    public boolean checkUser(String login, String password) {
        try {
            EntityPreparedStatement<User> preparedStatement = null;
            try {
                preparedStatement = userTable.prepareStatement("SELECT * FROM " + schema.getName() + '.' + USER_TABLE_NAME + " WHERE login = ?");
                preparedStatement.setString(1, login);
                for(User user : preparedStatement.executeEntityQuery()) {
                    if(user.passwordValid(password))
                        return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if(preparedStatement != null)
                    preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Nullable
    public User getUserForLogPass(String login, String pass) {
        try {
            EntityPreparedStatement<User> preparedStatement = null;
            try {
                preparedStatement = userTable.prepareStatement("SELECT * FROM " + schema.getName() + '.' + USER_TABLE_NAME + " WHERE login = ?");
                preparedStatement.setObject(1, login);
                for(User user : preparedStatement.executeEntityQuery()) {
                    if(user.passwordValid(pass))
                        return user;
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            } finally {
                if(preparedStatement != null) {
                    preparedStatement.close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
     }
}
