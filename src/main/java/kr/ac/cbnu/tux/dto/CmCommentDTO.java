package kr.ac.cbnu.tux.dto;

import kr.ac.cbnu.tux.domain.CmComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CmCommentDTO {


    private Long id;
    private String body;
    private OffsetDateTime createdDate;
    private Long authorId;
    private String author;

    public static CmCommentDTO build(CmComment comment) {

        return CmCommentDTO.builder()
                .id(comment.getId())
                .body(comment.getBody())
                .createdDate(comment.getCreatedDate())
                .authorId(comment.getUser().getId())
                .author(comment.getUser().getNickname())
                .build();
    }

}
