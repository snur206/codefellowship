package com.codeFellowshipNur.repositories;

import com.codeFellowshipNur.models.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

// TODO: Step 1: Create ApplicationUser Repo
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {
    public ApplicationUser findByUsername(String username);
}
