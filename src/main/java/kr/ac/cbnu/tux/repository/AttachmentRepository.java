package kr.ac.cbnu.tux.repository;

import kr.ac.cbnu.tux.domain.Attachment;
import kr.ac.cbnu.tux.domain.Community;
import kr.ac.cbnu.tux.domain.ReferenceRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Optional<Attachment> findByFilenameAndPost(String filename, Community post);

    Optional<Attachment> findByFilenameAndData(String filename, ReferenceRoom data);

    @Transactional
    @Query("update Attachment a set a.downloadCount = a.downloadCount + 1 where a.id = :id")
    @Modifying
    void increaseDownloadCountById(Long id);
}
