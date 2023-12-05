package com.example.service;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.repository.MessageRepository;

@Service
@Transactional
public class MessageService {

    MessageRepository messageRepository;

    AccountRepository accountRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, AccountRepository accountRepository){
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    } 

    /**
     * Calls MessageRepository methods to persist messages to our database
     * @param message the message we wish to persist
     * @return the same message back, with an added message_id, or null if we were unable to persist the message
     */
    public Message postNewMessage(Message message) {
        // Verify the message can be used
        // Verify the message is neiter too long or too short
        if(message.getMessage_text().length() <= 0 || message.getMessage_text().length() > 254) {
            return null;
        }

        // Verify that the message's {posted_by} refers to a real account
        if(!accountRepository.existsById(message.getPosted_by())){
            return null;
        }

        // Checks done, persist message
        return messageRepository.save(message);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message getMessageById(Integer id) {
        return messageRepository.getById(id);
    }

    public boolean checkIfMessageExists(Integer id) {
        return messageRepository.existsById(id);
    }

    public void deleteMessageById(Integer id) {
        messageRepository.deleteById(id);
    }

    public Message updateMessageById(String body, Integer id) {
        // Check to make sure the message is the correct length
        if(body.length() <= 0 || body.length() > 254) {
            return null;
        }
        
        // Pull out the message, replace the message text, save the message object and then return it
        Message secureMessage = messageRepository.getById(id);
        secureMessage.setMessage_text(body);
        messageRepository.save(secureMessage);
        return messageRepository.getById(id);
    }

    public ArrayList<Message> getAllMessagesByPosted_By(Integer posted_by) {
        Optional<ArrayList<Message>> messages = messageRepository.findMessageByPosted_by(posted_by.toString());
        if(messages.isPresent()){
            return messages.get();
        }
        // If we don't exist return an empty ArrayList
        return new ArrayList<Message>();
    }
}
