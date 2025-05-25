package spring.api.trijava.chuyendewebjavajob.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import spring.api.trijava.chuyendewebjavajob.service.UserService;

import java.util.Collections;

// ghi đè bean
@Component("userDetailsService")
public class UserDetailsCustom implements UserDetailsService {

    private final UserService userService;

    public UserDetailsCustom(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        spring.api.trijava.chuyendewebjavajob.domain.User user = this.userService.handleGetUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Username/password không hợp lệ");
        }
        return new User(user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

}
