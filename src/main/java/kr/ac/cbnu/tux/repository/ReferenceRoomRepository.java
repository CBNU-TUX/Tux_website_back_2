package kr.ac.cbnu.tux.repository;

import kr.ac.cbnu.tux.domain.ReferenceRoom;
import kr.ac.cbnu.tux.enums.ReferenceRoomPostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReferenceRoomRepository extends JpaRepository<ReferenceRoom, Long> {
    // 메서드 이름 길이가 긴데, 이게 맞는 건지는...

    @Transactional(readOnly = true)
    Page<ReferenceRoom> findByIsDeletedFalseOrderByCreatedDateDesc(Pageable pageable);

    @Transactional(readOnly = true)
    Page<ReferenceRoom> findByIsDeletedFalseAndTitleContainingIgnoreCaseOrIsDeletedFalseAndLectureContainingIgnoreCaseOrIsDeletedFalseAndProfessorContainingIgnoreCaseOrderByCreatedDateDesc(
            String title, String lecture, String professor, Pageable pageable
    );

    @Transactional(readOnly = true)
    Page<ReferenceRoom> findByIsDeletedFalseAndCategoryOrderByCreatedDateDesc(ReferenceRoomPostType type, Pageable pageable);

    @Transactional(readOnly = true)
    Page<ReferenceRoom> findByIsDeletedFalseAndCategoryAndTitleContainingIgnoreCaseOrIsDeletedFalseAndCategoryAndLectureContainingIgnoreCaseOrIsDeletedFalseAndCategoryAndProfessorContainingIgnoreCaseOrderByCreatedDateDesc(
            ReferenceRoomPostType type, String title, ReferenceRoomPostType type1, String lecture, ReferenceRoomPostType type2, String professor, Pageable pageable
    );

    @Transactional(readOnly = true)
    Long countByIsDeletedFalse();

    @Transactional
    @Query("update ReferenceRoom d set d.view = d.view + 1 where d.id = :id")
    @Modifying
    void updateViewById(Long id);
}
