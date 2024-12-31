package com.ecom.Shopping_Cart.service;

import com.ecom.Shopping_Cart.model.UserDtls;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    public UserDtls saveUser(UserDtls user);

    public UserDtls getUserByEmail(String email);

    List<UserDtls> getAllUsers(String role);

    public Boolean updateAccountStatus(Boolean status, Integer id);

    public void increaseFailedAttempts(UserDtls user);

    public void userAccountLock(UserDtls user);

    public boolean unlockAccountTimeExpired(UserDtls user);

    public void resetAttempt(int userId);

    public UserDtls updateUserProfile(UserDtls user, MultipartFile image);

    public UserDtls updateUser(UserDtls user);

    public UserDtls saveAdmin(UserDtls user);

    public Boolean existsEmail(String email);

    public void updateUserResetToken(String email,String resetToken);


}
