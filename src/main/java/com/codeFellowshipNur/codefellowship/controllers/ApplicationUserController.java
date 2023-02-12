package com.codeFellowshipNur.codefellowship.controllers;

import com.codeFellowshipNur.codefellowship.models.ApplicationUser;
import com.codeFellowshipNur.codefellowship.models.Post;
import com.codeFellowshipNur.codefellowship.repositories.ApplicationUserRepository;
import com.codeFellowshipNur.codefellowship.repositories.PostRepository;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;


// TODO: Step 2: Create controller for ApplicationUser
@Controller
public class ApplicationUserController {
    @Autowired
    ApplicationUserRepository applicationUserRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    // How to auto login
    HttpServletRequest request;
    //POST route to create new ApplicationUser
    @PostMapping("/signup")
    public RedirectView createApplicationUser(String username, String password, String firstName, String lastName, String dateOfBirth, String bio){
        // Hash the PW
        String hashedPW = passwordEncoder.encode(password);
        // Create user
        String stringData = dateOfBirth;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date formattedDate;
        try {
            formattedDate = format.parse(stringData);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ApplicationUser newUser = new ApplicationUser(username, hashedPW, firstName, lastName, formattedDate, bio);
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

    @GetMapping("/myProfile")
    public String getMyProfile( Model m, Principal p) {
        ApplicationUser authenticatedUser = applicationUserRepository.findByUsername(p.getName());
        m.addAttribute("authUser", authenticatedUser);
        return "myProfile.html";
    }


    @PostMapping("/myProfile")
    public RedirectView createPost(Principal p, String body, Model m) {
        if(p!=null){
            String username = p.getName();
            ApplicationUser appUser = applicationUserRepository.findByUsername(username);
            m.addAttribute("addPost", appUser.getListOfPost());
            Date date = new Date();
            Post newPost = new Post(body, date, appUser);
            newPost.setCreatedBy(appUser);
            postRepository.save(newPost);
        }
        return new RedirectView("/myProfile");
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
        // Add usersIFollow and usersWhoFollowMe to the HTML page
        m.addAttribute("usersIFollow", viewUser.getUsersIFollow());
        m.addAttribute("usersWhoFollowMe", viewUser.getUsersWhoFollowMe());

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

    @PutMapping("/follow-user/{id}")
    public RedirectView followUser(Principal p, @PathVariable Long id) throws IllegalAccessException {
        ApplicationUser userToFollow = applicationUserRepository.findById(id).orElseThrow(() -> new RuntimeException("Error Reading User From The Database with ID of: " + id));
        ApplicationUser browseUser = applicationUserRepository.findByUsername(p.getName());
        if (browseUser.getUsername().equals(userToFollow.getUsername())) {
            throw new IllegalAccessException("Don't Follow Yourself!");
        }

        browseUser.getUsersIFollow().add(userToFollow);

        applicationUserRepository.save(browseUser);

        return new RedirectView("/user/" + id);
    }


}
