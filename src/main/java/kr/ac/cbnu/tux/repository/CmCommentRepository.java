package kr.ac.cbnu.tux.repository;

import kr.ac.cbnu.tux.domain.CmComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/* CommunityCommentRepository */
@Repository
public interface CmCommentRepository extends JpaRepository<CmComment, Long> {

}
