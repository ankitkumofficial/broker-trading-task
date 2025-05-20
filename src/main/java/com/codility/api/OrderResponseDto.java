package com.codility.api;

import com.codility.OrderSide;
import com.codility.OrderStatus;

import java.math.BigDecimal;

public class OrderResponseDto {

    private Long id;
    private String portfolioId;
    private String isin;
    private OrderSide side;
    private BigDecimal quantity;
    private BigDecimal price;
    private OrderStatus status;

    public Long getId() {
        return id;
    }

    public OrderResponseDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public OrderResponseDto setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
        return this;
    }

    public String getIsin() {
        return isin;
    }

    public OrderResponseDto setIsin(String isin) {
        this.isin = isin;
        return this;
    }

    public OrderSide getSide() {
        return side;
    }

    public OrderResponseDto setSide(OrderSide side) {
        this.side = side;
        return this;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public OrderResponseDto setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public OrderResponseDto setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public OrderResponseDto setStatus(OrderStatus status) {
        this.status = status;
        return this;
    }
}
