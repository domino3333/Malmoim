package com.malmoim.dto.auth;

import lombok.Data;

@Data
public class SignUpRequest {

    //이외 다른 것들은 default 값으로 들어가므로 넣을 필요 x
    private String email;
    private String password;
    private String name;
}
