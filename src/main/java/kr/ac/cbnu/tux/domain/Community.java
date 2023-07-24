package kr.ac.cbnu.tux.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import kr.ac.cbnu.tux.enums.CommunityPostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

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
    private User user;

    public void setUser(User user) {
        this.user = user;

        if (user != null && !user.getPosts().contains(this)) {
            user.getPosts().add(this);
        }
    }
}
