package com.example.application.Entities.User;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class User extends RoboticsMember {
    @Id
    private String Username = "";
    private String Password = "";
    private String TeamName = "";
    private String Role;

    public User(String firstname, String lastname,String username, String Password, String Team , String email, String Role , double totalOutreachEarned , double totalMoneyRaised , int numEventsAttended){
        super(firstname , lastname , email , totalOutreachEarned , totalMoneyRaised , numEventsAttended);
        this.Username = username;
        this.Password = Password;
        this.TeamName = Team;
        this.Role = Role;
    }

    public User(String firstName, String lastName, String username, String Password, String email , double totalOutreachEarned , double totalMoneyRaised , int numEventsAttended){
        super(firstName , lastName , email , totalOutreachEarned , totalMoneyRaised , numEventsAttended);
        this.Username = username;
        this.Password = Password;
    }

    public String getUsername(){
        return this.Username;
    }

    public String getPassword(){
        return this.Password;
    }

    public String getTeam(){
        return this.TeamName;
    }

    public String getRole(){return Role;}
}
