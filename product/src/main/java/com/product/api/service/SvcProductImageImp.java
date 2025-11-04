package com.product.api.service;

import java.nio.file.Files;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.product.api.commons.dto.ApiResponse;
import com.product.api.dto.in.DtoProductImageIn;
import com.product.api.entity.ProductImage;
import com.product.api.repository.RepoProductImage;
import com.product.exception.DBAccessException;

import jakarta.persistence.criteria.Path;

public class SvcProductImageImp implements SvcProductImage {
    @Autowired
    RepoProductImage repo;

    public ResponseEntity<ApiResponse> uploadProductImage(DtoProductImageIn in){
          try{
            if(in.getImage().startsWith("data:image")){
                int commaIndex = in.getImage().indexOf(',');
                if(commaIndex != -1){
                    in.setImage(in.getImage().substring(commaIndex + 1));
                }
            }
            byte[] imageBytes = Base64.getDecoder().decode(in.getImage());
            String fileName = UUID.randomUUID().toString() + ".png";
            String relativePath  = Path.get("img", "product", fileName).toString();
            String uploadDir = 
            Path imagePath = Path.get(uploadDir, "img", "product", fileName);
            Files.createDirectories(imagePath,.getParent());
            Files.write(imagePath, imageBytes);

            ProductImage productImage = new ProductImage();
            productImage.setProductId(in.getProduct_id());
            productImage.setImagePath("img/product/" + fileName);
            repo.save(productImage);
    
            return new ResponseEntity<>(new ApiResponse("Imagen del producto ha sido subida"), HttpStatus.OK);
        } catch(DataAccessException e){
            throw new DBAccessException(e);
        }
    
    }
    

    public ResponseEntity<ApiResponse> deleteProductImage(Integer product_image_id) {
        try{
            repo.disableProductImage(product_image_id);
        return new ResponseEntity<>(new ApiResponse("Imagen del producto ha sido eliminada"), HttpStatus.OK);
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    //falta cadenas a base 64
    public ResponseEntity<ProductImage[]> getProductImages(Integer product_id) {
        ProductImage[] productImgs = repo.findByProductId(product_id);
        return new ResponseEntity<>(productImgs, HttpStatus.OK);
    }
}
