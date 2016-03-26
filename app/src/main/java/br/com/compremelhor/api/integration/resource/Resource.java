package br.com.compremelhor.api.integration.resource;

import br.com.compremelhor.api.integration.ResponseAPI;
import br.com.compremelhor.model.EntityModel;

/**
 * Created by adriano on 26/03/16.
 */
public interface Resource<T extends EntityModel> {
    String APPLICATION_ROOT = "http://10.0.2.2:8080/compre_melhor_ws/rest/";

    ResponseAPI<T> updateResource(String requestBody);
    ResponseAPI<T> createResource(String requestBody);
    T getResource(Long id);
    T getResource(String location);
}
