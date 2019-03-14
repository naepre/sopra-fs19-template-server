package ch.uzh.ifi.seal.soprafs19.controller;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import ch.uzh.ifi.seal.soprafs19.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RestControllerAdvice //handling HTTP status codes
public class UserController {

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }




    @GetMapping("/users")
    Iterable<User> all() {
        return service.getUsers();
    }





    @PostMapping("/users")
    @ResponseBody //Custom response body
    public ResponseEntity<?> createUser(@Valid @RequestBody User newUser, HttpServletRequest request) {

        //To distinguish a login attempt and a register, client specifies in a custom header field
        String requestType = request.getHeader("requestType");
        if(requestType.contains("register")){
            try {

                //Attempt to create a new user through createUser service handler
                return new ResponseEntity<>(this.service.createUser(newUser), HttpStatus.CREATED); //201 resource created

            } catch (org.springframework.dao.DataIntegrityViolationException e) {

                return ResponseEntity
                        .status(HttpStatus.CONFLICT) // http status code 409
                        .body(Collections.singletonMap("error", "Username unavailable"));
            }
        }

        //attempt to login user if not 'register' request header type
        //If user exists, return the user entity else return the correct response
        User userExists = this.service.attemptLogin(newUser);
        if(userExists != null){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(this.service.attemptLogin(newUser));
        }
        return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "Username and/or password incorrect"));

    }


    @GetMapping("/users/{userId}")
    @ResponseBody
    public ResponseEntity<?> getBasicUserProfileOnly(@PathVariable Long userId, HttpServletRequest request){

        //Find user by ID, given as http GET parameter
        User basicUserProfile = service.getBasicUserProfile(userId);

        //If the user exists, return in custom http response entity
        if(!basicUserProfile.equals(Optional.empty())){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(basicUserProfile);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "user with user ID: "+userId+" was not found"));
    }


    @PutMapping("/users/{userId}")
    @ResponseBody
    @CrossOrigin //Allow cross origin
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User userUpdate){

        //updateUser service returns  boolean if user entity was updated
        if(service.updateUser(userId, userUpdate)){
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "user with user ID: "+userId+" was not found"));
    }

}
