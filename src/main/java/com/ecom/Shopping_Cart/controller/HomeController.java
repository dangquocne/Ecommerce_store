package com.ecom.Shopping_Cart.controller;

import com.ecom.Shopping_Cart.model.Category;
import com.ecom.Shopping_Cart.model.Product;
import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.service.CartService;
import com.ecom.Shopping_Cart.service.CategoryService;
import com.ecom.Shopping_Cart.service.ProductService;
import com.ecom.Shopping_Cart.service.UserService;
import com.ecom.Shopping_Cart.util.CommonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Controller
public class HomeController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;


    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CommonUtil commonUtil;


    //xac dinh nguoi dung va hien thi name trong navbar khi dang nhap
    @ModelAttribute
    public void getUserDDetails(Principal p, Model model) {

        if(p!=null){
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            model.addAttribute("user", userDtls);
            Integer countCart=  cartService.getCountCart(userDtls.getId());
            model.addAttribute("countCart", countCart);
        }

        //dung de load category tren phan navbar
        List<Category> categories = categoryService.getAllActiveCategory();
        model.addAttribute("categories", categories);

    }


    @GetMapping("/")
    public String index(Model model) {
        List<Category> categories = categoryService.getAllActiveCategory().stream().limit(6).toList();
        List<Product> products = productService.getAllIsActiveProduct("").stream().limit(8).toList();

        model.addAttribute("categories", categories);
        model.addAttribute("products", products);

        return "index";
    }

    @GetMapping("/signin")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }


    //Load product on the product and pagination
    @GetMapping("/products")
    public String products(Model model, @RequestParam(value ="category", defaultValue = "") String category,
                    @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
                    @RequestParam(value = "pageSize", defaultValue = "4")Integer pageSize,@RequestParam(defaultValue = "") String ch){
//        System.out.println("Category: " + category);
        List<Category> categories = categoryService.getAllActiveCategory();
        model.addAttribute("categories",categories);
        model.addAttribute("paramValue",category);


//        List<Product> products =productService.getAllIsActiveProduct(category);
//        model.addAttribute("products",products);
        Page<Product> page= null;

        if(StringUtils.isEmpty(ch)){
            page = productService.getAllActiveProductPagination(pageNo, pageSize, category);

        }else {
            page = productService.searchActiveProductPagination(pageNo, pageSize, category, ch);
        }

            List<Product> products = page.getContent();//lấy nội dung sản phẩm

            model.addAttribute("products", products);
            model.addAttribute("productsSize", products.size());

            model.addAttribute("pageNo", page.getNumber());
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalElements", page.getTotalElements());
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("isFirst", page.isFirst());
            model.addAttribute("isLast", page.isLast());



        return "product";
    }



    //Load thong tin trong detail view_product
    @GetMapping("/product/{id}")
    public String viewProduct(@PathVariable("id") int id, Model model){
        Product product = productService.getProductById(id);
        model.addAttribute("product",product);
        return "view_product";
    }





    //Uodate thong tin dang ki
    @PostMapping("/saveRegister")
    public String saveRegister(@ModelAttribute UserDtls userDtls, @RequestParam("img") MultipartFile file , HttpSession session) throws IOException {

         Boolean existsEmail = userService.existsEmail(userDtls.getEmail());
         if(existsEmail){
             session.setAttribute("errorMsg", "Email already exists");
         }else {


             String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
             userDtls.setProfileImage(imageName);
             UserDtls saveUserDtls = userService.saveUser(userDtls);


             if (!ObjectUtils.isEmpty(saveUserDtls)) {
                 if (!file.isEmpty()) {
                     //Luu vao duong dan hinh anh trong O D:/ sau khi nhan luu thong tin
                     File saveFile = new ClassPathResource("static/img").getFile();

                     Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + file.getOriginalFilename());

//                System.out.println(path);

                     Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                 }
                 session.setAttribute("sucMsg", "Register save successfully");
             } else {
                 session.setAttribute("errorMsg", "Register save failed");
             }
         }

        return "redirect:/register";
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch,Model model){

        List<Product> searchProduct = productService.searchProduct(ch);
        model.addAttribute("products",searchProduct);

        return "product";
    }

    //quen mat khau
    @GetMapping("/forgot-password")
    public String showForgotPass(){
        return "forgot_password";
    }


    //Quen mat khau va gui email user
    @PostMapping("/forgot-password")
    public String processForgotPassWord(@RequestParam String email, HttpSession session, HttpServletRequest request){
        UserDtls userByEmail = userService.getUserByEmail(email);
        if(ObjectUtils.isEmpty(userByEmail)){
            session.setAttribute("errorMsg", "Invalid email");
        }else {
            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email, resetToken);

            // Generate URL :
            // http://localhost:8080/reset-password?token=sfgdbgfswegfbdgfewgvsrg
            String url = CommonUtil.generateUrl(request);


            Boolean send = commonUtil.sendMail();
              if (send){
                  session.setAttribute("sucMsg", "Please check your email..Password Reset link sent");
              }else{
                  session.setAttribute("errorMsg", "Somethong wrong on server ! Email not send");
              }
        }


        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPass(){
        return "reset-password";
    }


}
