package kr.ac.cbnu.tux.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import kr.ac.cbnu.tux.enums.ReferenceRoomPostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

// 자료실
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceRoom {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private ReferenceRoomPostType category;

    @Column(nullable = false)
    @NotEmpty
    private String title;

    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    @NotEmpty
    private String body;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    private OffsetDateTime createdDate;

    private OffsetDateTime editedDate;

    private OffsetDateTime deletedDate;

    @Column(nullable = false)
    private Long view;
    
    /* 자료실 특수 정보 */
    @Column(nullable = false)
    private Boolean isAnonymized;   // 익명 여부

    private String lecture;         // 강의 이름
    private String semester;        // 학기
    private String professor;       // 교수
    

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;

        if (user != null && !user.getDatas().contains(this)) {
            user.getDatas().add(this);
        }
    }

    @OneToMany(mappedBy = "data", fetch = FetchType.EAGER)
    private List<RfComment> comments = new ArrayList<>();

    public void addComment(RfComment comment) {
        this.comments.add(comment);

        if (comment.getData() != this) {
            comment.setData(this);
        }
    }

    @OneToMany(mappedBy = "data", fetch = FetchType.EAGER)
    private List<Attachment> attachments = new ArrayList<>();

    public void addAttachment(Attachment file) {
        this.attachments.add(file);

        if (file.getData() != this) {
            file.setData(this);
        }
    }

}
