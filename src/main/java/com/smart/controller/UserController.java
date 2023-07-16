package com.smart.controller;

import com.smart.helper.Message;
import com.smart.models.Contact;
import com.smart.models.User;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private ContactRepository   contactRepository;


    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        model.addAttribute("title", "UserPage-SmartContactManager");
        String userName = principal.getName();
        System.out.println("UserName is : " + userName);
        User user = userRepository.getUserByuserName(userName);
        System.out.println("user name : " + user.getName());
        System.out.println("user Email : " + user.getEmail());
        System.out.println("user password : " + user.getPassword());
        System.out.println("user About : " + user.getAbout());
        System.out.println("user role : " + user.getRole());
        System.out.println("all details about user : " + user);
        model.addAttribute("user", user);
    }

//    Showinf user dashboard
    @RequestMapping("/index")
    public String dashboard(Model model ,Principal principal) {
        String UserName = principal.getName();
        System.out.println("Username"+UserName);
        User user = userRepository.getUserByuserName(UserName);
        System.out.println("UserDetails"+user);
        model.addAttribute("user", user);
        return "normal/user_dashboard";
    }

//    Adding contact information url
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "add_contact_form");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }


    //proccesing add contact form
    @PostMapping("/process-contact")
    public String processContact(Model model ,
                             Contact contact,
                             @RequestParam("profileimage") MultipartFile file,
                             Principal principal,
                             HttpSession session) throws IOException {
       try {
           System.out.println("Data" + contact);
           String name = principal.getName();
           User user = this.userRepository.getUserByuserName(name);
           contact.setUser(user);
           user.getContact().add(contact);
           if (file.isEmpty()) {
               contact.setImageUrl("default.png");
//               File saveFile = new ClassPathResource("/static/IMG").getFile();
//               Path path = Paths.get(saveFile.getAbsolutePath()+File.separator + file.getOriginalFilename());
//               Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//               System.out.println("Image uploaded successfully");
           }
           else
           {
               contact.setImageUrl(file.getOriginalFilename());
               System.out.println("File original name: " + file.getOriginalFilename());
               File saveFile = new ClassPathResource("static/img").getFile();
               // path
               Path path = Paths.get( saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
               Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
               System.out.println("Image is uploaded");
           }
           this.userRepository.save(user);
           System.out.println("Contact" + contact);
           System.out.println("image save successfully");
           Message successMssg = new Message("Added contact successfully !!", "alert-success");
           session.setAttribute("message", successMssg);
       }catch (Exception e) {
            System.out.println("Error"+e.getMessage());
           // display error message
           Message errorMssg = new Message("Something went wrong, Try again !!", "alert-danger");
           session.setAttribute("message", errorMssg);

       }
        return "normal/add_contact_form";
    }



//    Showing Contact Form
    @GetMapping("/show-contact/{page}")
    public String showContact(@PathVariable("page") Integer page , Model model ,Principal principal){
        model.addAttribute("title","Show Contacts");
        String username=principal.getName();
        User user = this.userRepository.getUserByuserName(username);
        Pageable pageable= PageRequest.of(page,5);
        Page<Contact> contacts =this.contactRepository.findContactByUser(user.getId(),pageable);
        model.addAttribute("contacts" ,contacts);
        model.addAttribute("currentpage",page);
        model.addAttribute("totalpages",contacts.getTotalPages());
        return "normal/show_contact";
    }


    // show particular contact
    @GetMapping("/contact/{cid}")
    public String showPartiContact(@PathVariable("cid") int cid, Model model, Principal principal) {
        System.out.println("contact id : " + cid);
        Optional<Contact> contactoptional = this.contactRepository.findById(cid);
        Contact contact = contactoptional.get();
        String username = principal.getName();
        User user = this.userRepository.getUserByuserName(username);
        if (user.getId() == contact.getUser().getId()) {
            model.addAttribute("contact", contact);
            model.addAttribute("title", contact.getName());
        }
        return "normal/contact_details";
    }
    
//    delete contact handler
    @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid") Integer cid ,Model model,HttpSession   session,Principal principal) {
    Optional<Contact> contactopt=this.contactRepository.findById(cid);
    Contact contact = contactopt.get();
    contact.setUser(null);
    String username = principal.getName();
    User user = this.userRepository.getUserByuserName(username);
//    if (user.getId() == contact.getUser().getId()) {
        this.contactRepository.delete(contact);
        session.setAttribute("message", new Message("contact successfully deleted..", "alert-success"));
//    }
        return "redirect:/user/show-contact/0";
    }

// update contact form
    @GetMapping("/update_form/{cid}")
    public String updateContact(@PathVariable("cid") int cid, Model model) {
        Optional<Contact> contactoptional = this.contactRepository.findById(cid);
        Contact contact = contactoptional.get();
        model.addAttribute("contact", contact);
        model.addAttribute("title", "update-contact");
        return "normal/update_form";
    }

//    Handling contact update
    @PostMapping("/process-update")
    public String updateHandler(@ModelAttribute Contact contact,Principal principal,@RequestParam("profileimage") MultipartFile file,Model model ,HttpSession session)  {
//        System.out.println("Contacts"+contact);
        try {

            System.out.println("contact is " + contact.getName());
            System.out.println("contact id is " + contact.getCid());
            Contact oldContact =this.contactRepository.findById(contact.getCid()).get();


            if (!file.isEmpty()) {
//deleting old file
                File deleteFile = new ClassPathResource("static/img").getFile();
                File deleteFile1 = new ClassPathResource("static/img").getFile();
                deleteFile1.delete();
//Saving new file in db
                File saveFile = new ClassPathResource("static/img").getFile();
                // path
                Path path = Paths.get( saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                contact.setImageUrl(file.getOriginalFilename());

                }
            else {
                contact.setImageUrl(oldContact.getImageUrl());
            }
            User user =this.userRepository.getUserByuserName(principal.getName());
            contact.setUser(user);
            this.contactRepository.save(contact);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/user/contact/"+contact.getCid();
    }

}