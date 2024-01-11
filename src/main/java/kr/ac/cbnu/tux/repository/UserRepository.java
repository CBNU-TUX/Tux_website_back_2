package kr.ac.cbnu.tux.repository;

import kr.ac.cbnu.tux.domain.User;
import kr.ac.cbnu.tux.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Transactional(readOnly = true)
    Optional<User> findUserByUsername(String username);

    @Transactional(readOnly = true)
    List<User> findByIsDeletedFalseAndRole(UserRole role);
    @Transactional(readOnly = true)
    List<User> findByIsDeletedFalseAndRoleNot(UserRole role);

    @Transactional(readOnly = true)
    Boolean existsByEmail(String email);
    @Transactional(readOnly = true)
    Boolean existsByUsername(String username);
    @Transactional(readOnly = true)
    Boolean existsByNickname(String nickname);
}
