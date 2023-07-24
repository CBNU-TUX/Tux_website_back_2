package kr.ac.cbnu.tux.repository;

import jakarta.transaction.Transactional;
import kr.ac.cbnu.tux.domain.Community;
import kr.ac.cbnu.tux.enums.CommunityPostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    // 메서드 이름 길이가 긴데, 이게 맞는 건지는...

    Page<Community> findByIsDeletedFalseOrderByCreatedDateDesc(Pageable pageable);

    Page<Community> findByIsDeletedFalseAndTitleContainingIgnoreCaseOrderByCreatedDateDesc(String title, Pageable pageable);

    Page<Community> findByIsDeletedFalseOrderByViewDescCreatedDateDesc(Pageable pageable);

    Page<Community> findByIsDeletedFalseAndCategoryOrderByCreatedDateDesc(CommunityPostType type, Pageable pageable);

    Page<Community> findByIsDeletedFalseAndTitleContainingIgnoreCaseAndCategoryOrderByCreatedDateDesc(
            String title, CommunityPostType type, Pageable pageable
    );

    List<Community> findAllByIsDeletedFalse();

    Long countByIsDeletedFalse();

    @Transactional
    @Query("update Community p set p.view = p.view + 1 where p.id = :id")
    @Modifying
    void updateViewById(Long id);

}
