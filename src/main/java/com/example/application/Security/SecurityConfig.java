package com.example.application.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource = DataSourceConfig.getDataSource();


    @Autowired
    private CustomSuccessHandler successHandler;

    @Autowired
    public void authenticate(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder())
                .usersByUsernameQuery("select Username,Password, Enabled "
                        + "from MiddletonRoboticsMembers "
                        + "where Username = ?")
                .authoritiesByUsernameQuery("Select Username , Authority " +
                        "from Authorities " +
                        "where Username = ?");
    }




    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/login" , "/Register" , "/forgot_password" , "/test").permitAll()
                .antMatchers("/AdminHome" , "/UsersPage" , "/pending_approvals" , "/student_data" , "student_data/moreInfo").hasRole("ADMIN")
                .antMatchers("/events" , "/moreInfo" , "/previous_sign_ups").hasRole("STUDENT")
                .and()
                .formLogin().permitAll().loginPage("/login").permitAll()
                .successHandler(successHandler)
                .and()
                .httpBasic().and().logout().logoutSuccessUrl("/login").permitAll();

    }

}
