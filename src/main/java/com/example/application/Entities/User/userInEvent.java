package com.example.application.Entities.User;

public class userInEvent extends RoboticsMember {
    private String Role;
    private String eventSignedUpIn;
    private String status;
    private String comment;
    private int rewardApproved;

    public userInEvent(String firstName , String lastName , String role , String eventSignedUpIn , String status , int rewardApproved){
        super(firstName , lastName);
        this.Role = role;
        this.eventSignedUpIn = eventSignedUpIn;
        this.status = status;
        this.rewardApproved = rewardApproved;
    }

    public userInEvent(String firstName , String lastName , String role , String eventSignedUpIn , String comment, String status){
        super(firstName , lastName);
        this.Role = role;
        this.eventSignedUpIn = eventSignedUpIn;
        this.status = status;
        this.comment = comment;
    }

    public userInEvent(){

    }

    public String getComment(){
        return comment;
    }

    public int getRewardApproved(){
        return rewardApproved;
    }

    public String getRole(){
        return Role;
    }

    public String getEventSignedUpIn(){return eventSignedUpIn;}

    public String getStatus(){return status;}
}
