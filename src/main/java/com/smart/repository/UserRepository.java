package com.smart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smart.models.User;

@Repository
@EnableJpaRepositories
public interface UserRepository  extends JpaRepository<User, Integer>{
	
//	@Query("select u from user u where u.email=:email ")
//	public User getUserByUserName(@Param("email") String email);
//	public boolean existByEmail(String email);

	//	 @Query(value = "SELECT * FROM USERS WHERE email= ?1", nativeQuery = true)
	 
//	  User findByEmail(String emailAddress);
//	Optional<User> findByEmail(String emailAddress);

	@Query("select u from User u where u.email=:e")
	public User getUserByuserName(@Param("e")String email);


//	@Query(value = "SELECT * FROM USERS WHERE email=?1", nativeQuery = true)
//	public boolean getUserByEmail(String email);


}
