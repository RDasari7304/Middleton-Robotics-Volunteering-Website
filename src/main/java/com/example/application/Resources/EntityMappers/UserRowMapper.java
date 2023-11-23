package com.example.application.Resources.EntityMappers;

import com.example.application.Entities.User.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User(resultSet.getString("FirstName") ,
                resultSet.getString("LastName") ,
                resultSet.getString("Username") ,
                resultSet.getString("Password") ,
                resultSet.getString("Team") ,
                resultSet.getString("emailAddress") ,
                resultSet.getString("Role") ,
                resultSet.getDouble("TotalOutreach") ,
                resultSet.getDouble("TotalFundraised") ,
                resultSet.getInt("NumEventsAttended"));

        return user;
    }
}
