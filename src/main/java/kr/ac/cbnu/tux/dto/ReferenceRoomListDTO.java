package kr.ac.cbnu.tux.dto;

import kr.ac.cbnu.tux.domain.ReferenceRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReferenceRoomListDTO {

    private Long id;
    private String category;
    private String title;
    private OffsetDateTime createdDate;
    private OffsetDateTime editedDate;
    private Long view;
    private Integer comment;
    private String author;
    private Long likes;

    private String lecture;
    private String semester;
    private String professor;

    public static ReferenceRoomListDTO build(ReferenceRoom data) {
        return ReferenceRoomListDTO.builder()
                .id(data.getId())
                .category(data.getCategory().name())
                .title(data.getTitle())
                .createdDate(data.getCreatedDate())
                .editedDate(data.getEditedDate())
                .view(data.getView())
                .comment(data.getComments().stream().filter(c -> !c.getIsDeleted()).toList().size())
                .author((data.getIsAnonymized()) ? "익명" : data.getUser().getNickname())
                .likes(data.getLikes().stream().filter(l -> !l.getDislike()).count())
                .lecture(data.getLecture())
                .semester(data.getSemester())
                .professor(data.getProfessor())
                .build();
    }
}
