package com.example.application.Components;

public class eventCardHeader {
    private String date;
    private double rewardEarned;

    public eventCardHeader(String date , double rewardEarned){
        this.date = date;
        this.rewardEarned = rewardEarned;
    }

    public String getDate(){
        return date;
    }

    public double getRewardEarned(){
        return rewardEarned;
    }


}
