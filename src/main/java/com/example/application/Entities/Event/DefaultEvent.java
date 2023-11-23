package com.example.application.Entities.Event;

public class DefaultEvent {
    private String eventName;
    private String eventType;
    private String startingDate;
    private String description;
    private double reward;

    public DefaultEvent(String eventName , String eventType , String startingDate , String description , double reward){
        this.eventName = eventName;
        this.eventType = eventType;
        this.startingDate = startingDate;
        this.description = description;
        this.reward = reward;
    }

    public DefaultEvent(String eventName , double reward){
        this.eventName = eventName;
        this.reward = reward;
    }

    public void setEventName(String eventName){
        this.eventName = eventName;
    }

    public void setEventType(String eventType){
        this.eventType = eventType;
    }

    public void setStartingDate(String startingDate){
        this.startingDate = startingDate;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getEventName(){
        return eventName;
    }

    public String getEventType(){
        return eventType;
    }

    public String getStartingDate(){
        return startingDate;
    }

    public String getDescription(){
        return description;
    }


    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }
}
