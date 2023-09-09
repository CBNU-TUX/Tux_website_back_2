package kr.ac.cbnu.tux.security;

import kr.ac.cbnu.tux.domain.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class UserAuthentication extends UsernamePasswordAuthenticationToken {

    public UserAuthentication(User principal, String credentials) {
        super(principal, credentials);
    }

    public UserAuthentication(User principal, String credentials,
                              Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
