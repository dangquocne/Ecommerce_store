package com.ecom.Shopping_Cart.service.impl;

import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.repository.UserRepository;
import com.ecom.Shopping_Cart.service.UserService;
import com.ecom.Shopping_Cart.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDtls saveUser(UserDtls user) {
        user.setRole("ROLE_USER");
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        String endcodePassword= passwordEncoder.encode(user.getPassword());
        user.setPassword(endcodePassword);

        UserDtls savedUser = userRepository.save(user);

        return savedUser;
    }

    @Override
    public UserDtls getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    @Override
    public List<UserDtls> getAllUsers(String role) {

        return  userRepository.findByRole(role);
    }

    @Override
    public Boolean updateAccountStatus(Boolean status, Integer id) {
        Optional<UserDtls> findByUser = userRepository.findById(id);

        if(findByUser.isPresent()) {

            UserDtls user = findByUser.get();
            user.setIsEnable(status);
            userRepository.save(user);
            return true;
        }

        return false;
    }


    //phương thức tăng đếm số lần khi user đăng nhập sai quá 3 lân
    @Override
    public void increaseFailedAttempts(UserDtls user) {
        int attemps = user.getFailedAttempt()+1;
        user.setFailedAttempt(attemps);
        userRepository.save(user);

    }

    //phương thức thiết lập tài khoản = 0 khi sai mk 3 lần và thơời gian đăng nhập
    @Override
    public void userAccountLock(UserDtls user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);

    }

    //Kieemr tra thoi gian cho phep dang nhap nhu ban dau sau khi dang nhap sai mk qua 3 lan
    @Override
    public boolean unlockAccountTimeExpired(UserDtls user) {

        long lockTime = user.getLockTime().getTime();
        long unLockTime = lockTime+ AppConstant.UNLOCK_DURATION_TIME;

        long currentTime = System.currentTimeMillis();

        if(unLockTime<currentTime) {
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            user.setLockTime(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void resetAttempt(int userId) {


    }

    @Override
    public UserDtls updateUserProfile(UserDtls user, MultipartFile image) {
        UserDtls updateUser = userRepository.findById(user.getId()).get();
        if(!image.isEmpty()){
            updateUser.setProfileImage(image.getOriginalFilename());
        }

        if(!ObjectUtils.isEmpty(updateUser)){
            updateUser.setName(user.getName());
            updateUser.setMobileNumber(user.getMobileNumber());
            updateUser.setAddress(user.getAddress());
            updateUser.setCity(user.getCity());
            updateUser.setState(user.getState());
            updateUser.setPincode(user.getPincode());
            updateUser=userRepository.save(updateUser);
        }
        try {


            if (!image.isEmpty()) {
                //Luu vao duong dan hinh anh trong O D:/ sau khi nhan luu thong tin
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + image.getOriginalFilename());

//                System.out.println(path);

                Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return updateUser;
    }

    @Override
    public UserDtls updateUser(UserDtls user) {
        return userRepository.save(user);
    }

    @Override
    public UserDtls saveAdmin(UserDtls user) {
        user.setRole("ROLE_ADMIN");
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        String endcodePassword= passwordEncoder.encode(user.getPassword());
        user.setPassword(endcodePassword);

        UserDtls savedUser = userRepository.save(user);

        return savedUser;
    }


    @Override
    public Boolean existsEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        UserDtls user = userRepository.findByEmail(email);
        user.setResetToken(resetToken);
        userRepository.save(user);
    }
}
