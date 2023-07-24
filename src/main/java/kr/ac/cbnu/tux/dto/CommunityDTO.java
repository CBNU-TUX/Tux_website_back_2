package kr.ac.cbnu.tux.dto;

import kr.ac.cbnu.tux.domain.Community;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

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

    public static CommunityDTO build(Community post) {
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
                .build();
    }

}
