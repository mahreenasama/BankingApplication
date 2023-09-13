package com.redmath.bankingapp.user;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    public User findByUname(String uname){
        return userRepository.findByUname(uname);
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public User updateUserById(Long id, User user){
        user.setId(id);
        return userRepository.save(user);
    }

    public User getUserByAccountId(Long accountId, Authentication auth){
        User user = userRepository.findByUname(auth.getName());

        if(user.getAccount().getId().toString().equals(accountId.toString())){
            return userRepository.findByAccountId(accountId);
        }
        return null;
    }

    public User changePasswordByAccountId(Long accountId, String newPassword, Authentication auth){
        User user = userRepository.findByUname(auth.getName());

        if(user.getAccount().getId().toString().equals(accountId.toString())){
            String hashedPassword = new BCryptPasswordEncoder().encode(newPassword);
            User updatedUser = new User(user.getUname(), hashedPassword, user.getRoles(), user.getStatus(), user.getAccount());
            updatedUser.setId(user.getId());
            return userRepository.save(updatedUser);
        }
        return null;
    }


    //---new---
    @Cacheable("users")
    public UserDetails loadUserByUsername(String jti, String userName) throws UsernameNotFoundException {
        return loadUserByUsername(userName);
    }
    //---new---
    @Override
    public UserDetails loadUserByUsername(String uname) throws UsernameNotFoundException {
        logger.info("overrrrrrrrrrrrrrrrrrrrrloaded method");
        User user = userRepository.findByUname(uname);
        if (user == null) {
            logger.info("overrrrrrrrrrrr user null");

            throw new UsernameNotFoundException("Invalid user: " + uname);
        }
        logger.info("overrrrrrrrrrrr user not null");

        return new org.springframework.security.core.userdetails.User(user.getUname(), user.getPassword(), true,
                true, true, true, AuthorityUtils.commaSeparatedStringToAuthorityList(user.getRoles()));
    }
}


