package org.example.Model.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Entity
@Table(name = "order_details")
public class OrderDetails {
    private String orderId;
    private String itemCode;
    private int qty;
    private double unitPrice;
    private double totalProfit;
    private double discount;
}
