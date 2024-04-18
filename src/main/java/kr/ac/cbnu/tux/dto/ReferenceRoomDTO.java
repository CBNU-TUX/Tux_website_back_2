package kr.ac.cbnu.tux.dto;

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
public class ReferenceRoomDTO {

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
    List<RfCommentDTO> comments;
    private Integer likes;
    List<String> likedPeople;
    private Integer dislikes;

    private Boolean isAnonymized;
    private String lecture;
    private String semester;
    private String professor;

    public static ReferenceRoomDTO build(ReferenceRoom data) {
        List<AttachmentDTO> files = data.getAttachments().stream()
                .sorted((c1, c2) -> c1.getOrder().compareTo(c2.getOrder()))
                .map(c -> AttachmentDTO.build(c))
                .toList();

        List<RfCommentDTO> comments = data.getComments().stream()
                .filter(c -> !c.getIsDeleted())
                .sorted((c1, c2) -> c1.getCreatedDate().compareTo(c2.getCreatedDate()))
                .map(c -> RfCommentDTO.build(c))
                .toList();

        List<String> likedPeople = data.getLikes().stream()
                .filter(l -> !l.getDislike() && !l.getUser().isDeleted())
                .map(l -> l.getUser().getNickname())
                .toList();

        Long dislikes = data.getLikes().stream()
                .filter(l -> l.getDislike())
                .count();

        return ReferenceRoomDTO.builder()
                .id(data.getId())
                .category(data.getCategory().name())
                .title(data.getTitle())
                .body(data.getBody())
                .createdDate(data.getCreatedDate())
                .editedDate(data.getEditedDate())
                .view(data.getView())
                .authorId(data.getUser().getId())
                .author((data.getIsAnonymized()) ? "익명" : data.getUser().getNickname())
                .isAnonymized(data.getIsAnonymized())
                .likes(likedPeople.size())
                .likedPeople(likedPeople)
                .dislikes(dislikes.intValue())
                .lecture(data.getLecture())
                .semester(data.getSemester())
                .professor(data.getProfessor())
                .files(files)
                .comments(comments)
                .build();
    }

}
