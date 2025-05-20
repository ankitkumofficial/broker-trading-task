package com.codility.api;

import com.codility.OrderSide;

import java.math.BigDecimal;

public class OrderRequestDto {
    private String portfolioId;
    private String isin;
    private String side;
    private BigDecimal quantity;

    public String getPortfolioId() {
        return portfolioId;
    }

    public OrderRequestDto setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
        return this;
    }

    public String getIsin() {
        return isin;
    }

    public OrderRequestDto setIsin(String isin) {
        this.isin = isin;
        return this;
    }

    public String getSide() {
        return side;
    }

    public OrderRequestDto setSide(String side) {
        this.side = side;
        return this;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public OrderRequestDto setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
        return this;
    }
}
