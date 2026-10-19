package com.manish.shop.order;

import java.math.BigDecimal;

public record Order(String id,
                    BigDecimal subtotal,
                    BigDecimal discount,
                    BigDecimal tax,
                    BigDecimal total,
                    OrderStatus status) {
}
