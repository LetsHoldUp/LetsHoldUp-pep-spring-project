package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
public class SocialMediaController {
    //Constructor
    @Autowired
    public SocialMediaController(ObjectMapper objectMapper, AccountService accountService, MessageService messageService){
        this.objectMapper = objectMapper;
        this.accountService = accountService;
        this.messageService = messageService;
    }

    ObjectMapper objectMapper;

    AccountService accountService;

    MessageService messageService;

    // We need to leverage spring
    // Todo:
    // Set up 8 endpoints
    // We will need to set up beans for each of the service's and DAO's
    //  -Should be as simple as adding @Bean and @Autowired annotations in the right places, I believe
    //  -AccountService, MessageService, AccountRepository, MessageRepository, and SocialMediaController classes in bean form!

    // Useful syntax
    // @RequestMapping -> @GetMapping, @PostMapping, @PutMapping, @PatchMapping, @DeleteMapping
    // @RequestBody String


    // We need 8 endpoints, they are as follows
    // 1. New User registration
    @PostMapping(path= "/register")
    public @ResponseBody ResponseEntity<Account> RegisterUser(@RequestBody String body) throws JsonMappingException, JsonProcessingException{
        // Convert jsonbody into Account object
        Account account = objectMapper.readValue(body, Account.class);
        
        // Call AccountService.RegisterUser(). This returns null, if it could not create a new account, or an account object with its account id filled in
        Account outAccount = accountService.registerUser(account);

        // Check if this account returned with a negative ID, if it did then we have a duplicate account creation attempt and must return a 409
        if(outAccount.getAccount_id() < 0){
            return ResponseEntity.status(409).build();
        }
        
        // Return a response entity, with a success and the account with its account id
        return ResponseEntity.status(HttpStatus.OK).body(outAccount);
    }

    // 2. Process User login
    @PostMapping(path= "/login")
    public @ResponseBody ResponseEntity<Account> loginUser(@RequestBody String body) throws JsonMappingException, JsonProcessingException{
        // Convert jsonbody into Account object
        Account account = objectMapper.readValue(body, Account.class);

        // Call accountService.loginUser, which will return the account, with its {account_id}
        // Or null if the login was unsuccessful
        Account outAccount = accountService.loginUser(account);

        // If its null, the login information is false
        if(outAccount == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
        }

        return ResponseEntity.status(HttpStatus.OK).body(outAccount);
    }

    // 3. Allow new messages to be posted
    @PostMapping(path= "/messages")
    public @ResponseBody ResponseEntity<Message> postNewMessage(@RequestBody String body) throws JsonMappingException, JsonProcessingException{
        // Convert jsonbody into a Message object
        Message message = objectMapper.readValue(body, Message.class);

        // Call messageService.postNewMessage method
        Message outMessage = messageService.postNewMessage(message);
        
        // Check to ensure the message has been persisted, outMessage will be null otherwise
        if (outMessage == null){
            return ResponseEntity.status(400).build();
        }

        // Otherwise we can return the message in the body
        return ResponseEntity.status(HttpStatus.OK).body(outMessage);
    }

    // 4. Retreive all messages
    @GetMapping(path = "/messages")
    public @ResponseBody ResponseEntity<List<Message>> getAllMessages() throws JsonMappingException, JsonProcessingException{ 
        // Get all of the messages in an ArrayList
        ArrayList<Message> outList = (ArrayList<Message>) messageService.getAllMessages();

        // Call the messageService.getAllMessages() method and return the list of message
        return ResponseEntity.status(HttpStatus.OK).body(outList);
    }

    // 5. Retreive a message by its {message_id}
    @GetMapping(path="/messages/{message_id}")
    public @ResponseBody ResponseEntity<Message> getMessageById(@PathVariable String message_id) throws JsonMappingException, JsonProcessingException{ 
        // Convert the string boy to a proper id 
        Integer id = Integer.parseInt(message_id);

        // Check to make sure the message exists
        if(messageService.checkIfMessageExists(id) == false){
            // Since message doesn't exist, return OK with no body
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        // Get the message and then send it in the ResponseEntity body
        Message outMessage = messageService.getMessageById(id);
        return ResponseEntity.status(200).body(outMessage);
    }

    // 6. Delete a message by its {message_id}
    @DeleteMapping(path="/messages/{message_id}")
    public @ResponseBody ResponseEntity<String> deleteMessageById(@PathVariable String message_id) throws JsonMappingException, JsonProcessingException{ 
        // Convert the string boy to a proper id 
        Integer id = Integer.parseInt(message_id);

        // Check to make sure the message exists
        if(messageService.checkIfMessageExists(id) == false){
            // Since message doesn't exist, return OK with no body
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        // Delete the message
        messageService.deleteMessageById(id);

        // Return the number of affected rows, which will always be 1
        return ResponseEntity.status(HttpStatus.OK).body("1");
    }

    // 7. Update a message by its {message_id}
    @PatchMapping(path="/messages/{message_id}")
    public @ResponseBody ResponseEntity<Integer> updateMessageById(@RequestBody String body, @PathVariable String message_id) throws JsonMappingException, JsonProcessingException{
        // Convert the string boy to a proper id 
        Integer id = Integer.parseInt(message_id);

        // Convert the JSON body into a lone string value
        body = objectMapper.readValue(body, Message.class).getMessage_text();

        // Check to make sure the message exists
        if(messageService.checkIfMessageExists(id) == false){
            // Since message doesn't exist, return OK with no body
            return ResponseEntity.status(400).build();
        }

        // Now we will call messageService.updateMessageById 
        // This will check to make sure the message is the correct length, and then update it
        // Or it will return null if the text is not correct
        Message outMessage = messageService.updateMessageById(body, id);
        if(outMessage == null){
            return ResponseEntity.status(400).build(); 
        }

        return ResponseEntity.status(200).body(1);
    }

    // 8. Retreive all message by their {posted_by}
    @GetMapping(path="/accounts/{account_id}/messages")
    public @ResponseBody ResponseEntity<List<Message>> getAllMessagesByPosted_By(@PathVariable String posted_by) throws JsonMappingException, JsonProcessingException {
        // Convert the posted_by string into an integer
        Integer posted = Integer.parseInt(posted_by);

        ArrayList<Message> outMessages = messageService.getAllMessagesByPosted_By(posted);
        
        return ResponseEntity.status(200).body(outMessages);
    }

    // 9. Spring test


}
