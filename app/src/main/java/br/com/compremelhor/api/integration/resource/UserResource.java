package br.com.compremelhor.api.integration.resource;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import br.com.compremelhor.model.User;

/**
 * Created by adriano on 25/03/16.
 */
public class UserResource extends AbstractResource<User> {

    public UserResource(Context context) {
        super("users", context);
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

    @Override
    public User getResource(String attributeName, String attributeValue) {
        if (!Arrays.asList(columns).contains(attributeName.trim()))
            throw new IllegalArgumentException("Unknown attribute name for User entity: " + attributeName);

        try {
            URL url = new URL(APPLICATION_ROOT.concat(RESOURCE_ROOT)
                    .concat("?")
                    .concat(attributeName)
                    .concat("=")
                    .concat(attributeValue));

            return doGET(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.d("REST_API", e.getMessage());
            return null;
        }
    }

    @Override
    public User getResource(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        for (Map.Entry<String, String> pair : entries) {

            if (!Arrays.asList(columns).contains(pair.getKey().trim()))
                throw new IllegalArgumentException(
                        "Unknown attribute name for User entity: " + pair.getKey().trim());

            if (sb.length() == 0) sb.append("?");
            else sb.append("&");

            sb.append(pair.getKey().trim())
                    .append("=").append(pair.getValue());
        }

        try {
            URL url = new URL(APPLICATION_ROOT.concat(RESOURCE_ROOT)
                    .concat(URLEncoder.encode(sb.toString(), "UTF-8")));

            return doGET(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e1) {
            Log.d("REST_API", e1.getMessage());
            return null;
        }
    }

    private String[] columns = {"username", "typeDocument", "id", "document"};
}
