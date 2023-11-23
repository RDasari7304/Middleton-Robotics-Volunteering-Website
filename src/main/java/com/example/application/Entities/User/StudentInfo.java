package com.example.application.Entities.User;

public class StudentInfo {
    private String firstName;
    private String lastName;
    private String Team;
    private int numEventsAttended;
    private int numEventsApproved;
    private String sumOfEventsFormula;
    private int countStem;
    private int countFirst;


    public StudentInfo(String firstName , String lastName , String Team ){
        this.firstName = firstName;
        this.lastName = lastName;
        this.Team = Team;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTeam() {
        return Team;
    }

    public void setTeam(String team) {
        Team = team;
    }

    public int getNumEventsAttended() {
        return numEventsAttended;
    }

    public void setNumEventsAttended(int numEventsAttended) {
        this.numEventsAttended = numEventsAttended;
    }

    public int getNumEventsApproved() {
        return numEventsApproved;
    }

    public void setNumEventsApproved(int numEventsApproved) {
        this.numEventsApproved = numEventsApproved;
    }
}
