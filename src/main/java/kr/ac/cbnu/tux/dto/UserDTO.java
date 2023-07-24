package kr.ac.cbnu.tux.dto;

import kr.ac.cbnu.tux.domain.User;
import kr.ac.cbnu.tux.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String username;
    private String nickname;
    private UserRole role;
    private String email;
    private String department;
    private String studentNumber;
    private String phoneNumber;
    private boolean isDeleted;
    private OffsetDateTime createdDate;
    private OffsetDateTime deletedDate;

    public static UserDTO build(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .email(user.getEmail())
                .department(user.getDepartment())
                .studentNumber(user.getStudentNumber())
                .phoneNumber(user.getPhoneNumber())
                .isDeleted(user.isDeleted())
                .createdDate(user.getCreatedDate())
                .deletedDate(user.getDeletedDate())
                .build();
    }

}
