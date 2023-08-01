package kr.ac.cbnu.tux.service;

import jakarta.transaction.Transactional;
import kr.ac.cbnu.tux.domain.User;
import kr.ac.cbnu.tux.dto.LoginDTO;
import kr.ac.cbnu.tux.enums.UserRole;
import kr.ac.cbnu.tux.repository.UserRepository;
import kr.ac.cbnu.tux.security.JwtTokenProvider;
import kr.ac.cbnu.tux.security.UserAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {

    private static final String PASSWORD_RULE = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d@$!%*#?&]{8,}$";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Long create(User user) throws Exception {
        if (!userRepository.existsByUsername(user.getUsername()) && !user.getUsername().equals("anonymousUser")
                && Pattern.matches(PASSWORD_RULE, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(UserRole.GUEST);
            user.setBanned(false);
            user.setBanned(false);
            user.setDeleted(false);
            user.setCreatedDate(OffsetDateTime.now());
            userRepository.save(user);
            return user.getId();
        } else {
            throw new Exception("username is not unique / password rule not matched");
        }
    }

    @Transactional
    public void update(Long id, User updated) throws Exception {
        Optional<User> opUser = userRepository.findById(id);
        if (opUser.isPresent()) {
            User user = opUser.get();
            if (updated.getNickname() != null)
                user.setNickname(updated.getNickname());
            if (updated.getPassword() != null)
                user.setPassword(passwordEncoder.encode(updated.getPassword()));
            if (updated.getEmail() != null)
                user.setEmail(updated.getEmail());
            if (updated.getDepartment() != null)
                user.setDepartment(updated.getDepartment());
            if (updated.getStudentNumber() != null)
                user.setStudentNumber(updated.getStudentNumber());
            if (updated.getPhoneNumber() != null)
                user.setPhoneNumber(updated.getPhoneNumber());
        } else {
            throw new Exception("User not found");
        }
    }

    @Transactional
    public void delete(Long id) throws Exception {
        Optional<User> opUser = userRepository.findById(id);
        if (opUser.isPresent()) {
            User user = opUser.get();
            user.setDeleted(true);
            user.setDeletedDate(OffsetDateTime.now());
        } else {
            throw new Exception("User not found");
        }
    }

    public void hardDelete(Long id) throws Exception {
        User user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
    }

    @Transactional
    public void ban(Long id) throws Exception {
        User user = userRepository.findById(id).orElseThrow();
        user.setBanned(true);
    }

    @Transactional
    public void changeUserRole(Long id, UserRole role) throws Exception {
        User user = userRepository.findById(id).orElseThrow();
        user.setRole(role);
    }

    @Transactional
    public void setTemporalPassword(Long id, String password) throws Exception {
        User user = userRepository.findById(id).orElseThrow();
        user.setPassword(passwordEncoder.encode(password));
    }

    public String tryLogin(LoginDTO loginDTO) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not present"));

        if (user.isBanned() || user.isLocked() || user.isDeleted())
            throw new UsernameNotFoundException("User not present");

        if (passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            Authentication authentication = new UserAuthentication(
                    loginDTO.getUsername(), loginDTO.getPassword(), user.getAuthorities()
            );
            return JwtTokenProvider.generateToken(authentication);
        } else {
            throw new IllegalArgumentException("Password not matched");
        }
    }

    public Optional<User> read(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not present"));
    }

    public Boolean canUseAsUsername(String username) {
        return !userRepository.existsByUsername(username);
    }

    public Boolean canUseAsNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    public Boolean canUseAsEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    public List<User> listAllWaitingUser() {
        return userRepository.findUserByRole(UserRole.GUEST);
    }

    public List<User> listAllUserNotGuest() {
        return userRepository.findUserByRoleNot(UserRole.GUEST);
    }
}
