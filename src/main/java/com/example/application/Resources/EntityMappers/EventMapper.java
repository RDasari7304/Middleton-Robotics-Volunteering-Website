package com.example.application.Resources.EntityMappers;

import com.example.application.Entities.Event.Event;
import com.example.application.Resources.Services.DataService;
import com.example.application.Role;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class EventMapper implements RowMapper<Event> {
    DataService dataService = new DataService();

    @Override
    public Event mapRow(ResultSet resultSet, int i) throws SQLException {
        ArrayList<Role> roles = new ArrayList<>();
        for(Role r : dataService.getRolesForEvent(resultSet.getString("Name"))){
            roles.add(r);
        }
        Event event = new Event(roles , resultSet.getString("Name") ,
                resultSet.getString("Location") , resultSet.getString("Type") , resultSet.getDouble("Reward") ,
                resultSet.getString("StartingDate") ,
                resultSet.getString("StartingTime") , resultSet.getString("EndingDate") , resultSet.getString("EndingTime") ,
                resultSet.getString("SignUpStartDate"), resultSet.getString("SignUpStartTime"), resultSet.getString("SignUpEndDate"), resultSet.getString("SignUpEndTime"), resultSet.getInt("SlotsLeft") , resultSet.getString("Description") , resultSet.getInt("TotalSlotsAvailable"));

        return event;
    }
}
