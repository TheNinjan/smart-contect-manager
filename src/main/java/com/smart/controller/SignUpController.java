package com.smart.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.smart.helper.Message;
import com.smart.models.User;
import com.smart.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class SignUpController {

	@Autowired
      public UserRepository userRepository;
	

	@Autowired(required = true)
	public PasswordEncoder passwordEncoder;

	    // Handler for registering user
	 @PostMapping("/do_register")
	 public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,
	                               @RequestParam(value = "agreement", defaultValue = "false") Boolean agreement,
	                               Model model,
	                               HttpSession session) {

	        try {

	            if (!agreement) {
	                System.out.println("You have not agreed the terms & conditions");
	                throw new Exception("You have not agreed the terms & conditions");
	            }
	            if (bindingResult.hasErrors()){
	                model.addAttribute("user", user);
	                return "signup";
	            }
	            user.setRole("ROLE_USER");
	            user.setEnabled(true);
	            user.setImageurl("default.png");
	            user.setPassword(passwordEncoder.encode(user.getPassword()));
	            System.out.println("USER: " + user.toString());
	            System.out.println("AGRE-EMENT: " + agreement);
	            User savedUser = this.userRepository.save(user);

	            // Show empty user in front end
	            model.addAttribute("user", new User());
	            model.addAttribute("message", new  Message("Successfully Registered!! ", "alert-success"));
	            session.setAttribute("message",new Message("Successfully Registered!! ", "alert-success"));
	            return "signup";
	        }
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	            model.addAttribute("user", user);
	            model.addAttribute("message", new  Message("Something went wrong  "+e.getMessage(), "alert-danger"));
	            session.setAttribute(
	                    "message",
	                    new Message("Something went wrong !! " + e.getMessage(), "alert-danger")
	            		);
	            return "signup";
	        }
	        
	        
	      }
	 
//	 
//	      @GetMapping("/{id}")
//	      public Optional<User> getUserbyId(@PathVariable int id ) {
//	    	  return userRepository.findById(id);
//	      }
	 
	 

//	      @PostMapping("/doLogin")
//	      public String userLogin(@RequestBody LoginRequest loginRequest) {
//	    	  if(userRepository.getUserByEmail(loginRequest.getEmail())) {
//
//	    	       User user=userRepository.getUserByuserName(loginRequest.getEmail());
//	    	       String oldPwd=user.getPassword();
//
//	    	       if(oldPwd.equals(loginRequest.getPassword())){
//	    	    	   userRepository.save(user);
//	    	    	   return "User login successfully";
//	    	       }
//	    	  }
//	    	  return "User does not exist with this email id : "+loginRequest.getEmail();
//
//
//	      }



	    

}
