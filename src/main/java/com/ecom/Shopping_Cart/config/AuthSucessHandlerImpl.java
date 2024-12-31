package com.ecom.Shopping_Cart.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Service
public class AuthSucessHandlerImpl implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
    Authentication authentication) throws IOException, ServletException {

//        Lấy danh sách quyền (authorities) của người dùng
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();//Trả về danh sách các quyền mà người dùng được gán (ví dụ: ROLE_ADMIN, ROLE_USER).

        //Chuyển danh sách quyền thành Set<String> để dễ kiểm tra.
        //Ví dụ: Nếu người dùng có quyền ROLE_ADMIN và ROLE_USER,
        // thì roles sẽ là ["ROLE_ADMIN", "ROLE_USER"].

        Set<String> roles = AuthorityUtils.authorityListToSet(authorities);

        if(roles.contains("ROLE_ADMIN")){
            response.sendRedirect("/admin/");
        }else{
            response.sendRedirect("/");
        }

    }
}
