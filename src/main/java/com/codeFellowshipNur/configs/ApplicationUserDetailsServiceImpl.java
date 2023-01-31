package com.codeFellowshipNur.configs;


import com.codeFellowshipNur.repositories.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// TODO: Step 3: ApplicationUserDetailsServiceImpl implements UserDetailsService
@Service // Sprong autodetects and loads
public class ApplicationUserDetailsServiceImpl implements UserDetailsService {
    // TODO: Step 3a: AutoWire ApplicationUserRepository
   @Autowired
    ApplicationUserRepository applicationUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) applicationUserRepository.findByUsername(username);
    }
}
