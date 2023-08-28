package kr.ac.cbnu.tux.repository;

import kr.ac.cbnu.tux.domain.Community;
import kr.ac.cbnu.tux.enums.CommunityPostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    // 메서드 이름 길이가 긴데, 이게 맞는 건지는...

    @Transactional(readOnly = true)
    Page<Community> findByIsDeletedFalseOrderByCreatedDateDesc(Pageable pageable);

    @Transactional(readOnly = true)
    Page<Community> findByIsDeletedFalseAndTitleContainingIgnoreCaseOrderByCreatedDateDesc(String title, Pageable pageable);

    @Transactional(readOnly = true)
    Page<Community> findByIsDeletedFalseOrderByViewDescCreatedDateDesc(Pageable pageable);

    @Transactional(readOnly = true)
    Page<Community> findByIsDeletedFalseAndCategoryOrderByCreatedDateDesc(CommunityPostType type, Pageable pageable);

    @Transactional(readOnly = true)
    Page<Community> findByIsDeletedFalseAndTitleContainingIgnoreCaseAndCategoryOrderByCreatedDateDesc(
            String title, CommunityPostType type, Pageable pageable
    );

    @Transactional(readOnly = true)
    List<Community> findAllByIsDeletedFalse();

    @Transactional(readOnly = true)
    Long countByIsDeletedFalse();

    @Transactional
    @Query("update Community p set p.view = p.view + 1 where p.id = :id")
    @Modifying
    void updateViewById(Long id);

}
