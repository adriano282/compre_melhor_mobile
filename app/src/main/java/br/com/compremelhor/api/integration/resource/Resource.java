package br.com.compremelhor.api.integration.resource;

import java.util.Map;

import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.model.EntityModel;

/**
 * Created by adriano on 26/03/16.
 */
public interface Resource<T extends EntityModel> {
    String PHISICAL_DEVICE = "192.168.0.101:8080";
    String EMULATOR_DEVICE = "10.0.2.2";

    String DEVICE_IP = PHISICAL_DEVICE;
    String APPLICATION_ROOT = "http://" + DEVICE_IP + "/compre_melhor_ws/rest/";

    ResponseServer<T> updateResource(T entity);
    ResponseServer<T> createResource(T entity);
    T getResource(String attributeName, String attributeValue);
    T getResource(Map<String, String> params);
    T getResource(int id);
    T getResource(String location);
}
