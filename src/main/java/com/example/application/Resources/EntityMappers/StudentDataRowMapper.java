package com.example.application.Resources.EntityMappers;

import com.example.application.Entities.User.User;
import com.example.application.Entities.User.studentDataEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDataRowMapper implements RowMapper<studentDataEntity> {
    @Override
    public studentDataEntity mapRow(ResultSet resultSet, int i) throws SQLException {
        studentDataEntity studentDataEntity = new studentDataEntity(resultSet.getString("FirstName"),
                resultSet.getString("LastName") ,
                resultSet.getString("emailAddress") ,
                resultSet.getInt("NumEventsAttended") ,
                resultSet.getDouble("TotalOutreach") ,
                resultSet.getDouble("TotalFundraised")
                );

        return studentDataEntity;
    }
}
