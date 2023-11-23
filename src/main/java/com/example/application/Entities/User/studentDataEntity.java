package com.example.application.Entities.User;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class studentDataEntity extends RoboticsMember {
    @Id
    private int numOfEventsAttended;
    private double hoursEarned;
    private double moneyRaised;
    private String outReachHoursStatus;
    private String moneyRaisedStatus;


    public studentDataEntity(String firstName , String lastName , String email , int numOfEventsAttended , double hoursEarned , double moneyRaised){
        super(firstName , lastName , email , hoursEarned , moneyRaised , numOfEventsAttended);
        this.numOfEventsAttended = numOfEventsAttended;
        this.hoursEarned = hoursEarned;
        this.moneyRaised = moneyRaised;

        outReachHoursStatus = hoursEarned > 30 ? "Complete" : "In Progress";
        moneyRaisedStatus = moneyRaised >= 600 ? "Complete" : "In Progress";

    }

    public int getNumOfEventsAttended() {
        return numOfEventsAttended;
    }

    public double getHoursEarned() {
        return hoursEarned;
    }

    public double getMoneyRaised() {
        return moneyRaised;
    }

    public String getOutReachHoursStatus() {
        return outReachHoursStatus;
    }

    public String getMoneyRaisedStatus() {
        return moneyRaisedStatus;
    }

    public void setOutReachHoursStatus(String outReachHoursStatus){
        this.outReachHoursStatus = outReachHoursStatus;
    }

    public void setMoneyRaisedStatus(String moneyRaisedStatus){
        this.moneyRaisedStatus = moneyRaisedStatus;
    }

}
