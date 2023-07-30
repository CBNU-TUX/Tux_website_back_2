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
    private String author;

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
                .author((data.getIsAnonymized()) ? "익명" : data.getUser().getNickname())
                .lecture(data.getLecture())
                .semester(data.getSemester())
                .professor(data.getProfessor())
                .build();
    }
}
