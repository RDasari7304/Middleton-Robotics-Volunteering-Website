package com.example.application.Entities.User;

public class RoboticsMember {
    private String FirstName;
    private String LastName;
    private String email;
    private double totalOutreachHoursEarned;
    private double totalMoneyFundraised;
    private int numEventsAttended;

    public RoboticsMember(){

    }

    public RoboticsMember(String firstName , String lastName , String email , double totalOutreachHoursEarned , double totalMoneyFundraised , int numEventsAttended){
        this.FirstName = firstName;
        this.LastName = lastName;
        this.email = email;
        this.totalOutreachHoursEarned = totalOutreachHoursEarned;
        this.totalMoneyFundraised = totalMoneyFundraised;
        this.numEventsAttended = numEventsAttended;
    }

    public RoboticsMember(String firstName , String lastName){
        this.FirstName = firstName;
        this.LastName = lastName;
    }


    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getTotalOutreachHoursEarned() {
        return totalOutreachHoursEarned;
    }

    public void setTotalOutreachHoursEarned(double totalOutreachHoursEarned) {
        this.totalOutreachHoursEarned = totalOutreachHoursEarned;
    }

    public double getTotalMoneyFundraised() {
        return totalMoneyFundraised;
    }

    public void setTotalMoneyFundraised(double totalMoneyFundraised) {
        this.totalMoneyFundraised = totalMoneyFundraised;
    }

    public int getNumEventsAttended() {
        return numEventsAttended;
    }

    public void setNumEventsAttended(int numEventsAttended) {
        this.numEventsAttended = numEventsAttended;
    }
}
