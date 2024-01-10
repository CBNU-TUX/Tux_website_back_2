package kr.ac.cbnu.tux.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 연혁, 연락처 등 정적인 페이지를 저장하기 위한 엔티티

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaticPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotEmpty
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String body;
}
