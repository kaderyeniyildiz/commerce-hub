package com.devlab.userservice.repository;

import com.devlab.userservice.entity.User;
import com.devlab.userservice.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query(""" 
                  select u from User u
                  where (:firstName is null or u.firstName = :firstName)
                  and (:lastName is null or u.lastName = :lastName)
                  and (:email is null or u.email = :email)
                  and (:phoneNumber is null or u.phoneNumber = :phoneNumber)
                  and (:status is null or u.status = :status)
            """)
    Page<User> searchUsersByCriteria(@Param("firstName") String firstName,
                                     @Param("lastName") String lastName,
                                     @Param("email") String email,
                                     @Param("phoneNumber") String phoneNumber,
                                     @Param("status") UserStatus status,
                                     Pageable pageable);
}