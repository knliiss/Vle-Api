package dev.knalis.vleapi.validator;

import dev.knalis.vleapi.model.entity.user.User;
import dev.knalis.vleapi.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(User.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        boolean consists = userDetailsService.userExists(user.getUsername());
        if (consists) {
            errors.rejectValue("username", "400", "Username already exists");
        }
    }
}
