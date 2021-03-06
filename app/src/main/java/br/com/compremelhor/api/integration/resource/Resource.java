package br.com.compremelhor.api.integration.resource;

import java.util.List;
import java.util.Map;

import br.com.compremelhor.api.integration.ResponseServer;

/**
 * Created by adriano on 26/03/16.
 */
public interface Resource<T> {
    String PHISICAL_DEVICE_ON_HOT_SPOT_NETWORK = "192.168.43.126:8080";
    String PHISICAL_DEVICE_ON_WIFI_NETWORK = "192.168.0.122:8080";
    String EMULATOR_DEVICE = "10.0.2.2";

    String ROUTER_IP = PHISICAL_DEVICE_ON_WIFI_NETWORK;
    String APPLICATION_ROOT = "http://" + ROUTER_IP + "/compre_melhor_ws/rest/";

    ResponseServer<T> updateResource(T entity);
    ResponseServer<T> createResource(T entity);
    ResponseServer<T> deleteResource(T entity);
    T getResource(String attributeName, String attributeValue);
    T getResource(Map<String, String> params);
    T getResource(int id);
    T getResource(String location);
    boolean isConnectedOnInternet();
    List<T> getAllResources(int start, int size);

}
