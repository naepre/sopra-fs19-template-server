package ch.uzh.ifi.seal.soprafs19.service;

import ch.uzh.ifi.seal.soprafs19.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs19.repository.UserRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserRepositoryCustom userRepositoryCustom;


    @Autowired
    public UserService(UserRepository userRepository, UserRepositoryCustom userRepositoryCustom) {
        this.userRepository = userRepository;
        this.userRepositoryCustom = userRepositoryCustom;
    }

    public Iterable<User> getUsers() {
        return this.userRepository.findAll();
    }


    //This can be improved to match agaisnt the JPA projection interface
    public User attemptLogin(User user){
        return this.userRepository.findByUsername(user.getUsername());
    }

    public User getBasicUserProfile(Long userId){
        return this.userRepository.getById(userId);
    }

    public boolean updateUser(Long userId, User update){

        //get user to update by ID
        User userToUpdate = this.userRepository.getById(userId);

        //If user exists, re-set username and birthday and save to repository
        if(userToUpdate != null) {
            userToUpdate.setUsername(update.getUsername());
            userToUpdate.setBirthday(update.getBirthday());
            userRepository.save(userToUpdate);
            log.debug("Updated Information for User: {}", userToUpdate);
            return true;
        }

        return false;
    }

    public User createUser(User newUser) {

        Calendar calendar = Calendar.getInstance();
        java.sql.Date date = new java.sql.Date(calendar.getTime().getTime());

        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
        newUser.setCreationDate(date); //creation date field

        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

}
