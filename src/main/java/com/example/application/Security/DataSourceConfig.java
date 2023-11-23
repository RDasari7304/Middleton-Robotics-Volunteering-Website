package com.example.application.Security;

import com.example.application.Resources.Services.Database;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public static DataSource getDataSource(){
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.jdbc.Driver");
        dataSourceBuilder.url("jdbc:mysql://" + Database.dbPath);
        dataSourceBuilder.username(Database.username);
        dataSourceBuilder.password(Database.password);

        return dataSourceBuilder.build();
    }

}
