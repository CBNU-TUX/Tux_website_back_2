package kr.ac.cbnu.tux.dto;

import kr.ac.cbnu.tux.domain.Community;
import kr.ac.cbnu.tux.domain.Like;
import kr.ac.cbnu.tux.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunityDTO {

    private Long id;
    private String category;
    private String title;
    private String body;
    private OffsetDateTime createdDate;
    private OffsetDateTime editedDate;
    private Long view;
    private Long authorId;
    private String author;
    List<AttachmentDTO> files;
    private List<CmCommentDTO> comments;
    private Long likes;
    List<String> likedPeople;
    private Long dislikes;

    public static CommunityDTO build(Community post) {
        List<AttachmentDTO> files = post.getAttachments().stream()
                .sorted((c1, c2) -> c1.getOrder().compareTo(c2.getOrder()))
                .map(c -> AttachmentDTO.build(c))
                .toList();

        List<CmCommentDTO> comments = post.getComments().stream()
                .filter(c -> !c.getIsDeleted())
                .sorted((c1, c2) -> c1.getCreatedDate().compareTo(c2.getCreatedDate()))
                .map(c -> CmCommentDTO.build(c))
                .toList();

        Long likes = post.getLikes().stream()
                .filter(l -> !l.getDislike())
                .count();

        List<String> likedPeople = post.getLikes().stream()
                .filter(l -> !l.getDislike() && !l.getUser().isDeleted())
                .map(l -> l.getUser().getNickname())
                .toList();

        Long dislikes = post.getLikes().stream()
                .filter(l -> l.getDislike())
                .count();

        return CommunityDTO.builder()
                .id(post.getId())
                .category(post.getCategory().name())
                .title(post.getTitle())
                .body(post.getBody())
                .createdDate(post.getCreatedDate())
                .editedDate(post.getEditedDate())
                .view(post.getView())
                .authorId(post.getUser().getId())
                .author(post.getUser().getNickname())
                .files(files)
                .comments(comments)
                .likes(likes)
                .likedPeople(likedPeople)
                .dislikes(dislikes)
                .build();
    }


}
