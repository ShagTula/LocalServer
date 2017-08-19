package com.alesharik.localstorage.main.data;

import com.alesharik.database.data.EntityPreparedStatement;
import com.alesharik.database.data.Table;
import com.alesharik.database.entity.Column;
import com.alesharik.database.entity.Creator;
import com.alesharik.database.entity.Destroyer;
import com.alesharik.database.entity.Entity;
import com.alesharik.database.entity.EntityManager;
import com.alesharik.database.entity.ForeignKey;
import com.alesharik.database.entity.Indexed;
import com.alesharik.database.entity.OverrideDomain;
import com.alesharik.database.entity.PrimaryKey;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("NullableProblems")
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Entity
public final class Category {
    @Getter
    @Column("id")
    @PrimaryKey
    @Nonnull
    private final UUID id;

    @Getter
    @Setter
    @Column("name")
    @OverrideDomain("varchar(50)")
    @Nonnull
    private String name;

    @Getter
    @Setter
    @Column("description")
    @Nonnull
    private String description;

    @Getter
    @Setter
    @Column("public")
    @Indexed
    @Nonnull
    private Boolean isPublic;

    @Getter
    @Setter
    @Column("data")
    @Nonnull
    private JsonObject data;

    @Getter
    @Setter
    @Column("parent")
    @Nullable
    @Indexed
    private UUID parent;

    @Getter
    @Setter
    @Column("users")
    @Indexed
    @Nonnull
    private Collection<UUID> users;

    @Getter
    @Setter
    @Column("creator")
    @Indexed
    @ForeignKey("local_storage." + DataManager.USER_TABLE_NAME)
    @Nonnull
    private UUID creator;

    public Category(UUID id) {
        this.id = id;
    }

    @Creator
    public static Category create(EntityManager<Category> entityManager, String name, UUID creator, @Nullable UUID parent) {
        Category category = new Category(UUID.randomUUID());
        category.name = name;
        category.description = "";
        category.isPublic = false;
        category.data = new JsonObject();
        category.creator = creator;
        category.parent = parent;
        category.users = new CopyOnWriteArrayList<>();
        return category;
    }

    @Destroyer
    public void delete(Table<Category> categoryTable) {
        try {
            EntityPreparedStatement<Category> preparedStatement = categoryTable.prepareStatement("SELECT * FROM " + categoryTable.getSchema().getName() + '.' + categoryTable.getName() + " WHERE parent = ?");
            preparedStatement.setObject(1, parent);
            for(Category category : preparedStatement.executeEntityQuery()) {
                category.delete(categoryTable);
            }
            users.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
