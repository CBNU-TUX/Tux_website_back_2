package kr.ac.cbnu.tux.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kr.ac.cbnu.tux.enums.CommunityPostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CommunityPostType category;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    public void setUser(User user) {
        this.user = user;

        if (user != null && !user.getPosts().contains(this)) {
            user.getPosts().add(this);
        }
    }

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<CmComment> comments = new ArrayList<>();

    public void addComment(CmComment comment) {
        this.comments.add(comment);

        if (comment.getPost() != this) {
            comment.setPost(this);
        }
    }

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<Attachment> attachments = new ArrayList<>();

    public void addAttachment(Attachment file) {
        this.attachments.add(file);

        if (file.getPost() != this) {
            file.setPost(this);
        }
    }

    public void removeAttachment(Attachment file) {
        this.attachments.remove(file);

        if (file.getData() != null) {
            file.setData(null);
        }
    }

}
