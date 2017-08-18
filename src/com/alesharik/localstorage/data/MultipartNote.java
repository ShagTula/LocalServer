package com.alesharik.localstorage.data;

import com.alesharik.database.entity.Column;
import com.alesharik.database.entity.Creator;
import com.alesharik.database.entity.Destroyer;
import com.alesharik.database.entity.Entity;
import com.alesharik.database.entity.EntityManager;
import com.alesharik.database.entity.ForeignKey;
import com.alesharik.database.entity.Indexed;
import com.alesharik.database.entity.Lazy;
import com.alesharik.database.entity.OverrideDomain;
import com.alesharik.database.entity.PrimaryKey;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("NullableProblems")
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Entity
@Lazy
public final class MultipartNote {
    @Getter
    @Column("id")
    @PrimaryKey
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
    @Column("category")
    @Indexed
    @ForeignKey("local_storage." + DataManager.CATEGORY_TABLE_NAME)
    @Nonnull
    private UUID category;

    @Getter
    @Setter
    @Column("viewers")
    @ForeignKey("local_storage." + DataManager.USER_TABLE_NAME)
    private Collection<UUID> viewers;

    @Getter
    @Setter
    @ForeignKey("local_storage." + DataManager.USER_TABLE_NAME)
    private Collection<UUID> collaborators;

    @Getter
    @Setter
    @Column("owner")
    @ForeignKey("local_storage." + DataManager.USER_TABLE_NAME)
    @Indexed
    @Nonnull
    private UUID owner;

    @Getter
    @Setter
    @Column("parts")
    private Collection<UUID> parts;

    /**
     * Strings are actually json objects
     */
    @Getter
    @Setter
    @Column("history")
    private Collection<String> history;

    public MultipartNote(UUID id) {
        this.id = id;
    }

    @Creator
    public static MultipartNote create(EntityManager<MultipartNote> entityManager, String name, Category category, User owner) {
        MultipartNote multipartNote = new MultipartNote(UUID.randomUUID());
        multipartNote.name = name;
        multipartNote.description = "";
        multipartNote.isPublic = false;
        multipartNote.category = category.getId();
        multipartNote.viewers = new CopyOnWriteArrayList<>();
        multipartNote.collaborators = new CopyOnWriteArrayList<>();
        multipartNote.owner = owner.getId();
        multipartNote.parts = new CopyOnWriteArrayList<>();
        multipartNote.history = new CopyOnWriteArrayList<>();
        return multipartNote;
    }

    @Destroyer
    public void delete() {}
}
