package com.example.application.Entities.User;

public class requestsUser extends User {
    boolean enabled;

    public requestsUser(String firstname, String lastname, String username, String password, String team , String email, String role){
        super(firstname , lastname , username , password , team, email , role , 0 , 0 , 0);
        this.enabled = false;
    }

    public boolean getEnabled(){
        return enabled;
    }

}
