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
public class CommunityListDTO {

    private Long id;
    private String category;
    private String title;
    private OffsetDateTime createdDate;
    private OffsetDateTime editedDate;
    private Long view;
    private String author;

    public static CommunityListDTO build(Community post) {
        return CommunityListDTO.builder()
                .id(post.getId())
                .category(post.getCategory().name())
                .title(post.getTitle())
                .createdDate(post.getCreatedDate())
                .editedDate(post.getEditedDate())
                .view(post.getView())
                .author(post.getUser().getNickname())
                .build();
    }
}
