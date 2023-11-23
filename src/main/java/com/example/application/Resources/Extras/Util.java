package com.example.application.Resources.Extras;


import com.example.application.Components.CustomRadioButton;
import com.example.application.Entities.Event.Event;
import com.example.application.Entities.Event.Request;
import com.example.application.Entities.Event.detailEvent;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.notification.Notification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

public class Util {

    public static boolean datePassed(LocalDate endingDate, LocalTime endingTime){
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        if(currentDate.isAfter(endingDate) || currentDate.isEqual(endingDate)){
            if(currentDate.isEqual(endingDate) && currentTime.isAfter(endingTime)){
                return true;
            }else if(currentDate.isEqual(endingDate) && currentTime.isBefore(endingTime)){
                return false;
            }

            return true;

        }else{
            return false;
        }
    }

    public static boolean validateSignUpWindow(LocalDate signUpStartDate, LocalTime signUpStartTime,
                                            LocalDate signUpEndDate, LocalTime signUpEndTime,
                                            LocalDate eventStartDate, LocalTime eventStartTime){
        if((signUpStartDate.isBefore(eventStartDate) || (signUpStartDate.isEqual(eventStartDate) && signUpStartTime.isBefore(eventStartTime)))
        && (signUpEndDate.isBefore(eventStartDate) || (signUpEndDate.isEqual(eventStartDate) && signUpEndTime.isBefore(eventStartTime))) &&
        (signUpEndDate.isAfter(signUpStartDate) || (signUpEndDate.isEqual(signUpStartDate) && signUpEndTime.isAfter(signUpStartTime)))){
            return true;
        }else{
            return false;
        }
    }

    public static String dateToString(LocalDate date){

        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

    public static String comparedString(Event event , DatePicker datePicker){
        if(datePicker.isEmpty()) return event.getStartingDate();

        return datePicker.getValue().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

    public static String timeToString(LocalTime time){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
        return time.format(dateTimeFormatter);

    }

    public static LocalDate stringToDate(String date){
        return LocalDate.parse(date , DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
    }

    public static LocalTime stringToTime(String time) {
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("hh:mm a"));
    }

    public static ArrayList<Integer>  getFundraisingPerMonth(List<detailEvent> events){
        ArrayList<Integer> perMonth = new ArrayList<>();
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);

        for(detailEvent e : events){
            if(e.getEventType().equals("Fundraising")){
                int i = stringToDate(e.getStartingDate()).getMonthValue();
                perMonth.set(i - 1 , perMonth.get(i - 1) + 1);
            }
        }

        return perMonth;

    }

    public static ArrayList<Integer> getOutreachEventPerMonth(List<detailEvent> events){
        ArrayList<Integer> perMonth = new ArrayList<>();
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);
        perMonth.add(0);

        for(detailEvent e : events){
            if(e.getEventType().equals("Outreach Event")){
                int i = stringToDate(e.getStartingDate()).getMonthValue();
                perMonth.set(i - 1 , perMonth.get(i - 1) + 1);
            }
        }

        return perMonth;

    }
    public static ArrayList<CustomRadioButton> copyToNewArray(ArrayList<CustomRadioButton> arrayToCopy){
        ArrayList<CustomRadioButton> newArray = new ArrayList<>();

        for(CustomRadioButton crb : arrayToCopy){
            newArray.add(crb);
        }

        return newArray;
    }

    public static ArrayList<Double> getRequestsArraySum(ArrayList<Request> requests){
        double outreachPending = 0;
        double fundraisingPending = 0;

        for(Request rq : requests){
            Notification.show(String.valueOf(rq.getRewardEarned()));
            if(rq.getEventType().equals("Fundraising Event")){
                fundraisingPending += rq.getRewardEarned();
            }else{
                outreachPending += rq.getRewardEarned();
            }
        }

        ArrayList<Double> outputs = new ArrayList<>();
        outputs.add(outreachPending);
        outputs.add(fundraisingPending);

        return outputs;
    }

    public static String getYearBound(){
        LocalDate today = LocalDate.now();

        int yearStarted = 0;
        int endingYear = 0;

        if(today.getMonthValue() <= 5){
            yearStarted = today.getYear() - 1;
            endingYear = today.getYear();
        }else if(today.getMonthValue() > 5){
            yearStarted = today.getYear();
            endingYear = today.getYear() + 1;
        }


        return String.valueOf(yearStarted) + " - " + String.valueOf(endingYear);
    }

    public static void notify(String target){
        Notification.show(target , 2500 , Notification.Position.BOTTOM_END);
    }

}
