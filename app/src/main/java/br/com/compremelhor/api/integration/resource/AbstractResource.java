package br.com.compremelhor.api.integration.resource;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import br.com.compremelhor.api.integration.ResponseAPI;
import br.com.compremelhor.model.EntityModel;

/**
 * Created by adriano on 25/03/16.
 */
public abstract class AbstractResource<T extends EntityModel>{
    protected final String APPLICATION_ROOT = "http://10.0.2.2:8080/compre_melhor_ws/rest/";
    protected final String RESOURCE_ROOT;

    public AbstractResource(String RESOURCE_ROOT) {
        this.RESOURCE_ROOT = RESOURCE_ROOT;
    }

    public abstract T bindResourceFromJson(JsonObject jsonObject);

    public ResponseAPI<T> createResource(String requestBody) {
        try {
            URL url = new URL(APPLICATION_ROOT.concat(RESOURCE_ROOT));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(HTTPMethods.POST.toString());
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream os = connection.getOutputStream();

            if (requestBody == null || requestBody.isEmpty())
                throw new RuntimeException("RequestBody null in a request Push on Server");

            os.write(requestBody.getBytes());
            os.flush();

            ResponseAPI<T> responseApi = new ResponseAPI<>();
            responseApi.setStatusCode(connection.getResponseCode());

            if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                String errors = connection.getHeaderField("errors");
                if (errors != null)
                    responseApi.setErrors(Arrays.asList(errors.split("#")));
            }

            responseApi.setLocation(connection.getHeaderField("Location"));
            connection.disconnect();
            responseApi.setEntity(getResourceFromLocation(responseApi.getLocation()));
            return responseApi;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public T getResourceById(Long id) {
        try {
            URL url = new URL(APPLICATION_ROOT.concat(RESOURCE_ROOT).concat("/").concat(String.valueOf(id)));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(HTTPMethods.GET.toString());
            connection.setRequestProperty("Content-Type", "application/json");

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d("REST API", "GET " + RESOURCE_ROOT + id + " - Failed");
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

            T t = bindResourceFromJson(jsonObject);
            connection.disconnect();
            return t;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public T getResourceFromLocation(String location) {
        Long id = extractIdFromLocation(location);
        return getResourceById(id);
    }

    private Long extractIdFromLocation(String location) {
        if (location == null) return null;
        if (location.isEmpty()) return null;
        if (location.split("/").length <1) return null;

        String stringId = location.split("/")[location.split("/").length-1];

        try {
            return Long.valueOf(stringId);
        } catch (Exception e) {
            return null;
        }
    }

    protected enum HTTPMethods {
        GET, POST, PUT, DELETE
    }
}
