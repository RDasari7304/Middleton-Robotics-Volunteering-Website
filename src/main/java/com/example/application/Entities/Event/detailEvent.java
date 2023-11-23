package com.example.application.Entities.Event;

public class detailEvent extends DefaultEvent {
    private String role;

    public detailEvent(String eventName , String startingDate ,  String eventType , double reward , String description , String role){
        super(eventName , eventType , startingDate , description , reward);
        this.role = role;
    }

    public String getRole(){
        return role;
    }


}
