package br.com.compremelhor.model;

import java.util.Calendar;


/**
 * Created by adriano on 25/08/15.
 */
public class DomainEntity {
    private Long id;
    private Calendar dateCreated;

    public DomainEntity() {}
    public DomainEntity(Long id) {
        this.id = id;
    }

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
