package com.nguyenhan.maddemo1.config;


import com.nguyenhan.maddemo1.model.User;
import com.nguyenhan.maddemo1.repository.PersonalWorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PersonalWorkSecurity {
    @Autowired
    private PersonalWorkRepository personalWorkRepository;

    public boolean isOwner(Long personalworkId){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = Optional.ofNullable(principal)
                .filter(p -> p instanceof User)
                .map(p -> (User)p)
                .orElseThrow(()-> new UsernameNotFoundException("Check current user failed"));

        return personalWorkRepository.findById(personalworkId)
                .map(pw -> pw.getUser().getEmail().equals(user.getEmail()))
                .orElse(false);
    }
}
