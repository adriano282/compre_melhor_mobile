package br.com.compremelhor.api.integration;

import java.util.List;

/**
 * Created by adriano on 25/03/16.
 */
public class ResponseServer<T> {
    private String location;
    private T entity;
    private Integer statusCode;
    private List<String> errors;

    public boolean hasErrors() {
        return errors != null && errors.size() > 0;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
