package com.example.application.Entities.Event;

public class exportEventDetails extends DefaultEvent {
    private int impact;
    private String stemRelated;
    private String firstRelated;

    public exportEventDetails(int impact , boolean stemRelated , boolean firstRelated , String eventName , double reward){
        super(eventName , reward);
        this.impact= impact;
        this.stemRelated = stemRelated ? "Y" : "N";
        this.firstRelated = firstRelated ? "Y" : "N";
    }

    public int getImpact(){
        return impact;
    }

    public String getStemRelated(){
        return stemRelated;
    }

    public String getFirstRelated(){
        return firstRelated;
    }


}
