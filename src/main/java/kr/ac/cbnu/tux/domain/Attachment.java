package kr.ac.cbnu.tux.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 첨부파일 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attachment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty
    private String filename;

    @Column(nullable = false)
    @NotEmpty
    private String path;

    @Column(nullable = false)
    private Boolean isImage;

    @Column(name = "orders")
    private Integer order;     // 순서 : 갤러리 글의 사진 순서


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_id")
    private ReferenceRoom data;

    public void setData(ReferenceRoom data) {
        this.data = data;

        if (data != null && !data.getAttachments().contains(this)) {
            data.getAttachments().add(this);
        }
    }
}
