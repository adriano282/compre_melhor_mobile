package br.com.compremelhor.model;

import java.math.BigDecimal;

/**
 * Created by adriano on 18/07/16.
 */
public class FreightType extends EntityModel {
    private String typeName;
    private String description;
    private BigDecimal rideValue;
    private boolean scheduled;
    private int availabilityScheduleWorkDays;
    private int delayInWorkdays;
    private int establishmentId;

    public void setDelayInWorkdays(int delayInWorkdays) {
        this.delayInWorkdays = delayInWorkdays;
    }

    public int getDelayInWorkdays() { return delayInWorkdays; }
    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public int getEstablishmentId() {
        return establishmentId;
    }

    public void setEstablishmentId(int establishmentId) {
        this.establishmentId = establishmentId;
    }
    public BigDecimal getRideValue() {
        return rideValue;
    }

    public void setRideValue(BigDecimal rideValue) {
        this.rideValue = rideValue;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAvailabilityScheduleWorkDays() {
        return availabilityScheduleWorkDays;
    }

    public void setAvailabilityScheduleWorkDays(int availabilityScheduleWorkDays) {
        this.availabilityScheduleWorkDays = availabilityScheduleWorkDays;
    }

}
