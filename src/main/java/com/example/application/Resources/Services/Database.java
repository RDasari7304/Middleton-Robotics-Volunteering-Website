package com.example.application.Resources.Services;

import com.example.application.Entities.Event.Event;
import com.example.application.Entities.Event.Request;
import com.example.application.Entities.Event.detailEvent;
import com.example.application.Entities.Event.exportEventDetails;
import com.example.application.Entities.User.User;
import com.example.application.Entities.User.requestsUser;
import com.example.application.Entities.User.studentDataEntity;
import com.example.application.Entities.User.userInEvent;
import com.example.application.Resources.Extras.EmailSender;
import com.example.application.Resources.Extras.Util;
import com.example.application.Role;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class Database {
    static Connection connection = null;

    /*public static String dbPath = "./MiddletonRoboticsVHO.db";*/
    public static String dbPath = "localhost:3306/middletonroboticswebsite";//"middletonrobovho.cwmulkoh06bc.us-east-2.rds.amazonaws.com/MiddletonRoboVHO"; //"localhost:3306/middletonroboticswebsite";
    public static String username = "root";//"MiddletonRoboVHO";"root";//"MiddletonRoboVHO";
    public static String password = "root";//"MiddletonRoboVHO#1";"root";//"MiddletonRoboVHO#1";


    public static void connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Connection Failed");
            return;
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + dbPath, username, password);
            /*connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);*/
        } catch (SQLException e) {
            System.out.println(e);
            return;
        }

    }

    public static void createAllTablesRoutine(){
        createUsersTable();
        createAuthoritiesTable();
        createEventRoles();
        createRegistrationInEvents();
        createEventsTable();
        createRequest();
    }

    public static void createAuthoritiesTable(){
        String sqlQuery = "CREATE TABLE authorities(username TEXT , authority TEXT)";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createUsersTable(){
        String sqlQuery = "CREATE TABLE middletonroboticsmembers"+
                "(FirstName TEXT,"+
                "LastName TEXT ," +
                "username TEXT," +
                "password TEXT ,"+
                "emailAddress TEXT,"+
                "Team TEXT," +
                "Role TEXT , " +
                "Enabled TINYINT(1)," +
                "TotalOutreach DOUBLE,"+
                "TotalFundraised DOUBLE," +
                "NumEventsAttended INT)";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createEventRoles(){
        String sqlQuery = "CREATE TABLE eventRoles"+
                "(role TEXT,"+
                "prereq TEXT ," +
                "slotsLeft INT," +
                "description TEXT ,"+
                "EventName TEXT )";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createRegistrationInEvents(){
        String sqlQuery = "CREATE TABLE registeredinevents"+
                "(FirstName TEXT,"+
                "LastName TEXT ," +
                "Role TEXT," +
                "EventName TEXT ,"+
                "Status TEXT )";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }/*

    public static void createRegisteredTable(){
        String sqlQuery = "CREATE TABLE register"+
                "(FirstName VARCHAR(5000),"+
                "LastName VARCHAR(255) ," +
                "username VARCHAR(1000)," +
                "password VARCHAR(5000) ,"+
                "emailAddress VARCHAR(5000),"+
                "Team VARCHAR(5000)," +
                "Role VARCHAR(5000) )";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }*/

    public static void createEventsTable(){
        String sqlQuery = "CREATE TABLE events"+
                "(eventName TEXT,"+
                "Location TEXT ," +
                "eventType TEXT," +
                "startingDate TEXT ,"+
                "startingTime TEXT,"+
                "endingDate TEXT," +
                "endingTime TEXT ,"+
                "slotsLeft INT ,"+
                "description TEXT ,"+
                "reward DOUBLE , "+
                "totalSlotsAvailable INT)";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createRequest(){
        String sqlQuery = "CREATE TABLE requests"+
                "(firstName TEXT,"+
                "lastName TEXT ," +
                "email TEXT," +
                "roleInEvent TEXT ,"+
                "comment TEXT,"+
                "rewardEarned DOUBLE," +
                "status TEXT ,"+
                "eventAttended TEXT ,"+
                "eventType TEXT,"+
                "proofImage BLOB)";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void addAuthoritiyForUser(String username ,  boolean admin){
        String sqlQuery = "INSERT INTO Authorities (Username , Authority) VALUES(? , ?)";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1 , username);
            preparedStatement.setString(2 , admin ? "ROLE_ADMIN" : "ROLE_STUDENT");

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getNumOfUsersSignedUpIn(String eventName) {
        String selectQuery = "SELECT COUNT(*) FROM EventSignUps where EventName = ?";

        if (connection != null) {
            try {
                PreparedStatement selecter = connection.prepareStatement(selectQuery);
                selecter.setString(1, eventName);

                ResultSet rs = selecter.executeQuery();
                rs.next();

                return rs.getInt(1);


            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection is null!");
        }

        return 0;
    }

    public static void addUser(String firstName, String lastName, String userName, String password, String email, String teamName, String Role , boolean registering) {
        if (connection != null) {
            String query = "Insert into MiddletonRoboticsMembers(FirstName , LastName , Username , Password , emailAddress , Team , Role , Enabled , TotalOutreach , TotalFundraised) values(?, ?, ?, ? ,? ,? , ? , ? , 0 , 0)";
            String query2 = "Insert into YearByYearOutreachFundraised(FirstName , LastName , OutreachHoursEarned , MoneyFundraised , OutreachHoursAwaitingApproval , FundraisedMoneyAwaitingApproval , RoboticsYear) values(?, ?, ?, ? ,? ,? , ? )";
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


            String encryptedPassword = passwordEncoder.encode(password);

            try {
                PreparedStatement insertStatement = connection.prepareStatement(query);
                insertStatement.setString(1, firstName);
                insertStatement.setString(2, lastName);
                insertStatement.setString(3, userName);
                insertStatement.setString(4, encryptedPassword);
                insertStatement.setString(5, email);
                insertStatement.setString(6, teamName);
                insertStatement.setString(7, Role);
                insertStatement.setInt(8, registering ? 0 : 1);

                insertStatement.executeUpdate();

                PreparedStatement insertStatement2 = connection.prepareStatement(query2);
                insertStatement2.setString(1 , firstName);
                insertStatement2.setString(2 , lastName);
                insertStatement2.setDouble(3 , 0);
                insertStatement2.setDouble(4 , 0);
                insertStatement2.setDouble(5 , 0);
                insertStatement2.setDouble(6 , 0);



                insertStatement2.setString(7 , Util.getYearBound());
                insertStatement2.executeUpdate();

                boolean admin = false;

                if(Role.equals("Adult")){
                    admin = true;
                }

                addAuthoritiyForUser(userName , admin);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection is null");
        }
    }

    public static User getByUsername(String username) {
        String query = "SELECT * from MiddletonRoboticsMembers where Username = ?";

        if (connection != null) {
            try {
                PreparedStatement selectQuery = connection.prepareStatement(query);
                selectQuery.setString(1, username);

                ResultSet rs = selectQuery.executeQuery();

                if(rs != null ) {
                    rs.next();
                    String firstname = rs.getString(1);
                    String lastName = rs.getString(2);
                    String password = rs.getString(4);
                    String email = rs.getString(5);
                    double totalOutreachEarned = rs.getDouble(9);
                    double totalMoneyRaised = rs.getDouble(10);
                    int numEventsAttended = rs.getInt(11);

                    return new User(firstname, lastName, username, password, email , totalOutreachEarned , totalMoneyRaised , numEventsAttended);
                }

                return null;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection is null!");
        }

        return null;
    }

    public static User getUserByName(String firstName , String lastName) {
        String query = "SELECT * from MiddletonRoboticsMembers where FirstName = ? AND LastName = ?";

        if (connection != null) {
            try {
                PreparedStatement selectQuery = connection.prepareStatement(query);
                selectQuery.setString(1, firstName);
                selectQuery.setString(2 , lastName);

                ResultSet rs = selectQuery.executeQuery();

                if(rs != null ) {
                    rs.next();
                    String password = rs.getString(4);
                    String email = rs.getString(5);
                    double totalOutreachEarned = rs.getDouble(9);
                    double totalMoneyRaised = rs.getDouble(10);
                    int numEventsAttended = rs.getInt(11);

                    return new User(firstName, lastName, username, password, email , totalOutreachEarned , totalMoneyRaised , numEventsAttended);
                }

                return null;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection is null!");
        }

        return null;
    }

    public static ArrayList<studentDataEntity> getAllStudentEntities(){
        String sqlQuery = "SELECT * FROM MiddletonRoboticsMembers WHERE Enabled = 1";
        ArrayList<studentDataEntity> entities = new ArrayList<>();

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            ResultSet rs = null;

            rs = preparedStatement.executeQuery();

            if(rs != null){
                while(rs.next()){
                    String firstName = rs.getString(1);
                    String lastName = rs.getString(2);
                    String email = rs.getString(5);
                    double hoursEarned = rs.getDouble(9);
                    double moneyRaised = rs.getDouble(10);
                    int numEventsAttended = rs.getInt(11);

                    entities.add(new studentDataEntity(firstName , lastName , email  , numEventsAttended , hoursEarned , moneyRaised));

                }
            }

            return entities;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entities;
    }

    public static int getNumOfRequests() {
        String getRowsQuery = "SELECT COUNT(*) FROM MiddletonRoboticsMembers where Enabled = 0";
       /* String getRowsQuery = "SELECT count(*) FROM register";*/

        if (connection != null) {
            try {
                PreparedStatement query = connection.prepareStatement(getRowsQuery);

                ResultSet rs = query.executeQuery();

                rs.next();

                return rs.getInt(1);


            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("connection is null");
            return 0;
        }

        return 0;
    }

    public static void deleteUser(String Username) {
        if (connection != null) {
            String deleteQuery = "DELETE FROM MiddletonRoboticsMembers WHERE Username = ?";
            try {
                PreparedStatement delete = connection.prepareStatement(deleteQuery);
                delete.setString(1, Username);

                delete.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("connection is null");
            return;

        }
    }

    public static void deleteRegisterAcc(String Username) {
        if (connection != null) {
            String deleteQuery = "DELETE FROM MiddletonRoboticsMembers WHERE Username = ? and Enabled = 0";
            /*
            String deleteQuery = "DELETE FROM register WHERE username = ?";*/
            try {
                PreparedStatement delete = connection.prepareStatement(deleteQuery);
                delete.setString(1, Username);

                delete.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("connection is null");
            return;

        }
    }

    public static ArrayList<User> getAllUsers() {
        ArrayList<User> usersArray = new ArrayList<>();
        if (connection != null) {
            String getAllUsers = "SELECT * FROM MiddletonRoboticsMembers WHERE Enabled = 1";
            try {
                ResultSet rss = null;
                PreparedStatement executeGetAllUsers = connection.prepareStatement(getAllUsers);

                rss = executeGetAllUsers.executeQuery();

                if (rss != null) {
                    while (rss.next()) {
                        String FirstName = rss.getString(1);
                        String LastName = rss.getString(2);
                        String userName = rss.getString(3);
                        String password = rss.getString(4);
                        String email = rss.getString(5);
                        String teamName = rss.getString(6);
                        String role = rss.getString(7);
                        double totalOutreachEarned = rss.getDouble(9);
                        double totalMoneyRaised = rss.getDouble(10);
                        int numEventsAttended = rss.getInt(11);

                        usersArray.add(new User(FirstName, LastName, userName, password, teamName, email, role , totalOutreachEarned , totalMoneyRaised , numEventsAttended));
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error inserting into table");
            }


        } else {
            return new ArrayList();
        }

        return usersArray;
    }

    public static void changeRole(User user, String role, String EventName) {
        String alterQuery = "UPDATE EventSignUps Set Role = ? where FirstName = ? AND LastName = ? AND EventName = ?";

        if (connection != null) {
            try {
                PreparedStatement alter = connection.prepareStatement(alterQuery);
                alter.setString(1, role);
                alter.setString(2, user.getFirstName());
                alter.setString(3, user.getLastName());
                alter.setString(4, EventName);

                alter.executeUpdate();


            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Connection is null");
        }
    }

    public static void commit() {
        if (connection != null) {
            try {
                PreparedStatement commitStatement = connection.prepareStatement("COMMIT");
                commitStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }
        } else {
            System.out.println("connection is null");
            return;
        }
    }

    public static ArrayList<requestsUser> getAllRegisterUsers() {
        ArrayList<requestsUser> usersArray = new ArrayList<>();
        if (connection != null) {
            String getAllUsers = "SELECT * FROM MiddletonRoboticsMembers WHERE Enabled = 0";
            /*
            String getAllUsers = "SELECT * FROM register";*/
            try {
                ResultSet rss = null;
                PreparedStatement executeGetAllUsers = connection.prepareStatement(getAllUsers);

                rss = executeGetAllUsers.executeQuery();

                if (rss != null) {
                    while (rss.next()) {
                        String FirstName = rss.getString(1);
                        String LastName = rss.getString(2);
                        String userName = rss.getString(3);
                        String password = rss.getString(4);
                        String email = rss.getString(5);
                        String teamName = rss.getString(6);
                        String role = rss.getString(7);

                        usersArray.add(new requestsUser(FirstName, LastName, userName, password, teamName, email, role));
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error inserting into table");
            }


        } else {
            return new ArrayList();
        }

        return usersArray;
    }

    public static void addUserToRegister(User user) {
        String insertQuery = "Insert into MiddletonRoboticsMembers(FirstName , LastName , username , password , emailAddress , Team , Role , Enabled) values(?,?,?,?,?,?,? ,0)";
        if (connection != null) {
            try {
                PreparedStatement insert = connection.prepareStatement(insertQuery);
                insert.setString(1, user.getFirstName());
                insert.setString(2, user.getLastName());
                insert.setString(3, user.getUsername());
                insert.setString(4, user.getPassword());
                insert.setString(5, user.getEmail());
                insert.setString(6, user.getTeam());
                insert.setString(7, user.getRole());

                insert.executeUpdate();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static void registerUserInEvent(User user, String role, String eventName , String status) {
        String insertQuery = "Insert into EventSignUps(FirstName , LastName , Role , EventName , Status) values(?,?,?,?,?)";

        if (connection != null) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);

                preparedStatement.setString(1, user.getFirstName());
                preparedStatement.setString(2, user.getLastName());
                preparedStatement.setString(3, role);
                preparedStatement.setString(4, eventName);
                preparedStatement.setString(5, status);

                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Connection is null!");
            return;
        }
    }

    public static ArrayList<userInEvent> getAllRegisteredInEvent(Event event) {
        String selectQuery = "Select * from EventSignUps where EventName = ?";
        ArrayList<userInEvent> usersRegisteredInEvent = new ArrayList<>();
        if (connection != null) {
            try {
                ResultSet rss = null;

                PreparedStatement statement = connection.prepareStatement(selectQuery);
                statement.setString(1, event.getEventName());

                rss = statement.executeQuery();

                if (rss != null) {
                    while (rss.next()) {
                        String FirstName = rss.getString(1);
                        String LastName = rss.getString(2);
                        String Role = rss.getString(3);
                        String eventName = rss.getString(4);
                        String status = rss.getString(5);
/*
                        usersRegisteredInEvent.add(new userInEvent(FirstName, LastName, Role, eventName , status));*/
                    }
                }

                return usersRegisteredInEvent;

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Connection is null!");
            return new ArrayList<>();
        }

        return usersRegisteredInEvent;
    }

    public static ArrayList<userInEvent> getAllRegisteredInEventWithStatus(Event event , String Status) {
        String selectQuery = "Select * from EventSignUps where EventName = ? AND Status = ?";
        ArrayList<userInEvent> usersRegisteredInEvent = new ArrayList<>();
        if (connection != null) {
            try {
                ResultSet rss = null;

                PreparedStatement statement = connection.prepareStatement(selectQuery);
                statement.setString(1, event.getEventName());
                statement.setString(2 , Status);

                rss = statement.executeQuery();

                if (rss != null) {
                    while (rss.next()) {
                        String FirstName = rss.getString(1);
                        String LastName = rss.getString(2);
                        String Role = rss.getString(3);
                        String eventName = rss.getString(4);
                        String status = rss.getString(5);
/*
                        usersRegisteredInEvent.add(new userInEvent(FirstName, LastName, Role, eventName , status));*/
                    }
                }

                return usersRegisteredInEvent;

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Connection is null!");
            return new ArrayList<>();
        }

        return usersRegisteredInEvent;
    }

    public static ArrayList<detailEvent>  getAllUserEvents(String firstName , String lastName){
        String selectQuery = "select * from Requests where FirstName = ? AND LastName = ? AND Status = ?";
        ArrayList<detailEvent> allEventsUserAttended = new ArrayList<>();
        ArrayList<String> eventNames = new ArrayList<>();
        ArrayList<String> role = new ArrayList<>();

        if(connection !=null){
            try {
                PreparedStatement registeredSelect = connection.prepareStatement(selectQuery);
                registeredSelect.setString(1 , firstName);
                registeredSelect.setString(2 , lastName);
                registeredSelect.setString(3 , "approved");
                ResultSet rs = null;
                rs = registeredSelect.executeQuery();

                if(rs != null){
                    while(rs.next()){
                        eventNames.add(rs.getString(8));
                        role.add(rs.getString(4));
                    }
                }

                for(int i = 0 ; i < eventNames.size() - 1; i ++){
                    allEventsUserAttended.add(getEventByName(eventNames.get(i) , role.get(i)));
                }

                if(eventNames.size() != 0){
                    allEventsUserAttended.add(getEventByName(eventNames.get(eventNames.size() - 1) , role.get(eventNames.size() - 1)));
                }

                return allEventsUserAttended;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else System.out.println("Connection is null!");

        return new ArrayList<>();
    }

    public static detailEvent getEventByName(String name , String role){
        String selectQuery = "Select * from Events where Name = ?";

        if(connection != null){
            try {
                PreparedStatement select = connection.prepareStatement(selectQuery);
                select.setString(1 , name);

                ResultSet rs = null;
                rs = select.executeQuery();
                rs.next();

                String eventName = rs.getString(1);
                String eventType = rs.getString(3);
                String description = rs.getString(9);
                String startingDate = rs.getString(4);
                double reward = rs.getDouble(10);

                return new detailEvent(eventName , startingDate, eventType, reward , description , role);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }else{
            System.out.println("Connection is null!");
        }
        return null;
    }

    public static boolean hasSameUserName(String userName) {
        ArrayList<User> allUsers = getAllUsers();
        ArrayList<requestsUser> registers = getAllRegisterUsers();
        for (User user : allUsers) {
            if (user.getUsername().toLowerCase().equals(userName.toLowerCase())) return true;
        }
        for (requestsUser user : registers) {
            if (user.getUsername().toLowerCase().equals(userName.toLowerCase())) return true;
        }
        return false;
    }

    public static void transferToUser(String Username, String Email) {/*
        String selectQuery = "SELECT * FROM register WHERE username = ? AND emailAddress = ?";*/
        String selectQuery = "UPDATE MiddletonRoboticsMembers SET Enabled = 1 where Username = ? AND emailAddress = ?";

        if (connection != null) {
            try {
                PreparedStatement select = connection.prepareStatement(selectQuery);
                select.setString(1, Username);
                select.setString(2, Email);

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Connection is null!");
            return;
        }

    }
/*
    public static ArrayList<Event> getAllEvents() {
        String selectQuery = "select * from Events";
        ArrayList<Event> allEvents = new ArrayList<>();

        if (connection != null) {
            try {
                ResultSet rs = null;
                PreparedStatement select = connection.prepareStatement(selectQuery);

                rs = select.executeQuery();

                if (rs != null) {
                    while (rs.next()) {
                        String evtName = rs.getString(1);
                        String location = rs.getString(2);
                        String type = rs.getString(3);
                        String starting_date = rs.getString(4);
                        String starting_time = rs.getString(5);
                        String ending_date = rs.getString(6);
                        String ending_time = rs.getString(7);
                        int slotsLeft = rs.getInt(8);
                        String description = rs.getString(9);
                        double reward = rs.getDouble(10);
                        int totalSlotsAvailable = rs.getInt(11);

                        allEvents.add(new Event(getAllEventRoles(evtName), evtName, location, type,
                                reward, starting_date, starting_time, ending_date, ending_time, slotsLeft, description , totalSlotsAvailable));
                    }
                }

                return allEvents;

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Connection is null!");
        }

        return allEvents;
    }


    public static ArrayList<Role> getAllEventRoles(String eventName) {
        String selectQuery = "select * from EventRoles where EventName = ?";
        ArrayList<Role> evtRoles = new ArrayList<>();

        if (connection != null) {
            try {
                ResultSet rs = null;
                PreparedStatement select = connection.prepareStatement(selectQuery);
                select.setString(1, eventName);

                rs = select.executeQuery();

                if (rs != null) {
                    while (rs.next()) {
                        String roleName = rs.getString(1);
                        String prereq = rs.getString(2);
                        int slotsLeft = rs.getInt(3);
                        String description = rs.getString(4);
                        String event = rs.getString(5);

                        evtRoles.add(new Role(roleName, prereq, description, event, slotsLeft));
                    }
                }

                return evtRoles;

            } catch (SQLException e) {
                e.printStackTrace();
            }


        } else {
            System.out.println("Connection is null!");
        }

        return evtRoles;
    }

    public static void updateEvent(String initialEventName, Event event) {
        String updateQuery = "Update Events set Name = ? ,Location = ? , Type = ? , StartingDate = ? , StartingTime = ? , EndingDate = ? , EndingTime = ? , SlotsLeft = ? , Description = ? , Reward = ? where Name = ? ";

        if (connection != null) {
            try {
                PreparedStatement update = connection.prepareStatement(updateQuery);
                update.setString(1, event.getEventName());
                update.setString(2, event.getLocation());
                update.setString(3, event.getEventType());
                update.setString(4, event.getStartingDate());
                update.setString(5, event.getStartingTime());
                update.setString(6, event.getEndingDate());
                update.setString(7, event.getEndTime());
                update.setInt(8, event.getSlotsAvaliable());
                update.setString(9, event.getDescription());
                update.setDouble(10, event.getReward());
                update.setString(11, initialEventName);

                update.executeUpdate();

                deleteRoles(initialEventName);
                addRoles(event.getRoles(), event.getEventName());

            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("Connection is null!");
        }
    }

    public static Event loadEventByName(String eventName) {

        String selectQuery = "Select * from Events where Name = ?";

        if (connection != null) {
            try {
                PreparedStatement select = connection.prepareStatement(selectQuery);
                select.setString(1, eventName);

                ResultSet rs = select.executeQuery();

                rs.next();
                String location = rs.getString(2);
                String type = rs.getString(3);
                String starting_date = rs.getString(4);
                String starting_time = rs.getString(5);
                String ending_date = rs.getString(6);
                String ending_time = rs.getString(7);
                int slotsLeft = rs.getInt(8);
                String description = rs.getString(9);
                double reward = rs.getDouble(10);
                int totalSlotsAvailable = rs.getInt(11);

                ArrayList<Role> roles = getAllEventRoles(eventName);

                return new Event(roles, eventName, location, type, reward, starting_date, starting_time, ending_date, ending_time, slotsLeft, description , totalSlotsAvailable);


            } catch (SQLException e) {
                e.printStackTrace();
            }


        } else {
            System.out.println("Connection is null");
        }

        return null;
    }*/

    public static ArrayList<Request> getAllRequests() {
        String selectQuery = "Select * from Requests";
        ArrayList<Request> allRequests = new ArrayList<>();
        if (connection != null) {
            try {
                PreparedStatement select = connection.prepareStatement(selectQuery);
                ResultSet rs = null;
                rs = select.executeQuery();

                if (rs != null) {
                    while (rs.next()) {
                        String firstName = rs.getString(1);
                        String lastName = rs.getString(2);
                        String email = rs.getString(3);
                        String roleInEvent = rs.getString(4);
                        String comment = rs.getString(5);
                        double rewardEarned = rs.getDouble(6);
                        String status = rs.getString(7);
                        String eventAttended = rs.getString(8);
                        String eventType = rs.getString(9);
                        byte[] proofImage = rs.getBytes(10);

                        Image proofAsVaadinImage = null;

                        if(proofImage != null){

                            StreamResource streamResource = new StreamResource("isr", new InputStreamFactory() {
                                @Override
                                public InputStream createInputStream() {
                                    return new ByteArrayInputStream(proofImage);
                                }
                            });

                            proofAsVaadinImage = new Image(streamResource , "didn't work");
                        }

                        if(proofAsVaadinImage != null){
                            allRequests.add(new Request(firstName , lastName , email , roleInEvent , comment , rewardEarned , status , eventAttended , eventType ,"" ,  proofAsVaadinImage));
                        }

                    }
                }

                return allRequests;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection is null!");
        }

        return allRequests;

    }

    public static Role getRoleDetails(String event, String role){
        String sqlQuery = "SELECT * FROM EventRoles WHERE EventName = ? AND Role = ?";

        try{
            PreparedStatement select = connection.prepareStatement(sqlQuery);
            select.setString(1 , event);
            select.setString(2 , role);

            ResultSet rs = null;

            rs = select.executeQuery();


            if(rs != null){
                while(rs.next()) {
                    return new Role(rs.getString(1) , rs.getString(2) , rs.getString(4) , rs.getString(5) , rs.getInt(3));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Role(null , null , null , null , 0);
    }

    public static void addEvent(Event event){
        String insertQuery = "insert into Events(Name , Location , Type , StartingDate , StartingTime , EndingDate , EndingTime , SlotsLeft , Reward , Description , TotalSlotsAvailable) values (?,?,?,?,?,?,?,?,?,? , ?)";
        String insertEvtRoles = "insert into EventRoles(Role , Prereq , SlotsLeft , Description , EventName) values(?,?,?,?,?)";

        if(connection != null){
            try {
                PreparedStatement insert = connection.prepareStatement(insertQuery);
                insert.setString(1 , event.getEventName());
                insert.setString(2 , event.getLocation());
                insert.setString(3 , event.getEventType());
                insert.setString(4 , event.getStartingDate());
                insert.setString(5 , event.getStartingTime());
                insert.setString(6 , event.getEndingDate());
                insert.setString(7 , event.getEndTime());
                insert.setInt(8 , event.getSlotsAvaliable());
                insert.setDouble(9 , event.getReward());
                insert.setString(10 , event.getDescription());
                insert.setInt(11 , event.getTotalSlotsAvailable());

                insert.executeUpdate();

                PreparedStatement insertRole = connection.prepareStatement(insertEvtRoles);;

                for(int i = 0 ; i < event.getRoles().size() - 1 ; i ++){
                    insertRole.setString(1 , event.getRoles().get(i).getRoleName());
                    insertRole.setString(2 , event.getRoles().get(i).getPrereq());
                    insertRole.setInt(3 , event.getRoles().get(i).getNumAvailableSpots());
                    insertRole.setString(4 , event.getRoles().get(i).getDescription());
                    insertRole.setString(5 , event.getRoles().get(i).getEventName());

                    insertRole.executeUpdate();

                }

                insertRole.setString(1 , event.getRoles().get(event.getRoles().size() - 1).getRoleName());
                insertRole.setString(2 , event.getRoles().get(event.getRoles().size() - 1).getPrereq());
                insertRole.setInt(3 , event.getRoles().get(event.getRoles().size() - 1).getNumAvailableSpots());
                insertRole.setString(4 , event.getRoles().get(event.getRoles().size() - 1).getDescription());
                insertRole.setString(5 , event.getRoles().get(event.getRoles().size() - 1).getEventName());

                insertRole.executeUpdate();


            } catch (SQLException e) {
                e.printStackTrace();
            }

        }else{
            System.out.println("Connection is null!");
        }
    }

    public static void deleteRoles(String intitialEventName){
        String deleteQuery = "Delete from EventRoles where EventName = ?";

        if(connection != null){
            try {
                PreparedStatement delete = connection.prepareStatement(deleteQuery);
                delete.setString(1 , intitialEventName);

                delete.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Connection is null!");
        }
    }

    //this is bad coding but whatever
    public static void addRoles(ArrayList<Role> roles , String eventName){
        String insertQuery = "insert into EventRoles(Role , Prereq , SlotsLeft , Description , EventName) values(?,?,?,?,?)";
        if(connection != null){
            PreparedStatement insert;
            for(int i = 0 ; i < roles.size() - 1; i++){
                try {
                    insert = connection.prepareStatement(insertQuery);
                    insert.setString(1 , roles.get(i).getRoleName());
                    insert.setString(2 , roles.get(i).getPrereq());
                    insert.setInt(3 , roles.get(i).getNumAvailableSpots());
                    insert.setString(4 , roles.get(i).getDescription());
                    insert.setString(5 , eventName);

                    insert.executeUpdate();


                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            try {
                insert = connection.prepareStatement(insertQuery);
                insert.setString(1 , roles.get(roles.size() -1 ).getRoleName());
                insert.setString(2 , roles.get(roles.size() - 1).getPrereq());
                insert.setInt(3 , roles.get(roles.size() - 1).getNumAvailableSpots());
                insert.setString(4 , roles.get(roles.size() - 1).getDescription());
                insert.setString(5 , eventName);

                insert.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }


        }else{
            System.out.println("Connection is null!");
        }
    }

    public static void addRequest(Request request){
        String insertQuery = "insert into Requests(FirstName , LastName , Email , RoleInEvent , Comment , RewardEarned , Status , EventAttended , EventType , ProofImage) values(?,?,?,?,?,?,?,?,?,?)";
        String insertQuery2 = "UPDATE YearByYearOutreachFundraised SET OutreachHoursAwaitingApproval = ? , FundraisedMoneyAwaitingApproval = ? WHERE FirstName = ? AND LastName = ? AND RoboticsYear = ?";


        if(connection != null){
            try {
                PreparedStatement insert = connection.prepareStatement(insertQuery);
                insert.setString( 1 , request.getFirstName());
                insert.setString(2 , request.getLastName());
                insert.setString(3 , request.getEmail());
                insert.setString(4 , request.getRole());
                insert.setString(5 , request.getComment());
                insert.setDouble(6 , request.getRewardEarned());
                insert.setString(7 , request.getStatus());
                insert.setString(8 , request.getEventAttended());
                insert.setString(9 , request.getEventType());

                FileInputStream fin = null;

                byte[] byteArray;
                try {
                    fin = new FileInputStream(request.getProofImage().getSrc());

                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[1024];
                    while ((nRead = fin.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }

                    buffer.flush();
                    byteArray = buffer.toByteArray();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }


                insert.setBytes(10 , byteArray);

                insert.executeUpdate();

                ArrayList<Double> currentAwaiting = Util.getRequestsArraySum(getAllRequestsForUserWithStatus(request.getFirstName() , request.getLastName() , "pending"));



                PreparedStatement insert2 = connection.prepareStatement(insertQuery2);
                insert2.setDouble( 1 , currentAwaiting.get(0));
                insert2.setDouble( 2 , currentAwaiting.get(1));
                insert2.setString( 3 , request.getFirstName());
                insert2.setString( 4 , request.getLastName());
                insert2.setString( 5  , Util.getYearBound());

                insert2.executeUpdate();


            } catch (SQLException e) {
                e.printStackTrace();
            }

        }else{
            System.out.println("Connection is null");
        }
    }



    public static boolean dbHasUsernameAndEmail(String Username , String Email){
        ArrayList<User> allusers = getAllUsers();

        for(User user : allusers){
            if(user.getUsername().equals(Username)){
                if(user.getEmail().equals(Email)){
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean sendPasswordEmail(String Username , String Email){
        ArrayList<User> allusers = getAllUsers();

        for(User user : allusers){
            if(user.getUsername().equals(Username)){
                if(user.getEmail().equals(Email)){
                    String password = user.getPassword();
                    EmailSender.send(Email , "Middleton Robotics Website Password" , "Hello " + user.getFirstName() + ", The password to your account " + user.getUsername() + " is, " + user.getPassword(), "gamers2333@gmail.com" , "English12");
                    return true;
                }
            }
        }

        return false;

    }

    public static void updateRequestStatus(Request request , String toStatus){
        String sqlQuery = "UPDATE Requests SET Status = ? where FirstName = ? AND LastName = ? AND Email = ? and EventAttended = ? and RoleInEvent = ?";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1 , toStatus);
            preparedStatement.setString(2 , request.getFirstName());
            preparedStatement.setString(3 , request.getLastName());
            preparedStatement.setString(4 , request.getEmail());
            preparedStatement.setString(5 , request.getEventAttended());
            preparedStatement.setString(6 , request.getRole());

            preparedStatement.executeUpdate();



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void sendRequestToJoinEvent(userInEvent user){
        String sqlQuery = "INSERT INTO EventSignUps(FirstName , LastName , Role , EventName , Status) VALUES(? , ? , ? , ? , ?)";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1 , user.getFirstName());
            preparedStatement.setString(2 , user.getLastName());
            preparedStatement.setString(3 , user.getRole());
            preparedStatement.setString(4 , user.getEventSignedUpIn());
            preparedStatement.setString(5 , user.getStatus());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<userInEvent> getRequestsWithStatusInEvent(String eventName , String statusLookingFor){
        String sqlQuery = "SELECT * FROM EventSignUps where EventName = ? AND Status = ?";
        ArrayList<userInEvent> usersInEvent = new ArrayList<>();

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1 , eventName);
            preparedStatement.setString(2 , statusLookingFor);

            ResultSet rs = null;

            rs = preparedStatement.executeQuery();

            if(rs != null){
                while(rs.next()){
                    String firstName = rs.getString(1);
                    String lastName = rs.getString(2);
                    String roleRequested = rs.getString(3);
                    String event = rs.getString(4);
                    String status = rs.getString(5);
/*
                    usersInEvent.add(new userInEvent(firstName , lastName , roleRequested , event , status));*/
                }
            }

            return usersInEvent;


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usersInEvent;

    }


    public static ArrayList<String> getUserDetailsInfo(String firstName , String lastName){
        String sqlQuery = "SELECT * FROM MiddletonRoboticsMembers WHERE FirstName = ? AND LastName = ?";
        String sqlQuery2 = "SELECT COUNT(*) from Requests WHERE FirstName = ? AND LastName = ? AND Status = ?";

        ArrayList<String> userDetails = new ArrayList<>();

        String team = "";
        String numEventsApproved = "";
        String numEventsSignedUp = "";

        try{
            PreparedStatement preparedStatement1 = connection.prepareStatement(sqlQuery);
            preparedStatement1.setString(1 , firstName);
            preparedStatement1.setString(2 , lastName);

            ResultSet rs1 = null;

            rs1 = preparedStatement1.executeQuery();

            if (rs1 != null) {
                rs1.next();

                team = rs1.getString(6);

                switch (team){
                    case "Maelstrom":
                        team = "FTC 3846 - Maelstrom";
                        break;
                }
            }

            PreparedStatement preparedStatement2 = connection.prepareStatement(sqlQuery2);
            preparedStatement2.setString(1 , firstName);
            preparedStatement2.setString(2 , lastName);
            preparedStatement2.setString( 3, "approved");

            ResultSet rs2 = null;

            rs2 = preparedStatement2.executeQuery();

            if(rs2 != null){
                rs2.next();

                numEventsApproved = String.valueOf(rs2.getInt(1));
            }

            sqlQuery2 = "SELECT COUNT(*) from EventSignUps WHERE FirstName = ? AND LastName = ?";

            PreparedStatement preparedStatement3 = connection.prepareStatement(sqlQuery2);
            preparedStatement3.setString(1 , firstName);
            preparedStatement3.setString(2 , lastName);

            ResultSet rs3 = preparedStatement3.executeQuery();

            if(rs3 != null){
                rs3.next();

                numEventsSignedUp = String.valueOf(rs3.getInt(1));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        userDetails.add(firstName);
        userDetails.add(lastName);
        userDetails.add(team);
        userDetails.add(numEventsSignedUp);
        userDetails.add(numEventsApproved);

        return userDetails;

    }

    public static void updateJoinEventStatus(userInEvent user , String status){
        String sqlQuery = "UPDATE EventSignUps SET Status = ? where EventName = ? AND FirstName = ? AND LastName = ?";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1 , status);
            preparedStatement.setString(2 , user.getEventSignedUpIn());
            preparedStatement.setString(3 , user.getFirstName());
            preparedStatement.setString(4 , user.getLastName());

            preparedStatement.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Request> getAllRequestsWithStatus(String Status){
            String selectQuery = "Select * from Requests where Status = ?";
            ArrayList<Request> allRequests = new ArrayList<>();
            if (connection != null) {
                try {
                    PreparedStatement select = connection.prepareStatement(selectQuery);
                    select.setString(1 , Status);
                    ResultSet rs = null;
                    rs = select.executeQuery();

                    if (rs != null) {
                        while (rs.next()) {
                            String firstName = rs.getString(1);
                            String lastName = rs.getString(2);
                            String email = rs.getString(3);
                            String roleInEvent = rs.getString(4);
                            String comment = rs.getString(5);
                            double rewardEarned = rs.getDouble(6);
                            String status = rs.getString(7);
                            String eventAttended = rs.getString(8);
                            String eventType = rs.getString(9);
                            byte[] proofImage = rs.getBytes(10);

                            Image proofAsVaadinImage = null;

                            if(proofImage != null){

                                StreamResource streamResource = new StreamResource("isr", new InputStreamFactory() {
                                    @Override
                                    public InputStream createInputStream() {
                                        return new ByteArrayInputStream(proofImage);
                                    }
                                });

                                proofAsVaadinImage = new Image(streamResource , "didn't work");
                            }

                            if(proofAsVaadinImage != null){
                                allRequests.add(new Request(firstName , lastName , email , roleInEvent , comment , rewardEarned , status , eventAttended , eventType , "" , proofAsVaadinImage));
                            }

                        }
                    }

                    return allRequests;

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Connection is null!");
            }

            return allRequests;


    }

    public static ArrayList<Request> getAllRequestsForUserWithStatus(String FirstName , String LastName , String Status){
        String selectQuery = "Select * from Requests where Status = ? AND FirstName = ? AND LastName = ?";
        ArrayList<Request> allRequests = new ArrayList<>();
        if (connection != null) {
            try {
                PreparedStatement select = connection.prepareStatement(selectQuery);
                select.setString(1 , Status);
                select.setString(2 , FirstName);
                select.setString(3 , LastName);
                ResultSet rs = null;
                rs = select.executeQuery();

                if (rs != null) {
                    while (rs.next()) {
                        String firstName = rs.getString(1);
                        String lastName = rs.getString(2);
                        String email = rs.getString(3);
                        String roleInEvent = rs.getString(4);
                        String comment = rs.getString(5);
                        double rewardEarned = rs.getDouble(6);
                        String status = rs.getString(7);
                        String eventAttended = rs.getString(8);
                        String eventType = rs.getString(9);
                        byte[] proofImage = rs.getBytes(10);

                        Image proofAsVaadinImage = null;

                        if(proofImage != null){

                            StreamResource streamResource = new StreamResource("isr", new InputStreamFactory() {
                                @Override
                                public InputStream createInputStream() {
                                    return new ByteArrayInputStream(proofImage);
                                }
                            });

                            proofAsVaadinImage = new Image(streamResource , "didn't work");
                        }

                        if(proofAsVaadinImage != null){
                            allRequests.add(new Request(firstName , lastName , email , roleInEvent , comment , rewardEarned , status , eventAttended , eventType , "" , proofAsVaadinImage));
                        }

                    }
                }

                return allRequests;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Connection is null!");
        }

        return allRequests;


    }

    public static ArrayList<exportEventDetails> getAllExportEventsWithStatus(String firstName , String lastName , String status){
        String sqlQuery = "SELECT * FROM Requests WHERE FirstName = ? AND LastName = ? AND Status = ?";
        ArrayList<exportEventDetails> allExportEvents = new ArrayList<>();

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1 , firstName);
            preparedStatement.setString(2 , lastName);
            preparedStatement.setString(3 , status);

            ResultSet rs = null;

            rs = preparedStatement.executeQuery();


            if(rs != null){
                while(rs.next()){
                    String eventAttendedName = rs.getString(8);
                    double rewardEarned = rs.getDouble(6);

                    allExportEvents.add(new exportEventDetails(1200 , false , true , eventAttendedName , rewardEarned));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allExportEvents;
    }

    public static void updateRequestData(Request request  , String requestDecision){
        String sqlQuery1 = "UPDATE YearByYearOutreachFundraised SET OutreachHoursEarned = ? , MoneyFundraised = ? , OutreachHoursAwaitingApproval = ? , FundraisedMoneyAwaitingApproval = ? WHERE FirstName = ? AND LastName = ? AND RoboticsYear = ?";
        String sqlQuery2 = "UPDATE Requests SET Status = ? WHERE FirstName = ? AND LastName = ? AND EventAttended = ?";
        String sqlQuery3 = "UPDATE MiddletonRoboticsMembers SET TotalOutreach = ? , TotalFundraised = ? , NumEventsAttended = ? WHERE FirstName = ? AND LastName = ?";
        String sqlQuery4 = "SELECT * FROM YearByYearOutreachFundraised WHERE FirstName = ? AND LastName = ? AND RoboticsYear = ?";
        String sqlQuery5 = "SELECT * FROM MiddletonRoboticsMembers WHERE FirstName = ? AND LastName = ?";


        try{
            PreparedStatement preparedStatement1 = connection.prepareStatement(sqlQuery1);
            PreparedStatement preparedStatement2 = connection.prepareStatement(sqlQuery2);
            PreparedStatement preparedStatement3 = connection.prepareStatement(sqlQuery3);
            PreparedStatement preparedStatement4 = connection.prepareStatement(sqlQuery4);
            PreparedStatement preparedStatement5 = connection.prepareStatement(sqlQuery5);

            preparedStatement4.setString(1 , request.getFirstName());
            preparedStatement4.setString(2 , request.getLastName());
            preparedStatement4.setString(3 , Util.getYearBound());

            preparedStatement5.setString(1 , request.getFirstName());
            preparedStatement5.setString(2 , request.getLastName());

            ResultSet MemberRS = null;
            ResultSet YearByYearRS = null;

            MemberRS = preparedStatement5.executeQuery();
            YearByYearRS = preparedStatement4.executeQuery();


            MemberRS.next();
            YearByYearRS.next();
            double initialTotalMemberHours = MemberRS.getDouble(9);
            double initialTotalMoneyRaised = MemberRS.getDouble(10);

            double initialNumEventsAttended = MemberRS.getInt(11);

            double initialYearHoursEarned = YearByYearRS.getDouble(3);
            double initialYearTotalMoneyRaised = YearByYearRS.getDouble(4);

            double initialHoursWaitingApproval = YearByYearRS.getDouble(5);
            double initialFundraisingWaitingApproval = YearByYearRS.getDouble(6);

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

            preparedStatement1.setDouble(1 , initialYearHoursEarned);
            preparedStatement1.setDouble(2 , initialYearTotalMoneyRaised);
            preparedStatement1.setDouble(3 , initialHoursWaitingApproval);
            preparedStatement1.setDouble(4 , initialFundraisingWaitingApproval);
            preparedStatement1.setString(5 , request.getFirstName());
            preparedStatement1.setString(6 , request.getLastName());
            preparedStatement1.setString(7 , Util.getYearBound());
            preparedStatement1.executeUpdate();


            preparedStatement2.setString(1 , requestDecision);
            preparedStatement2.setString(2 , request.getFirstName());
            preparedStatement2.setString(3 , request.getLastName());
            preparedStatement2.setString(4 , request.getEventAttended());
            preparedStatement2.executeUpdate();

            preparedStatement3.setDouble(1 , initialTotalMemberHours);
            preparedStatement3.setDouble(2 , initialTotalMoneyRaised);
            preparedStatement3.setDouble(3 , initialNumEventsAttended);
            preparedStatement3.setString(4 , request.getFirstName());
            preparedStatement3.setString(5 , request.getLastName());
            preparedStatement3.executeUpdate();



        } catch (SQLException e) {
            e.printStackTrace();
        }


    }



}
