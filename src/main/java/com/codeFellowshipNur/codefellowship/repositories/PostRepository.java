package com.codeFellowshipNur.codefellowship.repositories;

import com.codeFellowshipNur.codefellowship.models.ApplicationUser;
import com.codeFellowshipNur.codefellowship.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    public Post findByBody(String body);
}

