package com.zachrrichards.CryptoAlerts.controllers;

import com.zachrrichards.CryptoAlerts.models.User;
import com.zachrrichards.CryptoAlerts.services.EmailService;
import com.zachrrichards.CryptoAlerts.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLoginPage(Model model, HttpServletRequest request, Principal principal) {
        model.addAttribute("user", new User());

        try {
            String email = principal.getName();
            model.addAttribute("userNav", true);
        } catch(Exception ex) { model.addAttribute("userNav", false); }

        try {
            Object loginFailure = request.getSession().getAttribute("loginFailure");
            model.addAttribute("loginFailure", loginFailure);

            request.getSession().removeAttribute("loginFailure");
        } catch (Exception ex) {}

        return "login";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegisterPage(Model model, Principal principal) {
        model.addAttribute("user", new User());

        try {
            String email = principal.getName();
            model.addAttribute("userNav", true);
        } catch(Exception ex) { model.addAttribute("userNav", false); }

        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processRegisterPage(Model model, @ModelAttribute @Valid User user, Errors errors, HttpServletRequest request, Principal principal) {

        try {
            String email = principal.getName();
            model.addAttribute("userNav", true);
        } catch(Exception ex) { model.addAttribute("userNav", false); }

        User userExists = userService.findByEmail(user.getEmail());

        if(userExists != null) {
            model.addAttribute("alreadyRegisteredMessage", "Oops! there is already a user with that email");
            model.addAttribute("user", user);
            return "register";
        }

        if(errors.hasErrors()) {
            model.addAttribute("user", user);
            return "register";
        }

        user.setEnabled(false);
        user.setConfirmationToken(UUID.randomUUID().toString());

        userService.saveUser(user);

        String appUrl = request.getScheme() + "://" + request.getServerName();

        SimpleMailMessage registrationEmail = new SimpleMailMessage();
        registrationEmail.setTo(user.getEmail());
        registrationEmail.setSubject("CryptoAlerts | Registration Confirmation");
        registrationEmail.setText("To confirm your e-mail address, please click the link below:\n"
                + appUrl + ":8080" + "/confirm?token=" + user.getConfirmationToken());
        registrationEmail.setFrom("zacharyrrichards@gmail.com");

        emailService.sendEmail(registrationEmail);

        model.addAttribute("confirmationMessage", "A confirmation email has been sent to " + user.getEmail());
        model.addAttribute("user", new User());
        return "register";
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.GET)
    public String showConfirmPage(Model model, @RequestParam String token, Principal principal) {

        try {
            String email = principal.getName();
            model.addAttribute("userNav", true);
        } catch(Exception ex) { model.addAttribute("userNav", false); }

        User user = userService.findByConfirmationToken(token);

        if(user == null) {
            model.addAttribute("passwordSet", true);
            model.addAttribute("passwordMsg", "Oops! this is an invalid confirmation link!");
        } else {
            model.addAttribute("confirmationToken", user.getConfirmationToken());
        }

        return "confirm";
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.POST)
    public String processConfirmPage(Model model, HttpServletRequest request, Principal principal) {

        try {
            String email = principal.getName();
            model.addAttribute("userNav", true);
        } catch(Exception ex) { model.addAttribute("userNav", false); }

        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String token = request.getParameter("token");

        if(password.length() < 6) {
            model.addAttribute("confirmationToken", token);
            model.addAttribute("passwordMsg", "Your password needs to be at least 6 characters long");
            return "confirm";
        } else if(!password.equals(confirmPassword)) {
            model.addAttribute("confirmationToken", token);
            model.addAttribute("passwordMsg", "The passwords do not match");
            return "confirm";
        }

        User user = userService.findByConfirmationToken(token);

        if(user.getPassword() != null) {
            model.addAttribute("passwordSet", true);
            model.addAttribute("passwordMsg", "Your password has already been set");
            return "confirm";
        }

        String[] role = {"ROLE_USER"};

        user.setPassword(password);
        user.setEnabled(true);
        user.setRole(role);

        userService.saveUser(user);

        model.addAttribute("passwordSuccess", "Congrats your password has been set!");
        model.addAttribute("passwordSet", true);
        return "confirm";
    }
}
