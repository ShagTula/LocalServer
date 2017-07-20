package com.alesharik.localstorage.data;

import com.alesharik.database.Database;
import com.alesharik.database.Schema;
import com.alesharik.database.Table;
import com.alesharik.localstorage.data.status.ChatStatus;
import com.alesharik.localstorage.data.status.InfoStatus;
import com.alesharik.localstorage.data.status.PrivateStatus;
import lombok.Getter;

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
}
