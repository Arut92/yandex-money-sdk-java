package com.yandex.money.model.cps.misc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DigitalGoods {

    private final List<Good> article;
    private final List<Good> bonus;

    public DigitalGoods(List<Good> article, List<Good> bonus) {
        if (article == null) {
            throw new NullPointerException("article is null");
        }
        this.article = article;
        if (bonus == null) {
            throw new NullPointerException("bonus is null");
        }
        this.bonus = bonus;
    }

    public static DigitalGoods createFromJson(JsonElement element) {
        return buildGson().fromJson(element, DigitalGoods.class);
    }

    public List<Good> getArticle() {
        return article;
    }

    public List<Good> getBonus() {
        return bonus;
    }

    private static Gson buildGson() {
        return new GsonBuilder()
                .registerTypeAdapter(DigitalGoods.class, new Deserializer())
                .create();
    }

    private static final class Deserializer implements JsonDeserializer<DigitalGoods> {
        @Override
        public DigitalGoods deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context)
                throws JsonParseException {

            JsonObject object = json.getAsJsonObject();
            return new DigitalGoods(deserializeGoods(object.getAsJsonArray("article")),
                    deserializeGoods(object.getAsJsonArray("bonus")));
        }

        private List<Good> deserializeGoods(JsonArray array) {
            List<Good> goods = new ArrayList<>();
            if (array != null) {
                for (JsonElement element : array) {
                    goods.add(Good.createFromJson(element));
                }
            }
            return goods;
        }
    }
}
