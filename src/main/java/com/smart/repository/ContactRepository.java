package com.smart.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.smart.models.Contact;

import java.util.List;


@Repository
public interface ContactRepository  extends JpaRepository<Contact, Integer>{
    @Query("FROM Contact  AS c WHERE c.user.id=:userid")
    public Page<Contact> findContactByUser(@Param("userid") int userid, Pageable pePageable);

}
