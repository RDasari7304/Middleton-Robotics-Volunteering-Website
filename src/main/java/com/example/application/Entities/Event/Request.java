package com.example.application.Entities.Event;


import com.vaadin.flow.component.html.Image;

public class Request {
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private double rewardEarned;
    private String status;
    private String eventAttended;
    private String comment;
    private String eventType;
    private String yearBound;
    private Image proofImage;

    public Request(String firstName , String lastName , String email , String role, String comment , double rewardEarned , String status , String eventAttended , String eventType , String yearBound , Image proofImage){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.rewardEarned = rewardEarned;
        this.status = status;
        this.eventAttended = eventAttended;
        this.comment = comment;
        this.eventType = eventType;
        this.yearBound = yearBound;
        this.proofImage = proofImage;
    }

    public Request(String firstName , String lastName , String email , String role, String comment , double rewardEarned , String status , String eventAttended , String eventType , String yearBound ){
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.rewardEarned = rewardEarned;
        this.status = status;
        this.eventAttended = eventAttended;
        this.comment = comment;
        this.eventType = eventType;
        this.yearBound = yearBound;
    }
    public void setYearBound(String yearBound){
        this.yearBound = yearBound;
    }


    public String getYearBound(){
        return this.yearBound;
    }

    public String getEventType(){
        return eventType;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public String getEmail(){
        return email;
    }

    public String getRole(){
        return role;
    }

    public double getRewardEarned(){
        return rewardEarned;
    }

    public String getStatus() {
        return status;
    }

    public String getEventAttended(){
        return  eventAttended;
    }

    public String getComment(){
        return comment;
    }

    public Image getProofImage(){
        return proofImage;
    }

}
