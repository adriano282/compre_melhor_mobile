package br.com.compremelhor.model;

import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * Created by adriano on 25/08/15.
 */
public class DomainEntity {
    private Long id;
    private GregorianCalendar dateCreated;
    private GregorianCalendar lastUpdated;

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

    public void setDateCreated(GregorianCalendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    public GregorianCalendar getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(GregorianCalendar lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
