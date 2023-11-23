package com.example.application.Resources.EntityMappers;

import com.example.application.Entities.Event.exportEventDetails;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExportEventDetailsMapper implements RowMapper<exportEventDetails> {
    @Override
    public exportEventDetails mapRow(ResultSet resultSet, int i) throws SQLException {
        exportEventDetails exportEventDetails = new exportEventDetails(1200 , false , true , resultSet.getString("EventAttended") , resultSet.getDouble("RewardEarned"));
        return exportEventDetails;
    }
}
