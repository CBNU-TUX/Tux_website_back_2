package kr.ac.cbnu.tux.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

// ReferenceRoom Comment
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RfComment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotEmpty
    private String body;

    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    private OffsetDateTime createdDate;

    private OffsetDateTime deletedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;

        if (user != null && !user.getRfComments().contains(this)) {
            user.getRfComments().add(this);
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_id")
    private ReferenceRoom data;

    public void setData(ReferenceRoom data) {
        this.data = data;

        if (data != null && !data.getComments().contains(this)) {
            data.getComments().add(this);
        }
    }

}
