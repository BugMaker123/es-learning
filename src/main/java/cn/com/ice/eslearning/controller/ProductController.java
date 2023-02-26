package cn.com.ice.eslearning.controller;

import cn.com.ice.eslearning.entity.Product;
import cn.com.ice.eslearning.service.ProductService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("addIndex")
    public void addIndex(@RequestBody Product product) throws Exception {
        String s = productService.indexProduct(product);
        System.out.println(s);
        System.out.println("add index over");
    }


    @PostMapping("searchData")
    public void searchData(@RequestParam("id") String id) throws Exception {
        Product res = productService.getProductById(id);
        System.out.println(res.toString());
        System.out.println("query data over");
    }
}
