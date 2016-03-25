package br.com.compremelhor.api.integration.resource;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.compremelhor.api.integration.ResponseAPI;
import br.com.compremelhor.model.User;

/**
 * Created by adriano on 25/03/16.
 */
public class UserResource extends AbstractResource<User> {

    public UserResource() {
        super("users");
    }

    public User findUserByUsername(String username) {
        try {
            URL url = new URL(APPLICATION_ROOT.concat(RESOURCE_ROOT)
                    .concat("?")
                    .concat("attributeName=").concat("username")
                    .concat("&")
                    .concat("attributeValue=").concat(username));

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(HTTPMethods.GET.toString());
            connection.setRequestProperty("Content-Type", "application/json");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("REST API", "GET " + RESOURCE_ROOT + url.getQuery() + " - Failed");
                Log.d("REST API", "Response Code: " + connection.getResponseCode());
                return null;
            }

            BufferedReader br =
                    new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            JsonElement json =  new JsonParser().parse(sb.toString());
            JsonObject jsonObject = json.getAsJsonObject();

            User t = bindResourceFromJson(jsonObject);
            connection.disconnect();
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseAPI<User> createUserOnWebServer(User user) {
        String requestBody = "{\"username\": \"" +user.getEmail() + "\", " +
                "\"password\": \""+ user.getPassword() +"\"}";
        return createResource(requestBody);
    }

    @Override
    public User bindResourceFromJson(JsonObject jsonObject) {
        User user = new User();
        user.setId(jsonObject.get("id").getAsLong());
        user.setEmail(jsonObject.get("username").getAsString());

        if (jsonObject.has("document") && !jsonObject.get("document").toString().equals("null")) {
            user.setDocument(jsonObject.get("document").getAsString());
            user.setTypeDocument(jsonObject.get("documentType").getAsString());
        }
        return user;
    }
}
