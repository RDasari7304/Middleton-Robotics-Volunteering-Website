package com.example.application.Resources.Services;

import com.example.application.Entities.Event.Event;
import com.example.application.Entities.Event.Request;
import com.example.application.Entities.Event.detailEvent;
import com.example.application.Entities.Event.exportEventDetails;
import com.example.application.Entities.User.*;
import com.example.application.Resources.EntityMappers.*;
import com.example.application.Resources.Extras.Util;
import com.example.application.Role;
import com.example.application.Security.DataSourceConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class DataService {

    JdbcTemplate jdbcTemplate = new JdbcTemplate();

    public DataService(){
        setJdbcTemplate(new JdbcTemplate());
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.setDataSource(DataSourceConfig.getDataSource());
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<User> findAllUsers(){
        return jdbcTemplate.query("SELECT * from MiddletonRoboticsMembers where Enabled = 1" , new UserRowMapper());
    }

    public Boolean isUserNameAvailable(String username){
        return jdbcTemplate.query("SELECT * from MiddletonRoboticsMembers where Username = ?" , new Object[] {username} , new UserRowMapper()).size() == 0;
    }

    public void registerUser(User user){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        jdbcTemplate.update("Insert into MiddletonRoboticsMembers(FirstName , LastName , Username , Password , emailAddress , Team , Role , Enabled , TotalOutreach , TotalFundraised , NumEventsAttended) values(?, ?, ?, ? ,? ,? , ? , ? , 0 , 0 , 0)"
         , user.getFirstName() , user.getLastName() , user.getUsername() , encryptedPassword , user.getEmail() , user.getTeam() , user.getRole()  , 0);
    }

    public List<studentDataEntity> getAllStudentData(){
        return jdbcTemplate.query("SELECT * FROM MiddletonRoboticsMembers WHERE Enabled = 1" , new StudentDataRowMapper());
    }

    public List<Event> getAllEvents(){
        return jdbcTemplate.query("select * from Events" , new EventMapper());

    }

    public detailEvent getDetailEventByName(String eventName , String role){
        return jdbcTemplate.query("select * from Events where Name = ? " , new Object[] {eventName} , (rs , row) -> new
                detailEvent(rs.getString("Name") , rs.getString("StartingDate") , rs.getString("Type") ,
                rs.getDouble("Reward") , rs.getString("description") , role)).get(0);
    }

    public List<userInEvent> getAllRegisteredInEvent(String eventName){
        return jdbcTemplate.query("Select * from EventSignUps where EventName = ?" , new Object[] {eventName} ,
                (rs , row) -> new userInEvent(rs.getString("FirstName") ,
                        rs.getString("LastName"), rs.getString("Role") ,
                        rs.getString("EventName") , rs.getString("Status") , rs.getInt("RewardApproved")));
    }

    public List<userInEvent> getAllRegisteredInEventWithStatus(String eventName , String status){
        return jdbcTemplate.query("Select * from EventSignUps where EventName = ? AND Status = ?" , new Object[] {eventName , status} ,
                (rs , row) -> new userInEvent(rs.getString("FirstName") ,
                        rs.getString("LastName"), rs.getString("Role") ,
                        rs.getString("EventName") , rs.getString("Status") , rs.getInt("RewardApproved")));
    }

    public void addEvent(Event event){
        jdbcTemplate.update("insert into Events(Name , Location , Type , StartingDate , StartingTime , EndingDate , EndingTime, SignUpStartDate, SignUpStartTime, SignUpEndDate, SignUpEndTime , SlotsLeft , Reward , Description , TotalSlotsAvailable) values (?,?,?,?,?,?,?,?,?,? , ?, ?, ?, ?, ?)"
        , event.getEventName() , event.getLocation() , event.getEventType() , event.getStartingDate() , event.getStartingTime() , event.getEndingDate() ,
                event.getEndTime(), event.getSignUpStartDate(), event.getSignUpStartTime(), event.getSignUpEndDate(), event.getSignUpEndTime() , event.getTotalSlotsAvailable() , event.getReward() , event.getDescription() , event.getTotalSlotsAvailable());
        for(Role role : event.getRoles()) {
            jdbcTemplate.update("insert into EventRoles(Role , PreReq , SlotsLeft , Description , EventName) VALUES (?, ?, ?, ?, ?)" ,
                    new Object[] {role.getRoleName() , role.getPrereq() , role.getNumAvailableSpots() , role.getDescription() , role.getEventName()});
        }
    }

    public Role getRoleDetails(String eventName , String role){
        System.out.println(eventName + " " + role);
        return jdbcTemplate.query("SELECT * FROM EventRoles WHERE EventName = ? AND Role = ?" , new Object[] {eventName , role} ,
                (rs , row) -> new Role(rs.getString("Role") , rs.getString("PreReq") , rs.getString("Description") ,
                        rs.getString("EventName") , rs.getInt("SlotsLeft"))).get(0);
    }

    public List<Role> getRolesForEvent(String EventName){
        return jdbcTemplate.query("select * from EventRoles where EventName = ?" , new Object[] {EventName} ,
                (rs , row) -> new Role(rs.getString("Role") , rs.getString("Prereq") , rs.getString("Description")
                , rs.getString("EventName") , rs.getInt("SlotsLeft")));
    }

    public List<String> getUserInfo(String firstName , String lastName){
        HashMap<Integer , String> intToColumName = new HashMap<>();
        intToColumName.put(0 , "FirstName");
        intToColumName.put(1 , "LastName");
        intToColumName.put(2 , "Team");

        int counter = 0;
        List<String[]> stringArray =  jdbcTemplate.query("SELECT FirstName , LastName , Team FROM MiddletonRoboticsMembers WHERE FirstName = ? AND LastName = ?" , new Object[] {firstName , lastName} ,
                (rs , row) -> new String[]{rs.getString("FirstName") , rs.getString("LastName") , rs.getString("Team")}

    );
        List<String> list = new ArrayList<>();
        list.add(stringArray.get(0)[0]);
        list.add(stringArray.get(0)[1]);
        list.add(stringArray.get(0)[2]);

        list.add(Integer.toString(jdbcTemplate.query("SELECT COUNT(*) from EventSignUps WHERE FirstName = ? AND LastName = ?" , new Object[] {firstName , lastName} , new IntMapper()).get(0)));

        list.add(Integer.toString(jdbcTemplate.query("SELECT COUNT(*) from Requests WHERE FirstName = ? AND LastName = ? AND Status = ?" , new Object[] {firstName , lastName , "approved"} , new IntMapper()).get(0)));
        return list;
    }

    public List<Request> getAllRequestsForUserWithStatus(String firstName , String lastName , String status){
        return jdbcTemplate.query("SELECT * FROM Requests where FirstName = ? AND LastName = ? AND Status = ?" , new Object[]{firstName , lastName , status} ,
                new RequestsRowMapper());
    }

    public void updateJoinRequestStatus(userInEvent user , String status){
        jdbcTemplate.update("UPDATE EventSignUps SET Status = ? where EventName = ? AND FirstName = ? AND LastName = ?" , status , user.getEventSignedUpIn()
        , user.getFirstName() , user.getLastName());

        int offset = 0;
        if(user.getStatus().equals("approved")){
            if(status.equals("rejected")) offset--;
        }else if(user.getStatus().equals("rejected")){
            if(status.equals("approved")) offset++;
        }else if(user.getStatus().equals("pending")){
            if(status.equals("approved")) offset++;
        }

        jdbcTemplate.update("UPDATE eventroles SET SlotsLeft = ? where EventName = ? AND Role = ? " , new Object[] {
                getRoleDetails(user.getEventSignedUpIn() , user.getRole()).getNumAvailableSpots() - offset, user.getEventSignedUpIn()
                 , user.getRole()
        });

        jdbcTemplate.update("UPDATE Events set SlotsLeft = ? WHERE Name = ?" , new Object[]{
                loadEventByName(user.getEventSignedUpIn()).getSlotsAvaliable() - offset , user.getEventSignedUpIn()
        });

    }

    public void signUpUserForEvent(userInEvent userInEvent){
        jdbcTemplate.update("INSERT into EventSignUps(FirstName , LastName , Role , EventName , Comment, Status , RewardApproved) VALUES(? , ? , ? , ? , ? , ? , ?)" ,
                new Object[] {userInEvent.getFirstName() , userInEvent.getLastName() , userInEvent.getRole() , userInEvent.getEventSignedUpIn() ,
                        userInEvent.getComment() , userInEvent.getStatus() , 0});
    }

    public void deleteUserFromEvent(userInEvent userInEvent){
        System.out.println("status for user leaving: " + userInEvent.getStatus());
        int offset = userInEvent.getStatus().equals("approved") ? 1 : 0;
        System.out.println("offset" + String.valueOf(offset));
        int rolespotsleft = getRoleDetails(userInEvent.getEventSignedUpIn(), userInEvent.getRole()).getNumAvailableSpots();
        System.out.println("num spots left" + String.valueOf(rolespotsleft));
        System.out.println("Event name:" + userInEvent.getEventSignedUpIn());

        if(offset > 0) {
            jdbcTemplate.update("UPDATE eventroles SET SlotsLeft = ? where EventName = ? ", new Object[]{
                    getRoleDetails(userInEvent.getEventSignedUpIn(), userInEvent.getRole()).getNumAvailableSpots() + offset, userInEvent.getEventSignedUpIn()
            });

            jdbcTemplate.update("UPDATE events SET SlotsLeft = ? where Name = ? ", new Object[]{
                    loadEventByName(userInEvent.getEventSignedUpIn()).getSlotsAvaliable() + offset, userInEvent.getEventSignedUpIn()
            });
        }

        jdbcTemplate.update("DELETE FROM EventSignUps WHERE FirstName = ? AND LastName = ? AND EventName = ?" , new Object[] {
           userInEvent.getFirstName() , userInEvent.getLastName() , userInEvent.getEventSignedUpIn()
        });

    }

    public void updateEvent(String initialEventName , Event event){
        jdbcTemplate.update("Update Events set Name = ? ,Location = ? , Type = ? , StartingDate = ? , StartingTime = ? , EndingDate = ? , EndingTime = ?, SignUpStartDate = ?, SignUpStartTime = ?, SignUpEndDate = ? , SignUpEndTime = ? , SlotsLeft = ? , Description = ? , Reward = ? where Name = ?"
        , event.getEventName() , event.getLocation() , event.getEventType() , event.getStartingDate() , event.getStartingTime() , event.getEndingDate() ,
                event.getEndTime(), event.getSignUpStartDate(), event.getSignUpStartTime(), event.getSignUpEndDate(), event.getSignUpEndTime() , event.getSlotsAvaliable() , event.getDescription() , event.getReward() , initialEventName);
        deleteRoles(initialEventName);
        addRoles(event.getRoles() , event.getEventName());
    }

    public void deleteRoles(String eventName){
        jdbcTemplate.update("Delete from EventRoles where EventName = ?" , eventName);
    }

    public void addRoles(ArrayList<Role> roles , String eventName){
        for(Role r : roles){
            jdbcTemplate.update("insert into EventRoles(Role , Prereq , SlotsLeft , Description , EventName) values(?,?,?,?,?)" ,
                    r.getRoleName() , r.getPrereq() , r.getNumAvailableSpots() , r.getDescription() , eventName);
        }
    }

    public List<exportEventDetails> getAllExportEventDetailsWithStatus(String firstName , String lastName , String status){
        return jdbcTemplate.query("SELECT * FROM Requests WHERE FirstName = ? AND LastName = ? AND Status = ?" , new Object[] {firstName , lastName , status} , new ExportEventDetailsMapper());
    }

    public List<Request> getAllRequestsWithStatus(String status) {
        return jdbcTemplate.query("Select * from Requests where Status = ?", new Object[]{status}, new RequestsRowMapper());
    }

    public Event loadEventByName(String eventName){
        return jdbcTemplate.query("Select * from Events where Name = ?" , new Object[] {eventName} , new EventMapper()).get(0);
    }

    public void updateRequestData(Request request , String requestDecision){
        System.out.println(request.getFirstName() + request.getLastName() + request.getYearBound());
        List<Double[]> YearByYearRS = jdbcTemplate.query("SELECT * FROM yearbyyearoutreachfundraised WHERE FirstName = ? AND LastName = ? AND RoboticsYear = ?" ,
                new Object[] {request.getFirstName() , request.getLastName(), request.getYearBound()} , (rs , row) ->
                        new Double[] {rs.getDouble(3) , rs.getDouble(4) , rs.getDouble(5) , rs.getDouble(6)});
        List<Double[]> MemberRS = jdbcTemplate.query("SELECT * FROM MiddletonRoboticsMembers WHERE FirstName = ? AND LastName = ?" ,
                new Object[] {request.getFirstName() , request.getLastName()} ,
                (rs , row) -> new Double[]{rs.getDouble(9) , rs.getDouble(10) , Double.valueOf(rs.getInt(11))});

        System.out.println(Util.getYearBound());
        System.out.println(YearByYearRS.get(0));
        double initialTotalMemberHours = MemberRS.get(0)[0];
        double initialTotalMoneyRaised = MemberRS.get(0)[1];

        double initialNumEventsAttended = MemberRS.get(0)[2];

        double initialYearHoursEarned = YearByYearRS.get(0)[0];
        double initialYearTotalMoneyRaised = YearByYearRS.get(0)[1];

        double initialHoursWaitingApproval = YearByYearRS.get(0)[2];
        double initialFundraisingWaitingApproval = YearByYearRS.get(0)[3];

        switch(requestDecision){
            case "approved":
                switch(request.getEventType()){
                    case "Outreach Event":
                        initialTotalMemberHours += request.getRewardEarned();
                        if(request.getStatus().equals("pending")) {
                            initialHoursWaitingApproval -= request.getRewardEarned();
                        }
                        initialNumEventsAttended += 1;
                        initialYearHoursEarned += request.getRewardEarned();
                        break;
                    case "Fundraising Event":
                        initialTotalMoneyRaised += request.getRewardEarned();
                        if(request.getStatus().equals("pending")) {
                            initialFundraisingWaitingApproval -= request.getRewardEarned();
                        }
                        initialNumEventsAttended += 1;
                        initialYearTotalMoneyRaised += request.getRewardEarned();
                        break;
                }
                break;
            case "rejected":
                switch(request.getEventType()){
                    case "Outreach Event":
                        if(request.getStatus().equals("approved")){
                            initialTotalMemberHours -= request.getRewardEarned();
                            initialYearHoursEarned -= request.getRewardEarned();
                            initialNumEventsAttended -= 1;
                        }
                        initialHoursWaitingApproval -= request.getRewardEarned();
                        break;
                    case "Fundraising Event":
                        if(request.getStatus().equals("approved")){
                            initialYearTotalMoneyRaised -= request.getRewardEarned();
                            initialTotalMoneyRaised -= request.getRewardEarned();
                            initialNumEventsAttended -= 1;
                        }
                        initialFundraisingWaitingApproval -= request.getRewardEarned();
                        break;
                }
                break;
        }

        if(initialFundraisingWaitingApproval < 0) initialFundraisingWaitingApproval = 0;
        if(initialHoursWaitingApproval < 0) initialHoursWaitingApproval = 0;
        if(initialTotalMemberHours < 0) initialTotalMemberHours = 0;
        if(initialTotalMoneyRaised < 0) initialTotalMoneyRaised = 0;
        if(initialYearHoursEarned < 0) initialYearHoursEarned = 0;
        if(initialYearTotalMoneyRaised < 0) initialYearTotalMoneyRaised = 0;
        if(initialNumEventsAttended < 0) initialNumEventsAttended = 0;

        jdbcTemplate.update("UPDATE YearByYearOutreachFundraised SET OutreachHoursEarned = ? , MoneyFundraised = ? , OutreachHoursAwaitingApproval = ? , FundraisedMoneyAwaitingApproval = ? WHERE FirstName = ? AND LastName = ? AND RoboticsYear = ?",
                initialYearHoursEarned , initialYearTotalMoneyRaised , initialHoursWaitingApproval , initialFundraisingWaitingApproval , request.getFirstName() , request.getLastName(),
                Util.getYearBound());
        jdbcTemplate.update("UPDATE Requests SET Status = ? WHERE FirstName = ? AND LastName = ? AND EventAttended = ?" , requestDecision , request.getFirstName() ,
                request.getLastName() , request.getEventAttended());
        jdbcTemplate.update("UPDATE eventsignups set RewardApproved = ? WHERE FirstName = ? AND LastName = ? AND EventName = ?" ,
                new Object[]{requestDecision == "rejected" ? 0 : 1  , request.getFirstName() , request.getLastName() , request.getEventAttended()});
        jdbcTemplate.update("UPDATE MiddletonRoboticsMembers SET TotalOutreach = ? , TotalFundraised = ? , NumEventsAttended = ? WHERE FirstName = ? AND LastName = ?"
        , initialTotalMemberHours , initialTotalMoneyRaised , initialNumEventsAttended , request.getFirstName() , request.getLastName());

    }

    public void transferToUser(String username , String role , String firstName , String lastName , String email){
        jdbcTemplate.update("UPDATE MiddletonRoboticsMembers SET Enabled = 1 where Username = ? AND emailAddress = ?" , username , email);
        jdbcTemplate.update("INSERT INTO yearbyyearoutreachfundraised(FirstName , LastName , OutreachHoursEarned , MoneyFundraised , OutreachHoursAwaitingApproval , FundraisedMoneyAwaitingApproval , RoboticsYear) VALUES(? , ? , ? , ? , ? , ? , ?)" ,
                new Object[] {firstName , lastName , 0 , 0 , 0, 0 , Util.getYearBound()});
        jdbcTemplate.update("INSERT INTO Authorities(Username , Authority) VALUES(? , ?)" ,
                new Object[] {username , role.equals("Student") ? "ROLE_STUDENT" : "ROLE_ADMIN"});
    }

    public void deleteRegisterAcc(String username){
        jdbcTemplate.update("DELETE FROM MiddletonRoboticsMembers WHERE Username = ? and Enabled = 0" , username);
    }

    public void deleteUser(String username){
        jdbcTemplate.update("DELETE FROM MiddletonRoboticsMembers WHERE Username = ?" , username);
    }

    public int getNumRequests(){
        return jdbcTemplate.query("SELECT COUNT(*) FROM MiddletonRoboticsMembers where Enabled = 0" , new IntMapper()).get(0);
    }

    public List<User> getAllUsers(){
        return jdbcTemplate.query("SELECT * FROM MiddletonRoboticsMembers WHERE Enabled = 1" , new UserRowMapper());
    }

    public List<requestsUser> getAllRegisterUsers(){
        return jdbcTemplate.query("SELECT * FROM MiddletonRoboticsMembers WHERE Enabled = 0" , new RegisterUserMapper());
    }

    public User getUserByUsername(String Username){
        return jdbcTemplate.query("SELECT * from MiddletonRoboticsMembers where Username = ?" , new Object[] {Username} ,
                new UserRowMapper()).get(0);
    }

    public User getUserByName(String firstName , String lastName){
        return jdbcTemplate.query("SELECT * from MiddletonRoboticsMembers where FirstName = ? AND LastName = ?" , new Object[] {firstName , lastName} ,
                new UserRowMapper()).get(0);
    }

    public List<Event> getEventsByName(String firstName , String lastName){
        List<userInEvent> userEvents = jdbcTemplate.query("SELECT * FROM EventSignUps where FirstName = ? AND LastName = ?" , new Object[] {firstName , lastName} ,
                (rs , row) -> new userInEvent(rs.getString("FirstName") ,
                rs.getString("LastName"), rs.getString("Role") ,
                rs.getString("EventName") , rs.getString("Status") , rs.getInt("RewardApproved")));
        List<Event> allEventsDetails = new ArrayList<>();
        for(userInEvent userInEvent : userEvents){
            allEventsDetails.add(loadEventByName(userInEvent.getEventSignedUpIn()));
        }

        return allEventsDetails;
    }

    public List<Request> getAllRequests(){
        return jdbcTemplate.query("SELECT * FROM requests" , new RequestsRowMapper());
    }

    public userInEvent getUserFromEvent(String firstName , String lastName , String eventName){
        return jdbcTemplate.query("SELECT * FROM EventSignUps WHERE Firstname = ? AND LastName = ? AND EventName = ?" , new Object[] {firstName , lastName , eventName}
        , (rs , row) -> new userInEvent(rs.getString("FirstName") ,
                        rs.getString("LastName"), rs.getString("Role") ,
                        rs.getString("EventName") , rs.getString("Status") , rs.getInt("RewardApproved"))).get(0);
    }

    public boolean deleteRequest(Request request){
        jdbcTemplate.update("delete from requests WHERE FirstName = ? AND LastName = ? AND EventAttended = ?" , new Object[] {request.getFirstName() , request.getLastName() ,
        request.getEventAttended()});
        Util.notify("Request successfully cancelled");
        return true;
    }

    public List<detailEvent> getAllUserEventsWithRewardStatus(String firstName , String lastName , int rewardApproved){
        List<userInEvent> userInEventData =  jdbcTemplate.query("select * from EventSignUps WHERE FirstName = ? and LastName = ? AND RewardApproved = ?" , new Object[] {firstName , lastName , rewardApproved} ,
                (rs , row) -> new userInEvent(rs.getString("FirstName") ,
                        rs.getString("LastName"), rs.getString("Role") ,
                        rs.getString("EventName") , rs.getString("Status") , rs.getInt("RewardApproved")));
        List<String> eventNames  = new ArrayList<>();
        List<String> roles  = new ArrayList<>();
        List<detailEvent> allEvents = new ArrayList<>();


        for(userInEvent userInEvent : userInEventData){
            eventNames.add(userInEvent.getEventSignedUpIn());
            roles.add(userInEvent.getRole());
        }

        for(int i = 0 ; i < eventNames.size() - 1 ; i ++){
            allEvents.add(getDetailEventByName(eventNames.get(i) , roles.get(i)));
        }

        if(eventNames.size() != 0){
            allEvents.add(getDetailEventByName(eventNames.get(eventNames.size() - 1) , roles.get(eventNames.size() - 1)));
        }


        return allEvents;
    }

    public void addRequest(Request request , byte[] imageData){
        Blob blob = null;
        try {
            blob = new SerialBlob(imageData);
            jdbcTemplate.update("insert into requests(firstName , lastName , email , roleInEvent , comment, rewardEarned , status, eventAttended , eventType , proofImage) VALUES(? , ? , ? , ? , ? , ? , ? , ? , ? , ?)"  ,
                    new Object[] {request.getFirstName() , request.getLastName() , request.getEmail() , request.getRole(), request.getComment() , request.getRewardEarned()
                            , request.getStatus() , request.getEventAttended() , request.getEventType() , blob});

            List<Double[]> YearByYearRS = jdbcTemplate.query("SELECT * FROM yearbyyearoutreachfundraised WHERE FirstName = ? AND LastName = ? AND RoboticsYear = ?" ,
                    new Object[] {request.getFirstName() , request.getLastName(), request.getYearBound()} , (rs , row) ->
                            new Double[] {rs.getDouble(3) , rs.getDouble(4) , rs.getDouble(5) , rs.getDouble(6)});

            String updateYearByYearQuery = "UPDATE yearbyyearoutreachfundraised";
            double current = 0;

            if(request.getEventType().toLowerCase().equals("outreach event")){
                updateYearByYearQuery += " SET OutreachHoursAwaitingApproval = ?";
                current = YearByYearRS.get(0)[2];
            }else{
                updateYearByYearQuery += " SET FundraisedMoneyAwaitingApproval = ?";
                current = YearByYearRS.get(0)[3];
            }

            updateYearByYearQuery += " WHERE FirstName = ? AND LastName = ? AND RoboticsYear = ?";

            jdbcTemplate.update(updateYearByYearQuery, current + request.getRewardEarned(), request.getFirstName(), request.getLastName(),
                    request.getYearBound());

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}

