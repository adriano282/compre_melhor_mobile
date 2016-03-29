package br.com.compremelhor.model;

import java.util.Calendar;


/**
 * Created by adriano on 25/08/15.
 */
public class EntityModel {
    private int id;
    private Calendar dateCreated;
    private Calendar lastUpdated;

    public EntityModel() {}
    public EntityModel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Calendar getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Calendar lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
