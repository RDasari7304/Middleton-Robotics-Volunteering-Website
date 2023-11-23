package com.example.application.Resources.Extras;

public class MessageRunnable implements Runnable{
    private String message;
    private String to;
    private String from;
    private String pass;
    private String sub;

    public MessageRunnable(String to, String from, String pass, String sub, String message){
        this.message = message;
        this.to = to;
        this.from = from;
        this.pass = pass;
        this.sub = sub;
    }

    @Override
    public void run() {
        message = message.replaceAll("\\<.*?\\>", "");

        EmailSender.send(to, sub, message
                , from, pass);
    }
}
