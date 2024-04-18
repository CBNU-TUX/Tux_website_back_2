package kr.ac.cbnu.tux.service;

import jakarta.transaction.Transactional;
import kr.ac.cbnu.tux.domain.Community;
import kr.ac.cbnu.tux.domain.Like;
import kr.ac.cbnu.tux.domain.ReferenceRoom;
import kr.ac.cbnu.tux.domain.User;
import kr.ac.cbnu.tux.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    private LikeRepository likeRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    @Transactional
    public void create(Community post, User user, Boolean dislike) throws Exception {
        if (!likeRepository.existsByPostAndUserAndDislike(post, user, dislike)) {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            like.setDislike(dislike);
            likeRepository.save(like);
        } else {
            throw new Exception("already like/dislike given");
        }
    }

    @Transactional
    public void create(ReferenceRoom data, User user, Boolean dislike) throws Exception {
        if (!likeRepository.existsByDataAndUserAndDislike(data, user, dislike)) {
            Like like = new Like();
            like.setData(data);
            like.setUser(user);
            like.setDislike(dislike);
            likeRepository.save(like);
        } else {
            throw new Exception("already like/dislike given");
        }
    }
}
