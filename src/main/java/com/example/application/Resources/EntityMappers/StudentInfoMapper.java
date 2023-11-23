package com.example.application.Resources.EntityMappers;

import com.example.application.Entities.User.StudentInfo;
import com.example.application.Entities.User.studentDataEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentInfoMapper implements RowMapper<StudentInfo> {
    @Override
    public StudentInfo mapRow(ResultSet resultSet, int i) throws SQLException {
        StudentInfo studentDataEntity = new StudentInfo(resultSet.getString("FirstName"),
                resultSet.getString("LastName") ,
                resultSet.getString("Team")
        );

        return studentDataEntity;
    }
}
