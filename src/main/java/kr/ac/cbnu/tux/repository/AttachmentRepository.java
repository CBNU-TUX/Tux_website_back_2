package kr.ac.cbnu.tux.repository;

import kr.ac.cbnu.tux.domain.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {


}
