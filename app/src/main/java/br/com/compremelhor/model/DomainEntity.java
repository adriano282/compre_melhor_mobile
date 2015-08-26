package br.com.compremelhor.model;

import com.j256.ormlite.field.DatabaseField;

import java.util.Calendar;

/**
 * Created by adriano on 25/08/15.
 */
public class DomainEntity {
    @DatabaseField(id = true)
    private Long id;
    @DatabaseField
    private Calendar dateCreated;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Calendar getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }
}
