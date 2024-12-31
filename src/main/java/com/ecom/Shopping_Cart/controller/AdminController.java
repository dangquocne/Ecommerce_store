package com.ecom.Shopping_Cart.controller;

import com.ecom.Shopping_Cart.model.Category;
import com.ecom.Shopping_Cart.model.Product;
import com.ecom.Shopping_Cart.model.ProductOrder;
import com.ecom.Shopping_Cart.model.UserDtls;
import com.ecom.Shopping_Cart.service.*;
import com.ecom.Shopping_Cart.util.CommonUtil;
import com.ecom.Shopping_Cart.util.OrderStatus;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
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

@Controller
@RequestMapping("/admin")
public class AdminController {


    private CategoryService categoryService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommonUtil commonUtil;

    //xac dinh nguoi dung bang name trong navbar khi dang nhap
    @ModelAttribute
    public void getUserDDetails(Principal p, Model model) {

        if(p!=null){
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            model.addAttribute("user", userDtls);
            Integer countCart=  cartService.getCountCart(userDtls.getId());
            model.addAttribute("countCart", countCart);
        }

        List<Category> categories = categoryService.getAllActiveCategory();
        model.addAttribute("categories", categories);
    }

    @GetMapping("/")
    public String index() {
        return "admin/index";
    }


    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model model) {
        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("categories", categories);
        return "admin/add_product";
    }

//    Loat tat ca category len view cho bảng category
    @GetMapping("/category")
    public String category(Model model) {
        model.addAttribute("categorys", categoryService.getAllCategory());
        return "admin/category";
    }


//them moi category
    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
                               HttpSession session) throws IOException {

        //kiem tra file
        String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
        category.setImageName(imageName);

        //kiem tra thong bao khi lu thong tin
        Boolean existCategory = categoryService.existCategory(category.getName());
        if (existCategory) {

            session.setAttribute("errorMsg", "Ten Danh Muc Da Ton Tai");

        } else {

            Category savedCategory = categoryService.saveCategory(category);

            if (ObjectUtils.isEmpty(savedCategory)) {

                session.setAttribute("errorMsg", "Not Save! Internal sever error");

            } else {
                //Luu vao duong dan hinh anh trong O D:/ sau khi nhan luu thong tin
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());

                System.out.println(path);

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                session.setAttribute("sucMsg", "Saved successfully");
            }
        }

        return "redirect:/admin/category";
    }


//xoa category thoe id
    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable("id") int id, HttpSession session) {
        Boolean deleteCategory = categoryService.deleteCategory(id);
        if (deleteCategory) {
            session.setAttribute("sucMsg", "Xoa thanh cong!");
        } else {
            session.setAttribute("errorMsg", "Co loi tren server");
        }

        return "redirect:/admin/category";
    }


//load category theo id de update len bang view edit_category
    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable("id") int id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit_category";
    }


//    Cap nhat category
    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) {

        Category oldCategory = categoryService.getCategoryById(category.getId());
        String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename();

        if (!ObjectUtils.isEmpty(oldCategory)) {

            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(imageName);

        }
        Category updateCategory = categoryService.saveCategory(oldCategory);

        if (!ObjectUtils.isEmpty(updateCategory)) {
            session.setAttribute("sucMsg", "Category update successfully");
        } else {
            session.setAttribute("errorMsg", "Co loi tren server");
        }

        return "redirect:/admin/loadEditCategory/" + category.getId();

    }


//    Them moi san pham vao data
    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,HttpSession session) throws IOException {


        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());

        Product saveProduct = productService.saveProduct(product);

        if (!ObjectUtils.isEmpty(saveProduct)) {
            //Luu vao duong dan hinh anh trong O D:/ sau khi nhan luu thong tin
            File saveFile = new ClassPathResource("static/img").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + image.getOriginalFilename());

//            System.out.println(path);

            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            session.setAttribute("sucMsg", "Product save successfully");

        }else{
            session.setAttribute("errorMsg", "Co loi tren server");
        }

        return "redirect:/admin/loadAddProduct";

    }


