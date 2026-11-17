
package com.carrental.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "car_pricing")
public class CarPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "car_id", nullable = false, unique = true)
    private Car car;

    @Column(name = "hourly_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyPrice;

    @Column(name = "daily_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyPrice;

    @Column(name = "weekly_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal weeklyPrice;

    @Column(name = "monthly_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "half_day_price", precision = 10, scale = 2)
    private BigDecimal halfDayPrice;

    public CarPricing() {
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public BigDecimal getHourlyPrice() {
        return hourlyPrice;
    }

    public void setHourlyPrice(BigDecimal hourlyPrice) {
        this.hourlyPrice = hourlyPrice;
    }

    public BigDecimal getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(BigDecimal dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public BigDecimal getWeeklyPrice() {
        return weeklyPrice;
    }

    public void setWeeklyPrice(BigDecimal weeklyPrice) {
        this.weeklyPrice = weeklyPrice;
    }

    public BigDecimal getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(BigDecimal monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

	public BigDecimal getHalfDayPrice() {
		return halfDayPrice;
	}

	public void setHalfDayPrice(BigDecimal halfDayPrice) {
		this.halfDayPrice = halfDayPrice;
	}
    
    
}
