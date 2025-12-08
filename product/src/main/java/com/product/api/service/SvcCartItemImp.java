package com.product.api.service;

import com.product.api.commons.dto.ApiResponse;
import com.product.api.dto.DtoCartItemOut;
import com.product.api.dto.in.DtoCartItemIn;
import com.product.api.entity.CartItem;
import com.product.api.entity.Product;
import com.product.exception.ApiException;
import com.product.api.repository.RepoCartItem;
import com.product.api.repository.RepoProduct;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SvcCartItemImp implements SvcCartItem {

    private final RepoCartItem repoCartItem;
    private final RepoProduct repoProduct;

    public SvcCartItemImp(RepoCartItem repoCartItem, RepoProduct repoProduct) {
        this.repoCartItem = repoCartItem;
        this.repoProduct = repoProduct;
    }

    @Override
    @Transactional
    public ApiResponse addItem(Long userId, DtoCartItemIn in) {
        if (in == null || in.getProductId() == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Se requiere el productId.");
        }
        if (in.getQuantity() <= 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0.");
        }

        Product product = repoProduct.findById(in.getProductId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Producto no encontrado."));

        int requestedQuantity = in.getQuantity();
        CartItem item = repoCartItem
                .findByUserIdAndProductId(userId, product.getProduct_id())
                .orElse(null);

        int currentQuantity = item != null ? item.getQuantity() : 0;
        int resultingQuantity = currentQuantity + requestedQuantity;
        Integer stock = product.getStock();
        if (stock == null || resultingQuantity > stock) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Stock insuficiente para el producto solicitado.");
        }

        if (item == null) {
            item = new CartItem();
            item.setUserId(userId);
            item.setProduct(product);
        }

        String gtinToSet = in.getGtin();
        if (gtinToSet == null || gtinToSet.isBlank()) {
            gtinToSet = item.getGtin();
        }
        if (gtinToSet == null || gtinToSet.isBlank()) {
            gtinToSet = product.getGtin();
        }
        item.setGtin(gtinToSet);

        Integer statusToSet = in.getStatus();
        if (statusToSet == null) {
            statusToSet = item.getStatus();
        }
        if (statusToSet == null) {
            statusToSet = 1;
        }
        item.setStatus(statusToSet);

        item.setQuantity(resultingQuantity);
        repoCartItem.save(item);
        String message = currentQuantity > 0 ? "Cantidad actualizada en el carrito." : "Item añadido al carrito.";
        return new ApiResponse(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DtoCartItemOut> listItems(Long userId) {
        return repoCartItem.findAllByUserId(userId).stream().map(ci -> {
            DtoCartItemOut out = new DtoCartItemOut();
            out.setId(ci.getId());
            out.setProductId(ci.getProduct().getProduct_id());
            out.setProductName(ci.getProduct().getProduct());
            out.setProductPrice(ci.getProduct().getPrice());
            out.setQuantity(ci.getQuantity());
            return out;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApiResponse deleteItem(Long userId, Long id) {
        if (id == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Se requiere el id.");
        }
        CartItem item = repoCartItem.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Item no encontrado en el carrito."));
        repoCartItem.delete(item);
        return new ApiResponse("Item eliminado del carrito.");
    }

    @Override
    @Transactional
    public ApiResponse clearCart(Long userId) {
        repoCartItem.deleteAllByUserId(userId);
        return new ApiResponse("Carrito vacio.");
    }
}
