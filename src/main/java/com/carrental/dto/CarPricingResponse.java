
package com.carrental.dto;

import java.math.BigDecimal;

public record CarPricingResponse(
    BigDecimal hourlyPrice,
    BigDecimal halfDayPrice,
    BigDecimal dailyPrice,
    BigDecimal weeklyPrice,
    BigDecimal monthlyPrice
) {
}
