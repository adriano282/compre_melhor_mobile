package br.com.compremelhor.api.integration.resource;

import java.util.Map;

import br.com.compremelhor.api.integration.ResponseServer;
import br.com.compremelhor.model.EntityModel;

/**
 * Created by adriano on 26/03/16.
 */
public interface Resource<T extends EntityModel> {
    String APPLICATION_ROOT = "http://10.0.2.2:8080/compre_melhor_ws/rest/";

    ResponseServer<T> updateResource(T entity);
    ResponseServer<T> createResource(T entity);
    T getResource(String attributeName, String attributeValue);
    T getResource(Map<String, String> params);
    T getResource(int id);
    T getResource(String location);
}
