package br.com.compremelhor.api.integration.resource;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.model.EntityModel;

/**
 * Created by adriano on 25/03/16.
 */
public abstract class AbstractResource<T extends EntityModel> implements Resource<T> {
    protected final String RESOURCE_ROOT;
    private Context context;

    public AbstractResource(String RESOURCE_ROOT, Context context) {
        this.RESOURCE_ROOT = RESOURCE_ROOT;
        this.context = context;
    }

    public abstract T bindResourceFromJson(JsonObject jsonObject);
    public abstract String bindJsonFromEntity(T t);

    public ResponseServer<T> updateResource(T entity) {
        return pushOnServer(entity, HTTPMethods.PUT);
    }

    public ResponseServer<T> createResource(T entity) {
        return pushOnServer(entity, HTTPMethods.POST);
    }

    public T getResource(int id) {
        try {
            URL url = new URL(APPLICATION_ROOT
                    .concat(RESOURCE_ROOT)
                    .concat("/")
                    .concat(String.valueOf(id)));

            return doGET(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            Log.d("REST_API", e.getMessage());
            return null;
        }
    }

    public T getResource(String location) {
        int id = extractIdFromLocation(location);
        return getResource(id);
    }

    protected T doGET(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(HTTPMethods.GET.toString());

        connection.setRequestProperty("Authorization", "token_app DG4OjT9ciuPtHk1p7Fi/kg==");
        connection.setRequestProperty("Content-Type", "application/json");

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            Log.d("REST API", "GET " + RESOURCE_ROOT + "/" + url.getQuery());
            Log.d("REST API", "Response Code: " + connection.getResponseCode());
            Log.d("REST API", "Response Message: " + connection.getResponseMessage());
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
    }

    public boolean isConnectedOnInternet() {
        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = connManager.getActiveNetworkInfo();
        Log.d("NETWORK", "Status: " + ni.isConnected());
        Log.d("NETWORK", "Type: " + ni.getSubtype());
        return ni.isConnected();
    }
    private ResponseServer<T> pushOnServer(T entity, HTTPMethods method) {
        if (method == HTTPMethods.GET || method == HTTPMethods.DELETE)
            throw new RuntimeException("GET || DELETE Method on method for push Data on Server");

        if (method == HTTPMethods.PUT && entity.getId() == 0)
            throw new IllegalArgumentException("PUT Method: id parameter cannot be null");

        String requestBody = bindJsonFromEntity(entity);
        try {
            String uri = APPLICATION_ROOT.concat(RESOURCE_ROOT);

            if (method == HTTPMethods.PUT) {
                uri = uri.concat("/").concat(String.valueOf(entity.getId()));
            }

            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod(method.toString());
            connection.setRequestProperty("Authorization", "token_app DG4OjT9ciuPtHk1p7Fi/kg==");
            connection.setRequestProperty("Content-Type", "application/json");
            OutputStream os = connection.getOutputStream();

            if (requestBody == null || requestBody.isEmpty())
                throw new RuntimeException("RequestBody null in a request Push on Server");

            os.write(requestBody.getBytes());
            os.flush();

            ResponseServer<T> responseServer = new ResponseServer<>();
            responseServer.setStatusCode(connection.getResponseCode());

            if (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED &&
                    connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                String errors = connection.getHeaderField("errors");
                if (errors != null)
                    responseServer.setErrors(Arrays.asList(errors.split("#")));
                connection.disconnect();
                return responseServer;
            }

            responseServer.setLocation(connection.getHeaderField("Location"));
            connection.disconnect();
            responseServer.setEntity(getResource(responseServer.getLocation()));
            return responseServer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int extractIdFromLocation(String location) {
        if (location == null) return 0;
        if (location.isEmpty()) return 0;
        if (location.split("/").length <1) return 0;

        String stringId = location.split("/")[location.split("/").length-1];

        try {
            return Integer.valueOf(stringId);
        } catch (Exception e) {
            return 0;
        }
    }

    public enum HTTPMethods {
        GET, POST, PUT, DELETE
    }
}
