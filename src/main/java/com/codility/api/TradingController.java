package com.codility.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is the entry point for all trading operations.
 */

@RestController
@RequestMapping("/orders")
public class TradingController {

    private TradingService tradingService;

    public TradingController(TradingService tradingService) {
        this.tradingService = tradingService;
    }

    /**
     * {
     *     "portfolioId": "portfolio-id-1",
     *     "isin": "US67066G1040",
     *     "side": "BUY",
     *     "quantity": 50.00
     * }
     */
    @PostMapping
    public OrderResponseDto placeOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return tradingService.placeOrder(orderRequestDto);
    }

    @GetMapping("/{id}")
    public OrderResponseDto retrieveOrder(@PathVariable Long id) {
        return tradingService.getOrder(id);
    }

    @PutMapping("/{id}")
    public OrderResponseDto cancelOrder(@PathVariable Long id) {
        return tradingService.cancelOrder(id);
    }

    @GetMapping("/init")
    public void init() {
        tradingService.init();
    }

}
