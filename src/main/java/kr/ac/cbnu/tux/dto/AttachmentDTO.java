package kr.ac.cbnu.tux.dto;

import kr.ac.cbnu.tux.domain.Attachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AttachmentDTO {

    private String filename;
    private String path;
    private Boolean isImage;
    private Integer order;

    public static AttachmentDTO build(Attachment file) {
        return AttachmentDTO.builder()
                .filename(file.getFilename())
                .path(file.getPath())
                .isImage(file.getIsImage())
                .order(file.getOrder())
                .build();
    }

}
