package com.service.authentication.service;

import com.service.authentication.config.Translator;
import com.service.authentication.exception.ResourceNotFoundException;
import com.service.authentication.model.AppUser;
import com.service.authentication.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j(topic = "USER-SERVICE")
public class UserService implements UserDetailsService {
    @Value("${jwt.reset.token.validity}")
    private long resetTokenValidity;
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
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /**
     * Get user by email
     *
     * @param email
     * @return
     * @throws ResourceNotFoundException
     */
    public AppUser getUserByEmail(String email) throws ResourceNotFoundException {
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
    public void sendResetTokenToEmail(String email) throws ResourceNotFoundException {
        // get user by email
        AppUser appUser = getUserByEmail(email);

        // reset password into database
        String resetToken = RandomStringUtils.randomNumeric(10);
        Date expiryResetDate = new Date(System.currentTimeMillis() + (resetTokenValidity * 60 * 1000));

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

        // save reset password token into Redis
        tokenService.saveResetToken(resetToken, email, expiryResetDate);
    }

    /**
     * Reset old password and update new password
     *
     * @param resetToken
     * @param newPassword
     */
    public void resetAndUpdatePassword(String resetToken, String newPassword) throws ResourceNotFoundException {
        String email = tokenService.validateResetToken(resetToken);
        AppUser user = getUserByEmail(email);
        user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        userRepo.save(user);
    }

}
