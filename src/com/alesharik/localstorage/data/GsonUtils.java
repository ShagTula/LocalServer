package com.alesharik.localstorage.data;

import com.alesharik.localstorage.data.status.ChatStatus;
import com.alesharik.localstorage.data.status.InfoStatus;
import com.alesharik.localstorage.data.status.PrivateStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Type;
import java.sql.Date;
import java.util.UUID;

@UtilityClass
public class GsonUtils {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDSerializer())
            .registerTypeAdapter(UUID.class, new UUIDDeserializer())
            .registerTypeAdapter(User.class, new UserSerializer())
            .registerTypeAdapter(User.class, new UserDeserializer())
            .registerTypeAdapter(ChatStatus.class, new ChatStatusDeserializer())
            .registerTypeAdapter(ChatStatus.class, new ChatStatusSerializer())
            .registerTypeAdapter(InfoStatus.class, new InfoStatusDeserializer())
            .registerTypeAdapter(InfoStatus.class, new InfoStatusSerializer())
            .registerTypeAdapter(PrivateStatus.class, new PrivateStatusDeserializer())
            .registerTypeAdapter(PrivateStatus.class, new PrivateStatusSerializer())
            .create();

    public static Gson getGson() {
        return GSON;
    }

    private static final class UUIDSerializer implements JsonSerializer<UUID> {

        @Override
        public JsonElement serialize(UUID src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }

    private static final class UUIDDeserializer implements JsonDeserializer<UUID> {

        @Override
        public UUID deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return UUID.fromString(json.getAsString());
        }
    }

    private static final class UserSerializer implements JsonSerializer<User> {

        @Override
        public JsonElement serialize(User src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("id", context.serialize(src.getId()));
            jsonObject.add("login", new JsonPrimitive(src.getLogin()));
            jsonObject.add("data", src.getData());
            jsonObject.add("infoStatus", context.serialize(src.getInfoStatus()));
            jsonObject.add("chatStatus", context.serialize(src.getChatStatus()));
            jsonObject.add("privateStatus", context.serialize(src.getPrivateStatus()));
            return jsonObject;
        }
    }

    private static final class UserDeserializer implements JsonDeserializer<User> {

        @Override
        public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            User user = new User(context.deserialize(jsonObject.get("id"), UUID.class));
            user.setLogin(jsonObject.get("login").getAsString());
            user.setData(jsonObject.get("data").getAsJsonObject());
            user.setPrivateStatus(context.deserialize(jsonObject.get("privateStatus"), UUID.class));
            user.setInfoStatus(context.deserialize(jsonObject.get("infoStatus"), UUID.class));
            user.setChatStatus(context.deserialize(jsonObject.get("chatStatus"), UUID.class));
            return user;
        }
    }

    private static final class ChatStatusSerializer implements JsonSerializer<ChatStatus> {

        @Override
        public JsonElement serialize(ChatStatus src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("id", context.serialize(src.getId()));
            jsonObject.add("nickName", new JsonPrimitive(src.getNickName()));
            jsonObject.add("avatarUrl", new JsonPrimitive(src.getAvatarUrl()));
            jsonObject.add("data", src.getData());
            jsonObject.add("onlineStatus", new JsonPrimitive(src.getStatus().getState()));
            return jsonObject;
        }
    }

    private static final class ChatStatusDeserializer implements JsonDeserializer<ChatStatus> {

        @Override
        public ChatStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            ChatStatus chatStatus = new ChatStatus(context.deserialize(object.get("id"), UUID.class));
            chatStatus.setNickName(object.get("nickName").getAsString());
            chatStatus.setAvatarUrl(object.get("avatarUrl").getAsString());
            chatStatus.setData(object.get("data").getAsJsonObject());
            chatStatus.setOnlineStatus(ChatStatus.OnlineStatus.forState(object.get("onlineStatus").getAsInt()));
            return chatStatus;
        }
    }

    private static final class InfoStatusSerializer implements JsonSerializer<InfoStatus> {

        @Override
        public JsonElement serialize(InfoStatus src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add("id", context.serialize(src.getId()));
            object.add("firstName", new JsonPrimitive(src.getFirstName()));
            object.add("lastName", new JsonPrimitive(src.getLastName()));
            object.add("patronymic", new JsonPrimitive(src.getPatronymic()));
            object.add("birthDate", new JsonPrimitive(src.getBirthday().toString()));
            object.add("data", src.getData());
            return object;
        }
    }

    private static final class InfoStatusDeserializer implements JsonDeserializer<InfoStatus> {

        @Override
        public InfoStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            InfoStatus infoStatus = new InfoStatus(context.deserialize(object.get("id"), UUID.class));
            infoStatus.setFirstName(object.get("firstName").getAsString());
            infoStatus.setLastName(object.get("lastName").getAsString());
            infoStatus.setPatronymic(object.get("patronymic").getAsString());
            infoStatus.setBirthday(Date.valueOf(object.get("birthDate").getAsString()));
            infoStatus.setData(object.get("data").getAsJsonObject());
            return infoStatus;
        }
    }

    private static final class PrivateStatusSerializer implements JsonSerializer<PrivateStatus> {

        @Override
        public JsonElement serialize(PrivateStatus src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject object = new JsonObject();
            object.add("id", context.serialize(src.getId()));
            object.add("phone", new JsonPrimitive(src.getPhone()));
            object.add("data", src.getData());
            return object;
        }
    }

    private static final class PrivateStatusDeserializer implements JsonDeserializer<PrivateStatus> {

        @Override
        public PrivateStatus deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();
            PrivateStatus privateStatus = new PrivateStatus(context.deserialize(object.get("id"), UUID.class));
            privateStatus.setPhone(object.get("phone").getAsString());
            privateStatus.setData(object.get("data").getAsJsonObject());
            return privateStatus;
        }
    }
}
