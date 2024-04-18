package kr.ac.cbnu.tux.repository;

import kr.ac.cbnu.tux.domain.Community;
import kr.ac.cbnu.tux.domain.Like;
import kr.ac.cbnu.tux.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    Boolean existsByPostAndUserAndDislike(Community post, User user, Boolean dislike);
}
