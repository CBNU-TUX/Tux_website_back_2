package kr.ac.cbnu.tux.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.cbnu.tux.domain.User;
import kr.ac.cbnu.tux.dto.LoginDTO;
import kr.ac.cbnu.tux.dto.UserDTO;
import kr.ac.cbnu.tux.enums.UserRole;
import kr.ac.cbnu.tux.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/auth")
    @ResponseStatus(code = HttpStatus.OK)
    public void login(final HttpServletRequest request, final HttpServletResponse response,
                      @RequestBody LoginDTO loginDTO) {
        try {
            String token = userService.tryLogin(loginDTO);
            Cookie tokenCookie = createTokenCookie(token, 168 * 60 * 60);
            response.addCookie(tokenCookie);

        } catch (Exception e) {
            Cookie emptyCookie = createTokenCookie(null, 0);
            response.addCookie(emptyCookie);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/api/auth")
    @ResponseStatus(code = HttpStatus.OK)
    public void logout(final HttpServletRequest request, final HttpServletResponse response) {
        Cookie tokenCookie = createTokenCookie(null, 0);
        response.addCookie(tokenCookie);
    }

    @GetMapping("/api/auth")
    @ResponseBody
    public UserDTO getCurrent(@AuthenticationPrincipal User user) {
        try {
            return UserDTO.build(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/api/user")
    @ResponseStatus(code = HttpStatus.CREATED)
    public void create(@RequestBody User user) {
        try {
            userService.create(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/api/user/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void update(@PathVariable("id") Long id, @RequestBody User user, @AuthenticationPrincipal User currentUser) {
        try {
            if (!Objects.deepEquals(currentUser.getId(), id)) {
                throw new Exception("User not matched");
            }

            userService.update(id, user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/api/user/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void delete(@PathVariable("id") Long id, @AuthenticationPrincipal User currentUser) {
        try {
            if (!Objects.deepEquals(currentUser.getId(), id)) {
                throw new Exception("User not matched");
            }

            userService.userDelete(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/api/user/{id}")
    @ResponseBody
    public UserDTO read(@PathVariable("id") Long id, @AuthenticationPrincipal User currentUser) {
        try {
            String currentUsername = currentUser.getUsername();
            String currentRole = currentUser.getRole().name();
            if (currentUsername.equals("anonymousUser") ||
                    !currentUsername.equals(userService.read(id).orElseThrow().getUsername()) ||
                    !currentRole.equals(UserRole.MANAGER.name()) && !currentRole.equals(UserRole.ADMIN.name())) {
                throw new Exception("No permission");
            }

            return UserDTO.build(userService.read(id).orElseThrow());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
        }
    }

    @GetMapping("/api/user/check/username")
    @ResponseBody
    public Boolean canUseAsUsername(@RequestParam("username") String username) {
        try {
            return userService.canUseAsUsername(username);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/api/user/check/nickname")
    @ResponseBody
    public Boolean canUseAsNickname(@RequestParam("nickname") String nickname) {
        try {
            return userService.canUseAsNickname(nickname);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


    private Cookie createTokenCookie(String token, int age) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(age);
        cookie.setPath("/");
        return cookie;
    }
}
