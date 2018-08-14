package com.zachrrichards.CryptoAlerts.controllers;

import com.zachrrichards.CryptoAlerts.dao.AlertRepository;
import com.zachrrichards.CryptoAlerts.dao.TickerRepository;
import com.zachrrichards.CryptoAlerts.models.Alert;
import com.zachrrichards.CryptoAlerts.models.User;
import com.zachrrichards.CryptoAlerts.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AlertRepository alertRepository;
    @Autowired
    private TickerRepository tickerRepository;

    @RequestMapping(value = "/me", method = RequestMethod.GET)
    public String getUser(Model model, Principal principal) {

        String email = principal.getName();
        User user = userService.findByEmail(email);

        if(user == null) {
            model.addAttribute("userNav", false);
            return "redirect:/login";
        }
        model.addAttribute("userNav", true);

        model.addAttribute("user", user);

        return "users/user";
    }

    @RequestMapping(value = "/me/create", method = RequestMethod.GET)
    public String getCreateAlert(Model model, Principal principal) {

        String email = principal.getName();
        User user = userService.findByEmail(email);

        if(user == null) {
            model.addAttribute("userNav", false);
            return "redirect:/login";
        }
        model.addAttribute("userNav", true);

        model.addAttribute("user", user);
        model.addAttribute("alert", new Alert());
        model.addAttribute("tickers", tickerRepository.findAllTickers(new Sort("symbol")));

        return "users/createAlert";
    }

    @RequestMapping(value = "/me", method = RequestMethod.POST)
    public String CreateAlert(Model model, @ModelAttribute Alert alert, Principal principal) {

        String email = principal.getName();
        User user = userService.findByEmail(email);

        if(user == null) {
            return "redirect:/login";
        }

        try {
            alert.setUser(user);
            alert.setFormatPrice(String.format("%.8f", alert.getPrice()));
            user.addAlerts(alert);
            alertRepository.save(alert);
        } catch(Exception ex) {}

        return "redirect:/users/me";
    }

    @RequestMapping(value = "/me", method = RequestMethod.DELETE)
    public String RemoveAlert(Model model, @RequestParam int id, Principal principal) {

        String email = principal.getName();
        User user = userService.findByEmail(email);

        if(user == null) {
            return "redirect:/login";
        }

        try {
            alertRepository.deleteById(id);
        } catch(Exception ex) {}

        return "redirect:/users/me";
    }

    @RequestMapping(value = "/me/settings", method = RequestMethod.GET)
    public String getUserSettings(Model model, Principal principal) {

        String email = principal.getName();
        User user = userService.findByEmail(email);

        if(user == null) {
            model.addAttribute("userNav", false);
            return "redirect:/login";
        }
        model.addAttribute("userNav", true);
        model.addAttribute("user", user);

        return "users/userSettings";
    }

    @RequestMapping(value = "/me/settings", method = RequestMethod.PUT)
    public String EditUser(Model model, @ModelAttribute User user, Principal principal) {

        String email = principal.getName();
        User editUser = userService.findByEmail(email);

        if(editUser == null) {
            model.addAttribute("userNav", false);
            return "redirect:/login";
        }
        model.addAttribute("userNav", true);
        model.addAttribute("user", editUser);

        if(user.getFirstName().length() < 1 || user.getFirstName().length() > 20) {
            model.addAttribute("editNameMsg", "First name has to be between 1 and 30 characters");
            return "users/userSettings";
        }
        if(user.getLastName().length() < 1 || user.getLastName().length() > 20) {
            model.addAttribute("editNameMsg", "Last name has to be between 1 and 30 characters");
            return "users/userSettings";
        }

        editUser.setFirstName(user.getFirstName());
        editUser.setLastName(user.getLastName());
        userService.saveUser(editUser);

        model.addAttribute("successNameMsg", "Your name was successfully changed!");

        return "users/userSettings";
    }

    @RequestMapping(value = "/me/settings", method = RequestMethod.DELETE)
    public String deleteUser(Model model, HttpServletRequest request, Principal principal) {

        String email = principal.getName();
        User user = userService.findByEmail(email);

        if(user == null) {
            return "redirect:/login";
        }

        try {
            userService.deleteAllAlerts(user);
            userService.delete(user);
            HttpSession session = request.getSession();
            session.invalidate();
        } catch(Exception ex) {}

        return "redirect:/login";
    }

    @RequestMapping(value = "/me/settings/password", method = RequestMethod.PUT)
    public String editUserPassword(Model model, HttpServletRequest request, Principal principal) {

        String email = principal.getName();
        User user = userService.findByEmail(email);
        model.addAttribute("user", user);

        if(user == null) {
            model.addAttribute("userNav", false);
            return "redirect:/login";
        }
        model.addAttribute("userNav", true);

        String password = request.getParameter("password");
        String newPass = request.getParameter("newPass");
        String matchingPass = request.getParameter("matchingPass");

        if(!User.PASSWORD_ENCODER.matches(password, user.getPassword())) {
            model.addAttribute("setPasswordMsg", "Your current password does not match");
            return "users/userSettings";
        }else if(newPass.length() < 6 || newPass.length() > 20) {
            model.addAttribute("setPasswordMsg", "New password has to be at between 6 and 20 characters long");
            return "users/userSettings";
        }else if(password.equals(newPass)) {
            model.addAttribute("setPasswordMsg", "Your new password can't be the same as your current password");
            return "users/userSettings";
        }else if(!newPass.equals(matchingPass)) {
            model.addAttribute("setPasswordMsg", "Your new password does not match");
            return "users/userSettings";
        }

        user.setPassword(newPass);
        userService.saveUser(user);

        model.addAttribute("successPasswordMsg", "Your password was changed successfully!");

        return "users/userSettings";
    }
}
