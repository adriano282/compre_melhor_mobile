package br.com.compremelhor.api.integration.resource.impl;

import android.content.Context;

import com.google.gson.JsonObject;

import br.com.compremelhor.api.integration.resource.AbstractResource;
import br.com.compremelhor.model.User;

/**
 * Created by adriano on 25/03/16.
 */
public class UserResource extends AbstractResource<User> {

    public UserResource(Context context) {
        super("users", context);
    }

    @Override
    public String[] getColumnNames() {
        return columns;
    }

    @Override
    public User bindResourceFromJson(JsonObject jsonObject) {
        User user = new User();
        user.setId(jsonObject.get("id").getAsInt());
        user.setEmail(jsonObject.get("username").getAsString());

        if (jsonObject.has("document") && !jsonObject.get("document").toString().equals("null")) {
            user.setDocument(jsonObject.get("document").getAsString());
            user.setTypeDocument(jsonObject.get("documentType").getAsString());
        }
        return user;
    }

    @Override
    public String bindJsonFromEntity(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        if (user.getPassword() != null) {
            sb.append("\"password\" : \"" + user.getPassword() + "\"");
        }
        if (user.getDocument() != null) {
            if (sb.length() > 1) sb.append(",");
            sb.append("\"document\" : \"" + user.getDocument() + "\"");
        }
        if (user.getTypeDocument() != null) {
            if (sb.length() > 1) sb.append(",");
            sb.append("\"documentType\" : \"" + user.getTypeDocument() + "\"");
        }
        if (user.getEmail() != null) {
            if (sb.length() > 1) sb.append(",");
            sb.append("\"username\" : \"" + user.getEmail() + "\"");
        }
        sb.append("}");

        return sb.toString();
    }

    private String[] columns = {"username", "typeDocument", "id", "document"};
}
