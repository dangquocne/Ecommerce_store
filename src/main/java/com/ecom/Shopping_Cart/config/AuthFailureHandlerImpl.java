package com.ecom.Shopping_Cart.config;

import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.repository.UserRepository;
import com.ecom.Shopping_Cart.service.UserService;
import com.ecom.Shopping_Cart.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
    AuthenticationException exception) throws IOException, ServletException {


        String email = request.getParameter("username");

        UserDtls userDtls = userRepository.findByEmail(email);

        //kiem tra trang thai enable true hay false
        if(userDtls.getIsEnable()){

            //kiem tra account co bi khoa khong
            if(userDtls.getAccountNonLocked()){

                //kiem tra so lan dang nhap sai qua 3 lan
                if(userDtls.getFailedAttempt()< AppConstant.ATTEMPT_TIME){
                    //neu nho hon 3 thi tang len cho du 3
                    userService.increaseFailedAttempts(userDtls);

                }else{
                    // neu qua 3 lan thi khoa tai khoan
                    userService.userAccountLock(userDtls);
                    exception = new LockedException("your account is Locked ! || failed attempt 3");
                }

            }else{
                //neu tai khoan bi khoa thi kiem tra thoi gian tai khoan mo lai khi dang nhap lai
                if(userService.unlockAccountTimeExpired(userDtls)){
                    exception = new LockedException("your account is unlocked || Please try to login");
                }else{

                    exception = new LockedException("your account is locked || Please try after sometimes");
                }
            }


        }else{
            exception = new LockedException("your account is inActive");
        }

        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);

    }
}
