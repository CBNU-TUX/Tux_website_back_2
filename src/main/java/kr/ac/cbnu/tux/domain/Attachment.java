package kr.ac.cbnu.tux.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long downloadCount;


    // 커뮤니티와 맵핑시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Community post;

    public void setPost(Community post) {
        this.post = post;

        if (post != null && !post.getAttachments().contains(this)) {
            post.getAttachments().add(this);
        }
    }

    // 자료실과 맵핑시
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
