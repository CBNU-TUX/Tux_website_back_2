package kr.ac.cbnu.tux.repository;

import kr.ac.cbnu.tux.domain.User;
import kr.ac.cbnu.tux.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);

    List<User> findUserByRole(UserRole role);
    List<User> findUserByRoleNot(UserRole role);

    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByNickname(String nickname);
}
