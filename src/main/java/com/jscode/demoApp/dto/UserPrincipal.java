package com.jscode.demoApp.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserPrincipal implements UserDetails {
    Long id;
    String email;
    String password;
    Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal of(Long id, String email, String password){
        return new UserPrincipal(id, email, password, Set.of());
    }

    public static UserPrincipal fromDto(MemberDto memberDto){
        return UserPrincipal.of(memberDto.getId(), memberDto.getEmail(), memberDto.getPassword());
    }

    public MemberDto toDto(){
        return MemberDto.of(this.getId(), this.getUsername(), null, null);
    }

    public Long getId(){return this.id;};

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {return this.authorities;}
    @Override public String getPassword() { return this.password; }
    @Override public String getUsername() { return this.email; }


    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true;}
}