//load sản phẩm lên view products
    @GetMapping("/loadProduct")
    public String loadProduct(Model model,@RequestParam(defaultValue = "") String ch ,
                              @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
                              @RequestParam(value = "pageSize", defaultValue = "3")Integer pageSize){

//        List<Product> product = null;
//
//        if(ch!=null && ch.length()>0){
//
//            product = productService.searchProduct(ch);
//        }else{
//            product = productService.getAllProducts();
//        }

        Page<Product> page = null;

        if(ch!=null && ch.length()>0){

            page = productService.searchProductPagination(pageNo,pageSize,ch);
        }else{
            page  = productService.getAllProductsPagination(pageNo,pageSize);
        }


        model.addAttribute("products",page.getContent());


//        model.addAttribute("productsSize",page.size());

        model.addAttribute("pageNo", page.getNumber());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());

        return "admin/products";
    }


    //Xoa san pham
    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable("id") int id, HttpSession session){
        Boolean deleteProduct = productService.deleteProduct(id);
        if(deleteProduct){
            session.setAttribute("sucMsg", "Product delete successfully");
        }else{
            session.setAttribute("errorMsg", "Co loi tren server");
        }

        return "redirect:/admin/loadProduct";
    }


    //load san pham bang id len edit_product

    @GetMapping("/loadEditProduct/{id}")
    public String loadEditProduct(@PathVariable("id") int id, Model model) {
         model.addAttribute("product", productService.getProductById(id));
         model.addAttribute("categories", categoryService.getAllCategory());
        return "admin/edit_product";
    }



    //Update san pham o view edit_product
    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile image, HttpSession session) {

        if(product.getDiscount()<0 || product.getDiscount() >100){
            session.setAttribute("errorMsg", "Invalid Discount");
        }else {

            Product updateProduct = productService.updateProduct(product, image);


            if (!ObjectUtils.isEmpty(updateProduct)) {

                session.setAttribute("sucMsg", "Product update successfully");
            } else {
                session.setAttribute("errorMsg", "Co loi tren server");
            }
        }
        return "redirect:/admin/loadEditProduct/"+product.getId();
    }


     //Load thong tin user len view users
    @GetMapping("/users")
    public String getAllUser(Model model,@RequestParam Integer type) {
        List<UserDtls> users=null;
        if(type==1){
            users = userService.getAllUsers("ROLE_USER");
        }else{
            users = userService.getAllUsers("ROLE_ADMIN");
        }
        model.addAttribute("usersType",type);

        model.addAttribute("users", users);

        return "admin/users";
    }


    //Cap nhat trang thai enable cho account user
    @GetMapping("/updateSts")
    public String updateAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, @RequestParam Integer type, HttpSession session) {
       Boolean f= userService.updateAccountStatus(status, id);
       if(f){
           session.setAttribute("sucMsg", "Account update successfully");
       }else{
           session.setAttribute("errorMsg", "Co loi tren server");
       }

        return "redirect:/admin/users?type="+type;
    }


    //Load tat ca san pham  user da order len view admin/order
    @GetMapping("/order")
    public String getAllOrder(Model model,
                              @RequestParam(value = "pageNo",defaultValue = "0") Integer pageNo,
                              @RequestParam(value = "pageSize",defaultValue = "2") Integer pageSize) {
//        List<ProductOrder> allOrder = orderService.getAllOrders();
//        model.addAttribute("orders", allOrder);
//        model.addAttribute("srch",false);

        Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo,pageSize);

        model.addAttribute("orders", page.getContent());
        model.addAttribute("srch",false);


        model.addAttribute("pageNo", page.getNumber());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());


        return "admin/order";
    }


    //    update status trong don hang khi xu li tien trinh giao hang
    //    va gui mail thong bao qua trinh sau khi order
    @GetMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam int id, @RequestParam int st,HttpSession session){

        OrderStatus[] values = OrderStatus.values();
        String status=null;
        for (OrderStatus orderSt : values){
            if(orderSt.getId()==st){
                status=orderSt.getName();
            }

        }
        ProductOrder updateStatus=orderService.UpdateOrderStatus(id,status);
        try {
            commonUtil.sendEmailForProductOrder(updateStatus,status);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!ObjectUtils.isEmpty(updateStatus)){
            session.setAttribute("sucMsg","Order status updated successfully");
        }else{
            session.setAttribute("errorMsg","Order status update failed");
        }


        return "redirect:/admin/order";
    }


