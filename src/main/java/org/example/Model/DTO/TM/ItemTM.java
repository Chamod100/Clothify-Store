package org.example.Model.DTO.TM; // ඔබගේ package එක

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ItemTM {
    // FXML Table Columns (colItemCode -> itemCode) වලට හරියටම ගැලපේ
    private String itemCode;
    private String description;

    // ⚠️ qty වෙනස් කිරීම: 'quantity' සිට 'qty' දක්වා
    private int qty;

    // ⚠️ unitPrice වෙනස් කිරීම: 'sellingPrice' සිට 'unitPrice' දක්වා
    private double unitPrice;
    private String date;        // colDate
    private double discount;    // colDiscount
    private String type;
    private String size;
    private double amount;      // colAmount (ගණනය කළ අගය)

    // Button/Option column එකට අවශ්‍යයි
    private Object option;

    // ⚠️ ඔබට අවශ්‍ය නැති fields ඉවත් කර ඇත (buyingPrice, supplierId)
}