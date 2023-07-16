package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.smart.models.User;
import com.smart.repository.UserRepository;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;

	
	@GetMapping("/")
	public String home( Model model) {
		model.addAttribute("title", "Home Smart Contact Manager");
		return "home";
	}
	@GetMapping("/about")
	public String about( Model model) {
		model.addAttribute("title", "About Smart Contact Manager");
		return "about";
	}
	@GetMapping("/signup")
	public String signup( Model model) {
		model.addAttribute("title", "SignUp Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	@GetMapping("/userlogin")
	public String login( Model model) {
		model.addAttribute("title", "Login Smart Contact Manager");
		return "userlogin";
	}
	
	
}
