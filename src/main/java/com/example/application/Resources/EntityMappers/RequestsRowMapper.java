package com.example.application.Resources.EntityMappers;

import com.example.application.Entities.Event.Request;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import org.springframework.jdbc.core.RowMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestsRowMapper implements RowMapper<Request> {
    @Override
    public Request mapRow(ResultSet rs, int i) throws SQLException {
        byte[] proofImage = rs.getBytes("ProofImage");
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

        Request request = new Request(rs.getString("FirstName") , rs.getString("LastName") , rs.getString("Email")
                , rs.getString("RoleInEvent") , rs.getString("Comment") , rs.getDouble("RewardEarned") ,
                rs.getString("Status") , rs.getString("EventAttended") , rs.getString("EventType") , null,
                proofAsVaadinImage);

        return request;

    }
}
