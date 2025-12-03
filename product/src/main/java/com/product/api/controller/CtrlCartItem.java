package com.product.api.controller;

import com.product.api.commons.dto.ApiResponse;
import com.product.api.dto.DtoCartItemOut;
import com.product.api.dto.in.DtoCartItemIn;
import com.product.api.service.SvcCartItem;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart-item")
public class CtrlCartItem {

    private final SvcCartItem svcCartItem;

    public CtrlCartItem(SvcCartItem svcCartItem) {
        this.svcCartItem = svcCartItem;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addItem(@RequestBody DtoCartItemIn in) {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ApiResponse resp = svcCartItem.addItem(userId, in);
        return ResponseEntity.ok(resp);
    }

    @GetMapping
    public ResponseEntity<List<DtoCartItemOut>> listItems() {
        Long userId = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(svcCartItem.listItems(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteItem(@PathVariable Long id) {
        return ResponseEntity.ok(svcCartItem.deleteItem(id));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> clearCart() {
        return ResponseEntity.ok(svcCartItem.clearCart());
    }
}
