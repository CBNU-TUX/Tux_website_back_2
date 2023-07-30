package kr.ac.cbnu.tux.repository;

import kr.ac.cbnu.tux.domain.RfComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// ReferenceRoomComment Repository
@Repository
public interface RfCommentRepository extends JpaRepository<RfComment, Long> {


}
