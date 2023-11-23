package com.example.application.Entities.Event;

import com.example.application.Role;

import java.util.ArrayList;

public class Event extends DefaultEvent {
    private String rewardType;
    private int slotsLeft;
    private ArrayList<Role> roles;
    private String startingTime;
    private String endingTime;
    private String eventType;
    private String endingDate;
    private String signUpStartTime;
    private String signUpStartDate;
    private String signUpEndTime;
    private String signUpEndDate;
    private String location;
    private int totalSlotsAvailable;

    public Event(ArrayList<Role> roles , String name , String location , String type , double reward , String startingDate , String startingTime , String endingDate , String endingTime , String signUpStartDate, String signUpStartTime, String signUpEndDate, String signUpEndTime, int slotsLeft , String description , int totalSlotsAvailable){
        super(name , type , startingDate , description , reward);
        this.roles = roles;
        this.location = location;
        this.eventType = type;
        this.startingTime = startingTime;
        this.endingDate = endingDate;
        this.endingTime = endingTime;
        this.signUpStartDate = signUpStartDate;
        this.signUpStartTime = signUpStartTime;
        this.signUpEndDate = signUpEndDate;
        this.signUpEndTime = signUpEndTime;
        this.slotsLeft = slotsLeft;
        this.totalSlotsAvailable = totalSlotsAvailable;

        if(type != null) {
            this.rewardType = type.equals("Fundraising") ? "Fundraising Money" : "Hours";
        }

    }


    public String getLocation(){return location;}


    public String getEndingDate(){return endingDate;}

    public String getRewardType(){
        return rewardType;
    }

    public String getStartingTime(){
        return startingTime;
    }

    public String getEndTime(){
        return endingTime;
    }


    public int getSlotsAvaliable() {
        return slotsLeft;
    }

    public ArrayList<Role> getRoles() {
        return roles;
    }

    public String getEventType(){
        return eventType;
    }

    public int getTotalSlotsAvailable(){return totalSlotsAvailable;}

    public String getSignUpStartDate(){return signUpStartDate;}

    public String getSignUpEndDate(){return signUpEndDate;}

    public String getSignUpStartTime(){return signUpStartTime;}

    public String getSignUpEndTime(){return signUpEndTime;}
}
