package kr.ac.cbnu.tux.dto;

import kr.ac.cbnu.tux.domain.Attachment;
import kr.ac.cbnu.tux.domain.ReferenceRoom;
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
public class GalleryListDTO {

    private Long id;
    private String category;
    private AttachmentDTO mainImage;
    private String title;
    private OffsetDateTime createdDate;
    private OffsetDateTime editedDate;
    private Long view;
    private String author;

    private String lecture;
    private String semester;
    private String professor;

    public static GalleryListDTO build(ReferenceRoom data) {
        List<Attachment> imageFiles = data.getAttachments().stream()
                .filter(c -> c.getIsImage())
                .sorted((c1, c2) -> c1.getOrder().compareTo(c2.getOrder()))
                .toList();

        return GalleryListDTO.builder()
                .id(data.getId())
                .category(data.getCategory().name())
                .mainImage((imageFiles.size() > 0) ? AttachmentDTO.build(imageFiles.get(0)) : null)
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
