package com.codeFellowshipNur.controllers;

import com.codeFellowshipNur.models.ApplicationUser;
import com.codeFellowshipNur.repositories.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.ui.Model;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Date;

// TODO: Step 2: Create controller for ApplicationUser
@Controller
public class ApplicationUserController {
    @Autowired
    ApplicationUserRepository applicationUserRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    // How to auto login
    HttpServletRequest request;
    //POST route to create new ApplicationUser
    @PostMapping("/signup")
    public RedirectView createApplicationUser(String username, String password, String firstName, String lastName, Date dateOfBirth, String bio){
        // Hash the PW
        String hashedPW = passwordEncoder.encode(password);
        // Create user
        ApplicationUser newUser = new ApplicationUser(username, hashedPW, firstName, lastName, new Date(), bio);
        // Save the user
        applicationUserRepository.save(newUser);
        // Auto login

        // Return redirectView
        return  new RedirectView("/login");
    }
    public void autoAuthWithHttpServletRequest(String username, String password){
        try {
            request.login(username, password);
        } catch (ServletException se) {
            se.printStackTrace();
        }
    }

    @GetMapping("login")
    public String getLoginPage(){
        return "login";
    }

    @GetMapping("/signup")
    public String getSignupPage(){
        return "signup.html";
    }

    // GET route /
    @GetMapping("/")
    public String getHome(Model m, Principal p){
        if(p != null){
            String username = p.getName();
            ApplicationUser dbUser = applicationUserRepository.findByUsername(username);

            m.addAllAttributes("username", username);
            m.addAllAttributes("FirstName", dbUser.getFirstName());
            m.addAllAttributes("LastName", dbUser.getLastName());


        }
        return "index.html";
    }
    //GET rout to /secret sauce
    @GetMapping("/secret")
    public String getSecretSauce(){
        return "secretSauce.html";
    }

}
