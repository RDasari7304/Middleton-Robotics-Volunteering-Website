package com.example.application;

public class Role {
    private String roleName;
    private String description;
    private int availableSpots;
    private String prereq;
    private String eventName;

    public Role(String role , String prereq , String description , String eventName , int availableSpots){
        this.roleName = role;
        this.prereq = prereq;
        this.description = description;
        this.availableSpots = availableSpots;
        this.eventName = eventName;
    }

    public String getRoleName(){
        return roleName;
    }

    public String getDescription(){
        return description;
    }

    public int getNumAvailableSpots(){
        return availableSpots;
    }

    public void setRoleName(String roleName){
        this.roleName = roleName;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setAvailableSpots(int availableSpots){
        this.availableSpots = availableSpots;
    }

    public void set(Role role){
        this.roleName = role.getRoleName();
        this.description = role.getDescription();
        this.availableSpots = role.getNumAvailableSpots();
    }

    public String getPrereq(){
        return prereq;
    }

    public void setPrereq(String prereq){
        this.prereq = prereq;
    }

    public String getEventName(){
        return eventName;
    }


}
