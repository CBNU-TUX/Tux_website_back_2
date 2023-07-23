package kr.ac.cbnu.tux.repository;

import kr.ac.cbnu.tux.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);
    Optional<User> findUserByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByNickname(String nickname);
}
