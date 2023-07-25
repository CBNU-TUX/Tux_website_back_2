package kr.ac.cbnu.tux.dto;

import kr.ac.cbnu.tux.domain.CmComment;
import kr.ac.cbnu.tux.domain.Community;
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
    private List<CmCommentDTO> comments;

    public static CommunityDTO build(Community post) {
        List<CmCommentDTO> comments = post.getComments().stream()
                .filter(c -> !c.getIsDeleted())
                .sorted((c1, c2) -> c1.getCreatedDate().compareTo(c2.getCreatedDate()))
                .map(c -> CmCommentDTO.build(c))
                .toList();

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
                .comments(comments)
                .build();
    }


}
