package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.UserModel;
import com.example.demo.Model.UserRepository;
import com.example.demo.Service.EmailService;
import com.example.demo.Utils.JwtTokenUtil;

@RestController
public class RegistrationController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    
    @PostMapping(value = "/req/signup", consumes = "application/json")
    public ResponseEntity<String> createUser(@RequestBody UserModel user){
        
        UserModel existingUser = userRepository.findByEmail(user.getEmail());
        
        if(existingUser != null){
            if(existingUser.isVerified()){
                return new ResponseEntity<>("User Already exist and Verified.",HttpStatus.BAD_REQUEST);
            }else{
                String verificationToken = JwtTokenUtil.generateToken(existingUser.getEmail());
                existingUser.setVerficationToken(verificationToken);
                userRepository.save(existingUser);
                //Send Email Code
                emailService.sendVerificationEmail(existingUser.getEmail(), verificationToken);
                return new ResponseEntity<>("Verification Email resent. Check your inbox",HttpStatus.OK);
            }
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        String vericationToken =JwtTokenUtil.generateToken(user.getEmail());
        user.setVerficationToken(vericationToken);
        userRepository.save(user);
        //Send Email Code
        emailService.sendVerificationEmail(user.getEmail(), vericationToken);
        
        return new ResponseEntity<>("Registration successfull! Please Verify your Email", HttpStatus.OK);
    }
    
}
