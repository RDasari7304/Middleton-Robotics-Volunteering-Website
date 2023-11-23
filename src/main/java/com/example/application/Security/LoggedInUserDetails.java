package com.example.application.Security;

import com.example.application.Entities.User.User;
import com.example.application.Resources.Services.DataService;
import com.example.application.Resources.Services.Database;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collection;

public class LoggedInUserDetails {

    private static DataService dataService = new DataService();

    private static Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    private static String currUserLoggedIn = auth.getName();

    private static User loggedInUserDetails = checkAnonymousToken() ? null : dataService.getUserByUsername(currUserLoggedIn);

    public static String getFirstName(){
        reloadAuth();
        if(checkAnonymousToken()) return "";
        return loggedInUserDetails.getFirstName();
    }

    public static String getLastName(){
        reloadAuth();
        if(checkAnonymousToken()) return "";
        return loggedInUserDetails.getLastName();
    }

    public static String getUsername(){
        reloadAuth();
        if(checkAnonymousToken()) return "";
        return currUserLoggedIn;
    }

    public static String getEmail(){
        reloadAuth();
        if(checkAnonymousToken()) return "";
        return loggedInUserDetails.getEmail();
    }

    public static String getTeam(){
        reloadAuth();
        if(checkAnonymousToken()) return "";
        return loggedInUserDetails.getTeam();
    }

    private static void reloadAuth(){
        auth = SecurityContextHolder.getContext().getAuthentication();
        if(!currUserLoggedIn.equals( auth.getName())){
            currUserLoggedIn = auth.getName();
            loggedInUserDetails = dataService.getUserByUsername(currUserLoggedIn);

        }

    }

    public static Collection<? extends GrantedAuthority> getAuthorities(){
        reloadAuth();
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();

    }

    public static ArrayList<String> getRoles(){
        ArrayList<String> Roles = new ArrayList<String>();

        for(GrantedAuthority a : getAuthorities()){
            Roles.add(a.getAuthority());
        }

        return Roles;
    }

    private static boolean checkAnonymousToken(){
        if(auth instanceof AnonymousAuthenticationToken){
            return true;
        }

        return false;
    }

}
