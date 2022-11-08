package com.service.authentication.service;

import com.service.authentication.config.Translator;
import com.service.authentication.exception.ResourceNotFoundException;
import com.service.authentication.model.AppUser;
import com.service.authentication.model.Token;
import com.service.authentication.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j(topic = "USER-SERVICE")
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private MailService mailService;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AppUser appUser = userRepo.findByUsername(username);
        if (appUser == null) {
            log.info("User not found in the database");
            throw new UsernameNotFoundException(Translator.toLocale("user-name-not-found"));
        } else {
            log.info("User found in the database {}", username);
        }
        return new org.springframework.security.core.userdetails.User(appUser.getUsername(), appUser.getPassword(), getAuthority(username));
    }

    /**
     * Get user authorities
     *
     * @param username
     * @return
     */
    private List<SimpleGrantedAuthority> getAuthority(String username) {
        List<String> roles = userRepo.findRoleByUsername(username);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
            authorities.add(authority);
        }

        return authorities;
    }

    /**
     * Get user by email
     *
     * @param email
     * @return
     * @throws ResourceNotFoundException
     */
    public AppUser getByEmail(String email) throws ResourceNotFoundException {
        log.info("Fetching user {} from the database", email);
        AppUser user = userRepo.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException(Translator.toLocale("user-email-not-found"));
        }
        return user;
    }

    /**
     * Send link confirm in order to reset password token to email
     *
     * @param email
     * @throws ResourceNotFoundException
     * @throws MessagingException
     */
    public void sendResetTokenToEmail(String email) throws ResourceNotFoundException, MessagingException {
        // get user by email
        AppUser appUser = getByEmail(email);

        // reset password into database
        String resetToken = RandomStringUtils.randomNumeric(10);

        // Todo: replace domain name in order to navigate to change password page
        String resetLink = "http://domain.name/reset-password?token=" + resetToken;

        // send new password to email
        // Todo: Need to replace email content
        String from = "no-reply@service.api";
        String subject = "[API-Service] Reset password request";
        String body = String.format("Dear %s,\n\n" +
                "If your forgot password\n" +
                "Click link to reset password: %s\n\n" +
                "- BackEnd Team", appUser.getUsername(), resetLink);

        // send mail to user
        mailService.sendEmail(from, email, subject, body);

        // save reset password token into the database
        tokenService.saveResetToken(appUser, resetToken);
    }

    /**
     * Reset old password and update new password
     *
     * @param resetToken
     * @param newPassword
     */
    public void resetAndUpdatePassword(String resetToken, String newPassword) throws ResourceNotFoundException {
        Token token = tokenService.findByToken(resetToken);
        if (token == null) {
            throw new ResourceNotFoundException(Translator.toLocale("reset-token-not-found"));
        }
        if (token.getExpiryDate().before(new Date())) {
            throw new ResourceNotFoundException(Translator.toLocale("reset-token-expired"));
        }

        // Update new password
        AppUser appUser = token.getUser();
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        appUser.setPassword(encoder.encode(newPassword));
        userRepo.save(appUser);
    }

}
