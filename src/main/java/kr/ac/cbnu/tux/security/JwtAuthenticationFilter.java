package kr.ac.cbnu.tux.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.cbnu.tux.domain.User;
import kr.ac.cbnu.tux.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Autowired
    public JwtAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getJwtFromCookie(request);
            if (JwtTokenProvider.validateToken(token)) {
                String username = JwtTokenProvider.getUsernameFromJwt(token);

                try {
                    User user = userRepository.findUserByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException("User not present"));

                    UserAuthentication authentication = new UserAuthentication(user, null, user.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    request.setAttribute("unauthorization", "401 Error while authentication");
                }
            } else {
                request.setAttribute("unauthorization", "401 No key or Key expiration");
            }
        } catch (Exception ignored) { }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for(Cookie cookie : cookies) {
            if(cookie.getName().equals("token")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
