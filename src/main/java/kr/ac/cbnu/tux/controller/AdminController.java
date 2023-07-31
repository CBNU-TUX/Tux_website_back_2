package kr.ac.cbnu.tux.controller;

import kr.ac.cbnu.tux.domain.User;
import kr.ac.cbnu.tux.dto.UserDTO;
import kr.ac.cbnu.tux.enums.UserRole;
import kr.ac.cbnu.tux.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
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
}
