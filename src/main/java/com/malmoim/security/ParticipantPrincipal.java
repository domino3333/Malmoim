package com.malmoim.security;


import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Builder
public class ParticipantPrincipal implements UserDetails {

    private Long roomNo;
    private Long participantNo;
    private String nickname;
    private List<GrantedAuthority> authority;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authority;
    }

    @Override
    public @Nullable String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.participantNo.toString();
    }
}
