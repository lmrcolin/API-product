package com.product.api.service;

import com.product.api.commons.dto.ApiResponse;
import com.product.api.dto.DtoCartItemOut;
import com.product.api.dto.in.DtoCartItemIn;

import java.util.List;

public interface SvcCartItem {
    ApiResponse addItem(Long userId, DtoCartItemIn in);

    List<DtoCartItemOut> listItems(Long userId);

    ApiResponse deleteItem(Long id);

    ApiResponse clearCart();
}
