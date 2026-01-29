package com.sk.skala.stockapi.data.dto;

public class PlayerStockDto {

    private Long stockId;
    private String stockName;
    private Double stockPrice;
    private Integer quantity;

    // 기본 생성자
    public PlayerStockDto() {
    }

    // Getter / Setter
    public Long getStockId() {
        return stockId;
    }

    public void setStockId(Long stockId) {
        this.stockId = stockId;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public Double getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(Double stockPrice) {
        this.stockPrice = stockPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // ===== Builder 패턴 =====
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PlayerStockDto dto = new PlayerStockDto();

        public Builder stockId(Long stockId) {
            dto.stockId = stockId;
            return this;
        }

        public Builder stockName(String stockName) {
            dto.stockName = stockName;
            return this;
        }

        public Builder stockPrice(Double stockPrice) {
            dto.stockPrice = stockPrice;
            return this;
        }

        public Builder quantity(Integer quantity) {
            dto.quantity = quantity;
            return this;
        }

        public PlayerStockDto build() {
            return dto;
        }
    }
}
