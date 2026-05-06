package ru.v_and_a.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private String uuid;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private Long userId;
    private BigDecimal totalAmount;
    @ElementCollection
    @CollectionTable(
            name = "order_items",
            joinColumns = @JoinColumn(name = "order_uuid")
    )
    private List<Item> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}