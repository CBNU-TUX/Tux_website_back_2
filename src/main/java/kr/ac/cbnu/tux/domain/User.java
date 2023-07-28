package kr.ac.cbnu.tux.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.ac.cbnu.tux.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotEmpty @Size(min = 4)
    @Pattern(regexp = "[a-zA-Z\\d_]+", message = "Username Rule : only alphabet + number + _")
    private String username;    // 로그인에 사용되는 아이디

    @Column(nullable = false)
    @NotEmpty
    private String nickname;    // 외부로 공개되는 닉네임 (작성자명)

    @Column(nullable = false)
    @NotEmpty
    private String password;    // 로그인에 사용되는 비밀번호 (암호화되어 저장됨)

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserRole role;      // 회원 권한 등급 (게스트 / 유저 / 매니저 / 어드민)
    /*
    * 게스트 : 가입 직후 권한, 커뮤니티 읽기 가능/자료실 및 갤러리 사용불가 (어드민 권한의 사용자가 유저로 권한 변경 필요)
    * 유저 : 일반 회원, 커뮤니티와 자료실 쓰기읽기 가능/갤러리 읽기 가능
    * 매니저 : 어드민보다 낮은 권한의 관리자, 커뮤니티와 자료실, 갤러리 쓰기읽기삭제 가능
    * 어드민 : 최고 관리자, 매니저 권한 + 회원 권한 변경 가능
    */

    @Column(nullable = false)
    @NotEmpty @Pattern(regexp = ".+@.+", message = "Invalid email address")
    private String email;       // 이메일 주소

    private String department;      // 학과
    private String studentNumber;   // 학번
    private String phoneNumber;     // 전화번호

    @Column(nullable = false)
    private boolean isLocked;       // 잠김 여부 (미사용, 스프링시큐리티 요구사항)
    @Column(nullable = false)
    private boolean isBanned;       // 밴 여부
    @Column(nullable = false)
    private boolean isDeleted;      // 탈퇴여부

    @Column(nullable = false)
    private OffsetDateTime createdDate; // 가입일시
    private OffsetDateTime bannedDate;  // 밴 일시
    private OffsetDateTime deletedDate; // 탈퇴 일시

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.role.name()));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return (!isLocked && !isBanned && !isDeleted);
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return (!isLocked && !isBanned && !isDeleted);
    }


    /* 커뮤니티 글 */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Community> posts = new ArrayList<>();

    public void addPost(Community post) {
        this.posts.add(post);

        if (post.getUser() != this) {
            post.setUser(this);
        }
    }

    /* 커뮤니티 댓글 */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<CmComment> cmComments = new ArrayList<>();

    public void addCmComment(CmComment comment) {
        this.cmComments.add(comment);

        if (comment.getUser() != this) {
            comment.setUser(this);
        }
    }

    /* 자료실 글 */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ReferenceRoom> datas = new ArrayList<>();

    public void addData(ReferenceRoom data) {
        this.datas.add(data);

        if (data.getUser() != this) {
            data.setUser(this);
        }
    }

    /* 자료실 댓글 */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<RfComment> rfComments = new ArrayList<>();

    public void addRfComment(RfComment comment) {
        this.rfComments.add(comment);

        if (comment.getUser() != this) {
            comment.setUser(this);
        }
    }


}
