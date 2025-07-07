package com.example.beststore.controller;

import com.example.beststore.model.auth.User;
import com.example.beststore.model.auth.UserDto;
import com.example.beststore.service.auth.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@ControllerAdvice
public class AuthController {

    @Autowired
    private UserService userService; // Assuming you have a UserService to handle user registration

    @GetMapping("/registration")
    public String openRegistrationPage(Model model) {
        model.addAttribute("userDto", new UserDto());
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String registerUser(
            @Valid
            @ModelAttribute("userDto") UserDto useDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
        boolean emailExists = userService.emailExists(useDto.getEmail());
        if (emailExists) {
            result.rejectValue("email", "error.userDto", "Email already exists. Please use a different email.");
            return "auth/registration";
        } else{
            User user = new User();
            user.setName(useDto.getName());
            user.setEmail(useDto.getEmail());
            user.setPassword(useDto.getPassword());
            boolean status = userService.registerUser(user);
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Please correct the errors in the form.");
                return "redirect:/registration";
            }
            if (status) {
                redirectAttributes.addFlashAttribute("message", "Registration successful! Please log in.");
                return "redirect:/login"; // use redirect, not view name
            } else {
                redirectAttributes.addFlashAttribute("error", "Registration failed. Please try again.");
                return "redirect:/registration";
            }
        }
    }

    @GetMapping("/login")
    public String openLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have been logged out successfully.");
        }
        model.addAttribute("user", new User());
        return "auth/login";
    }


//    @PostMapping("/login")
//    public String LoginUser(
//            @ModelAttribute("user") User user,
//            Model model,
//            RedirectAttributes redirectAttributes
//    ){
//        User validUser = userService.loginUser(user.getEmail(), user.getPassword());
//        System.out.println(validUser==null);
//        if(validUser != null) {
//            redirectAttributes.addFlashAttribute("message", "Login successful! Welcome, " + validUser.getName() + "!");
//            return "redirect:/products";
//        } else {
//            model.addAttribute("error", "Invalid email or password. Please try again.");
//            return "auth/login";
//        }
//    }
}
