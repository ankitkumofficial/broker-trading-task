package com.codility.api;

import com.codility.MarketDataService;
import com.codility.OrderSide;
import com.codility.OrderStatus;
import com.codility.repository.BuyingPowerEntity;
import com.codility.repository.BuyingPowerRepository;
import com.codility.repository.InventoryEntity;
import com.codility.repository.InventoryEntityId;
import com.codility.repository.InventoryRepository;
import com.codility.repository.OrderEntity;
import com.codility.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TradingService {

    private final MarketDataService marketDataService;
    private final BuyingPowerRepository buyingPowerRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;

    public TradingService(MarketDataService marketDataService, BuyingPowerRepository buyingPowerRepository,
                          InventoryRepository inventoryRepository, OrderRepository orderRepository) {
        this.marketDataService = marketDataService;
        this.buyingPowerRepository = buyingPowerRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
    }

    public OrderResponseDto placeOrder(OrderRequestDto requestDto) {
        OrderSide orderSide = OrderSide.valueOf(requestDto.getSide()); // to-do
        BigDecimal marketPrice = marketDataService.getPrice(requestDto.getIsin()); // to-do
        Optional<BuyingPowerEntity> buyingPowerEntityOptional = buyingPowerRepository.findById(requestDto.getPortfolioId());
        if (buyingPowerEntityOptional.isEmpty()) {
            throw new OrderException("Invalid portfolio id");
        }
        BuyingPowerEntity buyingPowerEntity = buyingPowerEntityOptional.get();
        InventoryEntityId inventoryEntityId = new InventoryEntityId(requestDto.getPortfolioId(), requestDto.getIsin());
        Optional<InventoryEntity> inventoryEntityOptional = inventoryRepository.findById(inventoryEntityId);
        InventoryEntity inventoryEntity = null;
        if (inventoryEntityOptional.isPresent()) {
            inventoryEntity = inventoryEntityOptional.get();
        }
        OrderResponseDto responseDto = null;
        switch (orderSide) {
            case BUY -> {
                BigDecimal requiredAmount = marketPrice.multiply(requestDto.getQuantity());
                if (requiredAmount.compareTo(buyingPowerEntity.getAmount()) > 0) {
                    throw new OrderException("Insufficient buying power");
                }
                // place order
                OrderEntity orderEntity = new OrderEntity(requestDto.getPortfolioId(), requestDto.getIsin(),
                        OrderStatus.CREATED, orderSide, requestDto.getQuantity(), marketPrice);
                orderEntity = orderRepository.save(orderEntity);
                // update inventory
                BigDecimal updatedQuantity = requestDto.getQuantity();
                if (inventoryEntity != null) {
                    updatedQuantity = updatedQuantity.add(inventoryEntity.getQuantity());
                }
                InventoryEntity updatedInventoryEntity = new InventoryEntity(requestDto.getPortfolioId(), requestDto.getIsin(),
                        updatedQuantity);
                inventoryRepository.save(updatedInventoryEntity);
                // update buying power
                BuyingPowerEntity updatedBuyingPowerEntity = new BuyingPowerEntity(buyingPowerEntity.getPortfolioId(),
                        buyingPowerEntity.getAmount().subtract(requiredAmount));
                buyingPowerRepository.save(updatedBuyingPowerEntity);
                responseDto = transform(orderEntity);
            }
            case SELL -> {
                if (inventoryEntity == null || inventoryEntity.getQuantity().compareTo(requestDto.getQuantity()) < 0) {
                    throw new OrderException("Insufficient inventory");
                }
                BigDecimal creditedAmount = requestDto.getQuantity().multiply(marketPrice);
                // place order
                OrderEntity orderEntity = new OrderEntity(requestDto.getPortfolioId(), requestDto.getIsin(),
                        OrderStatus.CREATED, orderSide, requestDto.getQuantity(), marketPrice);
                orderEntity = orderRepository.save(orderEntity);
                // update inventory
                InventoryEntity updatedInventory = new InventoryEntity(requestDto.getPortfolioId(), requestDto.getIsin(),
                        inventoryEntity.getQuantity().subtract(requestDto.getQuantity()));
                inventoryRepository.save(updatedInventory);
                // update buying power
                BuyingPowerEntity updatedBuyingPowerEntity = new BuyingPowerEntity(buyingPowerEntity.getPortfolioId(),
                        buyingPowerEntity.getAmount().add(creditedAmount));
                buyingPowerRepository.save(updatedBuyingPowerEntity);
                responseDto = transform(orderEntity);
            }
        }
        return responseDto;
    }

    public OrderResponseDto getOrder(Long id) {
        Optional<OrderEntity> orderEntityOptional = orderRepository.findById(id);
        if (orderEntityOptional.isEmpty()) {
            throw new OrderNotFoundException("Order not found");
        }
        OrderEntity orderEntity = orderEntityOptional.get();
        return transform(orderEntity);
    }

    public OrderResponseDto cancelOrder(Long id) {
        Optional<OrderEntity> orderEntityOptional = orderRepository.findById(id);
        if (orderEntityOptional.isEmpty()) {
            throw new OrderNotFoundException("Order not found");
        }
        OrderEntity orderEntity = orderEntityOptional.get();
        if (orderEntity.getStatus() != OrderStatus.CREATED) {
            throw new OrderException("Order cannot be cancelled");
        }
        Optional<BuyingPowerEntity> buyingPowerEntityOptional = buyingPowerRepository.findById(orderEntity.getPortfolioId());
        if (buyingPowerEntityOptional.isEmpty()) {
            throw new OrderException("Invalid portfolio id");
        }
        BuyingPowerEntity buyingPowerEntity = buyingPowerEntityOptional.get();
        InventoryEntityId inventoryEntityId = new InventoryEntityId(orderEntity.getPortfolioId(), orderEntity.getIsin());
        Optional<InventoryEntity> inventoryEntityOptional = inventoryRepository.findById(inventoryEntityId);
        if (inventoryEntityOptional.isEmpty()) {
            throw new OrderException("Inventory not found");
        }
        InventoryEntity inventoryEntity = inventoryEntityOptional.get();
        switch (orderEntity.getSide()) {
            case BUY -> {
                // update inventory
                InventoryEntity updatedInventory = new InventoryEntity(orderEntity.getPortfolioId(), orderEntity.getIsin(),
                        inventoryEntity.getQuantity().subtract(orderEntity.getQuantity()));
                inventoryRepository.save(updatedInventory);
                // update buying power
                BuyingPowerEntity updatedBuyingPowerEntity = new BuyingPowerEntity(orderEntity.getPortfolioId(),
                        buyingPowerEntity.getAmount().add(orderEntity.getQuantity().multiply(orderEntity.getPrice())));
                buyingPowerRepository.save(updatedBuyingPowerEntity);
            }
            case SELL -> {
                // update inventory
                InventoryEntity updatedInventory = new InventoryEntity(orderEntity.getPortfolioId(), orderEntity.getIsin(),
                        inventoryEntity.getQuantity().add(orderEntity.getQuantity()));
                inventoryRepository.save(updatedInventory);
                // update buying power
                BuyingPowerEntity updatedBuyingPowerEntity = new BuyingPowerEntity(buyingPowerEntity.getPortfolioId(),
                        buyingPowerEntity.getAmount().subtract(orderEntity.getQuantity().multiply(orderEntity.getPrice())));
                buyingPowerRepository.save(updatedBuyingPowerEntity);
            }
        }
        orderEntity.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(orderEntity);
        return transform(orderEntity);
    }

    private static OrderResponseDto transform(OrderEntity orderEntity) {
        return new OrderResponseDto().setId(orderEntity.getId())
                .setPortfolioId(orderEntity.getPortfolioId())
                .setIsin(orderEntity.getIsin())
                .setSide(orderEntity.getSide())
                .setQuantity(orderEntity.getQuantity())
                .setPrice(orderEntity.getPrice())
                .setStatus(orderEntity.getStatus());
    }

    public void init() {
        BuyingPowerEntity buyingPowerEntity = new BuyingPowerEntity("portfolio-id-1", new BigDecimal(5000));
        buyingPowerRepository.save(buyingPowerEntity);
    }
}
