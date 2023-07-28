package kr.ac.cbnu.tux.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.ac.cbnu.tux.domain.User;
import kr.ac.cbnu.tux.dto.LoginDTO;
import kr.ac.cbnu.tux.dto.UserDTO;
import kr.ac.cbnu.tux.enums.UserRole;
import kr.ac.cbnu.tux.service.UserService;
import kr.ac.cbnu.tux.utility.Security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
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
    public UserDTO getCurrent() {
        try {
            User user = (User) userService.loadUserByUsername(Security.getCurrentUsername());
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
    public void update(@PathVariable("id") Long id, @RequestBody User user) {
        try {
            User currentUser = (User) userService.loadUserByUsername(Security.getCurrentUsername());
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
    public void delete(@PathVariable("id") Long id) {
        try {
            User currentUser = (User) userService.loadUserByUsername(Security.getCurrentUsername());
            if (!Objects.deepEquals(currentUser.getId(), id)) {
                throw new Exception("User not matched");
            }

            userService.delete(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/api/user/{id}")
    @ResponseBody
    public UserDTO read(@PathVariable("id") Long id) {
        try {
            String currentUsername = Security.getCurrentUsername();
            String currentRole = Security.getCurrentUserRole();
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

    /* 최고관리자 전용 */
    @GetMapping("/api/admin/user/waiting")
    @ResponseBody
    public List<UserDTO> listAllGuest() {
        try {
            List<User> found = userService.listAllWaitingUser();
            return found.stream().map(user -> UserDTO.build(user)).toList();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/api/admin/user/member")
    @ResponseBody
    public List<UserDTO> listAllMemberNotGuest() {
        try {
            List<User> found = userService.listAllUserNotGuest();
            return found.stream().map(user -> UserDTO.build(user)).toList();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/api/admin/user/{id}/role/{role}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void changeUserRole(@PathVariable("id") Long id, @PathVariable("role") String role) {
        try {
            UserRole userRole = UserRole.GUEST;
            if (role.equals(UserRole.USER.name()))
                userRole = UserRole.USER;
            else if (role.equals(UserRole.MANAGER.name()))
                userRole = UserRole.MANAGER;
            else if (role.equals(UserRole.ADMIN.name()))
                userRole = UserRole.ADMIN;

            userService.changeUserRole(id, userRole);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/api/admin/user/{id}/password/{password}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void setTemporalPassword(@PathVariable("id") Long id, @PathVariable("password") String password) {
        try {
            userService.setTemporalPassword(id, password);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/api/admin/user/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void hardDelete(@PathVariable("id") Long id) {
        try {
            userService.hardDelete(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/api/admin/user/{id}/ban")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void ban(@PathVariable("id") Long id) {
        try {
            userService.ban(id);
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
