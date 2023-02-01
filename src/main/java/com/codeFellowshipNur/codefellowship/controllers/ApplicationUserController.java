package com.codeFellowshipNur.codefellowship.controllers;

import com.codeFellowshipNur.codefellowship.models.ApplicationUser;
import com.codeFellowshipNur.codefellowship.repositories.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
        autoAuthWithHttpServletRequest(username, password);
        // Return redirectView
        return new RedirectView("/");
    }
    public void autoAuthWithHttpServletRequest(String username, String password){
        try {
            request.login(username, password);
        } catch (ServletException se) {
            se.printStackTrace();
        }
    }

    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }

    @GetMapping("/signup")
    public String getSignupPage(){
        return "signup";
    }

    // GET route /
    @GetMapping("/")
    public String getHome(Model m, Principal p){
        try {
        if(p != null) {
            String username = p.getName();
            ApplicationUser dbUser = applicationUserRepository.findByUsername(username);

            m.addAttribute("username", username);
            m.addAttribute("FirstName", dbUser.getFirstName());
            m.addAttribute("LastName", dbUser.getLastName());
        }


        } catch (RuntimeException runtimeException) {
            throw new RuntimeException("Error message! Something is wrong.");
        }
        return "index.html";
    }
    //GET rout to /secret sauce
    @GetMapping("/secret")
    public String getSecretSauce(){
        return "secretSauce";
    }

    @GetMapping("/user/{id}")
    public String getOneAppUser(@PathVariable Long id, Model m, Principal p){
        ApplicationUser authenticatedUser = applicationUserRepository.findByUsername(p.getName());
        m.addAttribute("authUser", authenticatedUser);
        // Find user by ID from DB
        ApplicationUser viewUser = applicationUserRepository.findById(id).orElseThrow();
        // Attach the user to the model
        m.addAttribute("viewUser", viewUser);
        return "myProfile.html";
    }

    @PutMapping("/user/{id}")
    public RedirectView editAppUserInfo(@PathVariable Long id, String username, String firstName, String lastName, Principal p, RedirectAttributes redir) throws ServletException {
        // Find user to edit
        ApplicationUser userToBeEdited = applicationUserRepository.findById(id).orElseThrow();
        if (p.getName().equals(userToBeEdited.getUsername())) {
        // Update user
        userToBeEdited.setUsername(username);
        userToBeEdited.setFirstName(firstName);
        userToBeEdited.setLastName(lastName);
        // Save edited user back to db
        applicationUserRepository.save(userToBeEdited);
        request.logout();
        autoAuthWithHttpServletRequest(username, userToBeEdited.getPassword());
        } else {
            redir.addFlashAttribute("errorMessage", "Can't edit another user's info!");
        }
        return new RedirectView("/user/" + id);
    }

}
