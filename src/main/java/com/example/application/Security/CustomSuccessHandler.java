package com.example.application.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Configuration
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = getUrl(authentication);

        if(response.isCommitted()){
            return;
        }

        RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
        redirectStrategy.sendRedirect(request , response , targetUrl);
    }

    private String getUrl(Authentication auth){
        String url = "/login";

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        List<String> Roles = new ArrayList<String>();

        for(GrantedAuthority a : authorities){
            Roles.add(a.getAuthority());
        }

        if(Roles.contains("ROLE_ADMIN")){
            url = "/AdminHome";
        }

        if(Roles.contains("ROLE_STUDENT")){
            url = "/events";
        }

        return url;
    }



}
