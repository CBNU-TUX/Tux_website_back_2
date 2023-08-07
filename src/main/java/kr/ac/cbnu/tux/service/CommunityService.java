package kr.ac.cbnu.tux.service;

import jakarta.transaction.Transactional;
import kr.ac.cbnu.tux.domain.*;
import kr.ac.cbnu.tux.enums.CommunityPostType;
import kr.ac.cbnu.tux.enums.UserRole;
import kr.ac.cbnu.tux.repository.CmCommentRepository;
import kr.ac.cbnu.tux.repository.CommunityRepository;
import kr.ac.cbnu.tux.utility.Sanitizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CmCommentRepository cmCommentRepository;
    private final Sanitizer sanitizer;

    @Autowired
    public CommunityService(CommunityRepository communityRepository, CmCommentRepository cmCommentRepository, Sanitizer sanitizer) {
        this.communityRepository = communityRepository;
        this.cmCommentRepository = cmCommentRepository;
        this.sanitizer = sanitizer;
    }

    /* 파일 업로드 및 글쓰기 */

    @Transactional
    public void createWithoutFileUpload(CommunityPostType type, Community post, User user) {
        post.setCategory(type);
        post.setBody(sanitizer.sanitize(post.getBody()));
        post.setCreatedDate(OffsetDateTime.now());
        post.setIsDeleted(false);
        post.setView(0L);
        post.setUser(user);
        communityRepository.save(post);
    }

    @Transactional
    public Community temporalCreate(CommunityPostType type, User user) {
        // 첨부파일 사전 업로드
        Community post = new Community();
        post.setCategory(type);
        post.setTitle("Auto creation by uploading file");
        post.setBody(" ");
        post.setIsDeleted(true);
        post.setCreatedDate(OffsetDateTime.now());
        post.setView(0L);
        post.setUser(user);

        return communityRepository.save(post);
    }

    @Transactional
    public void addAttachment(Attachment file, Community post) {
        post.addAttachment(file);
    }

    @Transactional
    public void updateAfterTemporalCreate(Long id, Community updated, User user) throws Exception {
        Community post = communityRepository.findById(id).orElseThrow();
        if (user.getId().equals(post.getUser().getId())) {
            post.setTitle(updated.getTitle());
            post.setBody(updated.getBody());
            post.setIsDeleted(false);
        }
        else {
            throw new Exception("User not matched");
        }
    }

    public Optional<Community> getData(Long id) {
        return communityRepository.findById(id);
    }


    @Transactional
    public void update(Long id, Community updated, User user) throws Exception {
        Optional<Community> opPost = communityRepository.findById(id);
        if (opPost.isPresent()) {
            Community post = opPost.get();
            if (!Objects.equals(post.getUser().getId(), user.getId())) {
                throw new Exception("User not matched");
            }

            if (updated.getTitle() != null) {
                post.setTitle(updated.getTitle());
            }
            if (updated.getBody() != null) {
                post.setBody(sanitizer.sanitize(updated.getBody()));
            }
            post.setEditedDate(OffsetDateTime.now());
        } else {
            throw new Exception("Not found");
        }
    }

    @Transactional
    public void delete(Long id, User user) throws Exception {
        Community post = communityRepository.findById(id).orElseThrow();

        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.MANAGER &&
            !Objects.equals(post.getUser().getId(), user.getId())) {
            throw new Exception("User not matched");
        }

        post.setIsDeleted(true);
        post.setDeletedDate(OffsetDateTime.now());
    }

    public Optional<Community> read(Long id) {
        communityRepository.updateViewById(id);
        return communityRepository.findById(id);
    }


    /* 게시판 리스트 조회 */

    public Page<Community> list(Pageable pageable) {
        return communityRepository.findByIsDeletedFalseOrderByCreatedDateDesc(pageable);
    }

    public Page<Community> searchList(String query, Pageable pageable) {
        return communityRepository.findByIsDeletedFalseAndTitleContainingIgnoreCaseOrderByCreatedDateDesc(query, pageable);
    }

    public Page<Community> listByCategory(Pageable pageable, CommunityPostType type) {
        return communityRepository.findByIsDeletedFalseAndCategoryOrderByCreatedDateDesc(type, pageable);
    }

    public Page<Community> searchListByCategory(String query, Pageable pageable, CommunityPostType type) {
        return communityRepository.findByIsDeletedFalseAndTitleContainingIgnoreCaseAndCategoryOrderByCreatedDateDesc(query, type, pageable);
    }

    public List<Community> listAll() {
        return communityRepository.findAllByIsDeletedFalse();
    }

    public Long count() {
        return communityRepository.countByIsDeletedFalse();
    }


    /* 댓글 관련 코드 */

    @Transactional
    public void addComment(Long id, CmComment comment, User user) throws Exception {
        Optional<Community> opPost = communityRepository.findById(id);
        if (opPost.isPresent()) {
            Community post = opPost.get();
            comment.setCreatedDate(OffsetDateTime.now());
            comment.setIsDeleted(false);
            comment.setPost(post);
            comment.setUser(user);

            cmCommentRepository.save(comment);
        } else {
            throw new Exception("Not found");
        }
    }

    @Transactional
    public void deleteComment(Long commentId, User user)  throws Exception {
        Optional<CmComment> opComment = cmCommentRepository.findById(commentId);
        if (opComment.isPresent()) {
            CmComment comment = opComment.get();
            if (!Objects.equals(comment.getUser().getId(), user.getId())) {
                throw new Exception("User not matched");
            }

            comment.setIsDeleted(true);
            comment.setDeletedDate(OffsetDateTime.now());
        } else {
            throw new Exception("Not found");
        }
    }
}
