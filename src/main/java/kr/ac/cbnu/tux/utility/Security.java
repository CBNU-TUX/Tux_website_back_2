package kr.ac.cbnu.tux.utility;

import kr.ac.cbnu.tux.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Iterator;

public class Security {

    /**
     * @return 현재 로그인한 사용자의 username을 반환합니다. (토큰이 없거나 잘못된 경우 "anonymousUser" 반환)
     */
    public static String getCurrentUsername()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((UserDetails)authentication.getPrincipal()).getUsername();
    }

    /**
     * @return 현재 로그인한 사용자의 role(권한)을 반환합니다. (토큰이 없거나 잘못된 경우 "ROLE_ANONYMOUS" 반환)
     */
    public static String getCurrentUserRole()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> it = authorities.iterator();
        return it.next().toString();
    }
}
