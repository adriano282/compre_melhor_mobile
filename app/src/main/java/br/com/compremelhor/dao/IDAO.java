package br.com.compremelhor.dao;

import java.util.List;

import br.com.compremelhor.model.EntityModel;

public interface IDAO<T> {
    T find(int id);
    void delete(int id);
    long insert(EntityModel o);
    long insertOrUpdate(EntityModel o);
    List<T> findAllByForeignId(String idName, int idValue);
    List<T> findAllByAttribute(String attributeName, String attributeValue);
    T findByAttribute(String attributeName, String attributeValue);
}
