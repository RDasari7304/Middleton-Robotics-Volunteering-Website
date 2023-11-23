package com.example.application.Resources.EntityMappers;

import com.example.application.Entities.User.requestsUser;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterUserMapper implements RowMapper<requestsUser> {
    @Override
    public requestsUser mapRow(ResultSet resultSet, int i) throws SQLException {
        requestsUser requestsUser = new requestsUser(resultSet.getString("FirstName") ,
                resultSet.getString("LastName") ,
                resultSet.getString("Username") ,
                resultSet.getString("Password") ,
                resultSet.getString("Team") ,
                resultSet.getString("emailAddress") ,
                resultSet.getString("Role"));

        return requestsUser;
    }
}