// tim order bang order id va phan trang  tren view admin/order
    @GetMapping("/search-order")
    public String searchOrder(@RequestParam String orderId,Model model,HttpSession session,
                              @RequestParam(value = "pageNo",defaultValue = "0") Integer pageNo,
                              @RequestParam(value = "pageSize",defaultValue = "2") Integer pageSize) {

      if(orderId!=null && orderId.length()>0) {
          ProductOrder order = orderService.getOrderByOrderId(orderId);

          if (ObjectUtils.isEmpty(order)) {
              session.setAttribute("errorMsg", "Incorrect orderId");
              model.addAttribute("orderDts", null);

          } else {
              model.addAttribute("orderDts", order);
          }
          model.addAttribute("srch", true);

      }else{

//          List<ProductOrder> allOrder = orderService.getAllOrders();
//          model.addAttribute("orders", allOrder);
//          model.addAttribute("srch",false);

          Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo,pageSize);
          model.addAttribute("orders", page.getContent());
          model.addAttribute("srch",false);

          model.addAttribute("pageNo", page.getNumber());
          model.addAttribute("pageSize", pageSize);
          model.addAttribute("totalElements", page.getTotalElements());
          model.addAttribute("totalPages", page.getTotalPages());
          model.addAttribute("isFirst", page.isFirst());
          model.addAttribute("isLast", page.isLast());

      }
        return "/admin/order";
    }

//Load admin len admin/add_admin
    @GetMapping("/add-admin")
    public String getAdmin(){
        return "admin/add_admin";
    }


    // thong tin dang ki admin moi
    @PostMapping("/save-admin")
    public String saveAdmin(@ModelAttribute UserDtls userDtls, @RequestParam("img") MultipartFile file , HttpSession session) throws IOException {

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        userDtls.setProfileImage(imageName);
        UserDtls saveUserDtls = userService.saveAdmin(userDtls);

        if(!ObjectUtils.isEmpty(saveUserDtls)){
            if(!file.isEmpty()){
                //Luu vao duong dan hinh anh trong O D:/ sau khi nhan luu thong tin
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator + file.getOriginalFilename());

//                System.out.println(path);

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("sucMsg", "Register save successfully");
        }else{
            session.setAttribute("errorMsg", "Register save failed");
        }

        return "redirect:/admin/add-admin";
    }


    //load thong tin admin len view admin/profile
    @GetMapping("/profile")
    public String getProfile(){

        return "admin/profile";
    }


//    update profile cho admin
    @PostMapping("/update-profile")
    public String updateUserProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img,HttpSession session){
        UserDtls updateProfile = userService.updateUserProfile(user, img);
        if(ObjectUtils.isEmpty(updateProfile)){
            session.setAttribute("errorMsg","Profile update failed");

        }else{
            session.setAttribute("sucMsg","Profile updated successfully");
        }


        return "redirect:/admin/profile";
    }


//    Thay doi mat khau cho admin
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword, @RequestParam String currentPassword,Principal p,HttpSession session){
        UserDtls loggedInUserDetails = commonUtil.getLoggedInUserDetails(p);


        boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());
        if(matches){
            String encode = passwordEncoder.encode(newPassword);
            loggedInUserDetails.setPassword(encode);
            UserDtls userUpdate = userService.updateUser(loggedInUserDetails);
            if(ObjectUtils.isEmpty(userUpdate)){
                session.setAttribute("errorMsg","PassWord not update || Error in server");
            }else{
                session.setAttribute("sucMsg","Password updated successfully");
            }

        }else{
            session.setAttribute("errorMsg","Current PassWord incorrect");
        }

        return "redirect:/admin/profile";
    }


}


