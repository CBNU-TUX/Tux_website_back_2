package kr.ac.cbnu.tux.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "likes")
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Boolean dislike; // false: 추천, true: 비추천

    @ManyToOne(fetch = FetchType.EAGER)
    private Community post;

    public void setPost(Community post) {
        this.post = post;

        if (post != null && !post.getLikes().contains(this)) {
            post.getLikes().add(this);
        }
    }

    @ManyToOne(fetch = FetchType.EAGER)
    private ReferenceRoom data;

    public void setData(ReferenceRoom data) {
        this.data = data;

        if (data != null && !data.getLikes().contains(this)) {
            data.getLikes().add(this);
        }
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
