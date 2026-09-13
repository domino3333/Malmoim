package com.malmoim.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    //이외 다른 것들은 default 값으로 들어가므로 넣을 필요 x
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이메일 형식으로 입력해주세요")
    @Size(max = 50, message = "이메일은 50자 이하여야 합니다")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;

    @NotBlank(message = "이름을 입력해주세요")
    @Size(max = 20, message = "이름은 20자 이하여야 합니다")
    private String name;
}
