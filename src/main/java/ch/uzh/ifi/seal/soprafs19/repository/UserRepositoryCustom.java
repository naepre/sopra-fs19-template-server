package ch.uzh.ifi.seal.soprafs19.repository;

import ch.uzh.ifi.seal.soprafs19.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


//A projection interface to retrieve only a subset of columns from User

@Repository("userRepositoryCustom")
public interface UserRepositoryCustom extends CrudRepository<User, Long>{

	//Optional<User> findById(Long userId);
	//User getId();
	//User getUsername();
	//User getCreationDate();
	//User getStatus();
	//User getBirthday();

}