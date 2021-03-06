/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 NBCO Yandex.Money LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.yandex.money.api.typeadapters.showcase;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.yandex.money.api.model.showcase.Showcase;
import com.yandex.money.api.model.showcase.Showcase.Error;
import com.yandex.money.api.typeadapters.AllowedMoneySourceTypeAdapter;
import com.yandex.money.api.typeadapters.BaseTypeAdapter;
import com.yandex.money.api.typeadapters.showcase.container.GroupTypeAdapter;
import com.yandex.money.api.typeadapters.showcase.container.GroupTypeAdapter.ListDelegate;

import java.lang.reflect.Type;
import java.util.LinkedHashSet;

import static com.yandex.money.api.typeadapters.JsonUtils.getMandatoryString;
import static com.yandex.money.api.typeadapters.JsonUtils.getNotNullArray;
import static com.yandex.money.api.typeadapters.JsonUtils.getNotNullMap;
import static com.yandex.money.api.typeadapters.JsonUtils.getString;
import static com.yandex.money.api.typeadapters.JsonUtils.toJsonArray;
import static com.yandex.money.api.typeadapters.JsonUtils.toJsonObject;

/**
 * Type adapter for {@link Showcase}.
 *
 * @author Anton Ermak (ermak@yamoney.ru)
 */
public final class ShowcaseTypeAdapter extends BaseTypeAdapter<Showcase> {

    private static final ShowcaseTypeAdapter INSTANCE = new ShowcaseTypeAdapter();

    private static final String MEMBER_ERROR = "error";
    private static final String MEMBER_FORM = "form";
    private static final String MEMBER_HIDDEN_FIELDS = "hidden_fields";
    private static final String MEMBER_MONEY_SOURCE = "money_source";
    private static final String MEMBER_TITLE = "title";

    private ShowcaseTypeAdapter() {
        // register type adapters to GSON instance.
        GroupTypeAdapter.getInstance();
    }

    /**
     * @return instance of this class
     */
    public static ShowcaseTypeAdapter getInstance() {
        return INSTANCE;
    }

    @Override
    public Showcase deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject object = json.getAsJsonObject();
        return new Showcase.Builder()
                .setTitle(getMandatoryString(object, MEMBER_TITLE))
                .setHiddenFields(getNotNullMap(object, MEMBER_HIDDEN_FIELDS))
                .setForm(ListDelegate.deserialize(object.getAsJsonArray(MEMBER_FORM), context))
                .setMoneySources(new LinkedHashSet<>(getNotNullArray(object, MEMBER_MONEY_SOURCE,
                        AllowedMoneySourceTypeAdapter.getInstance())))
                .setErrors(getNotNullArray(object, MEMBER_ERROR, ErrorTypeAdapter.getInstance()))
                .create();
    }

    @Override
    public JsonElement serialize(Showcase src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject objects = new JsonObject();
        objects.addProperty(MEMBER_TITLE, src.title);
        objects.add(MEMBER_MONEY_SOURCE, toJsonArray(src.moneySources, AllowedMoneySourceTypeAdapter.getInstance()));
        if (!src.errors.isEmpty()) {
            objects.add(MEMBER_ERROR, toJsonArray(src.errors, ErrorTypeAdapter.getInstance()));
        }
        objects.add(MEMBER_FORM, ListDelegate.serialize(src.form, context));
        objects.add(MEMBER_HIDDEN_FIELDS, toJsonObject(src.hiddenFields));
        return objects;
    }

    @Override
    protected Class<Showcase> getType() {
        return Showcase.class;
    }

    private static final class ErrorTypeAdapter extends BaseTypeAdapter<Showcase.Error> {

        private static final ErrorTypeAdapter INSTANCE = new ErrorTypeAdapter();

        private static final String MEMBER_ALERT = "alert";
        private static final String MEMBER_NAME = "name";

        public static ErrorTypeAdapter getInstance() {
            return INSTANCE;
        }

        @Override
        public Error deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            JsonObject jsonObject = json.getAsJsonObject();
            return new Error(getString(jsonObject, MEMBER_NAME),
                    getMandatoryString(jsonObject, MEMBER_ALERT));
        }

        @Override
        public JsonElement serialize(Error src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(MEMBER_NAME, src.name);
            jsonObject.addProperty(MEMBER_ALERT, src.alert);
            return jsonObject;
        }

        @Override
        protected Class<Error> getType() {
            return Showcase.Error.class;
        }
    }
}
