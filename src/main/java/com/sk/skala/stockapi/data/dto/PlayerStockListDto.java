package com.sk.skala.stockapi.data.dto;

import java.util.List;

public class PlayerStockListDto {

    private String playerId;
    private Double playerMoney;
    private List<PlayerStockDto> stocks;

    // 기본 생성자
    public PlayerStockListDto() {
    }

    // Getter / Setter
    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public Double getPlayerMoney() {
        return playerMoney;
    }

    public void setPlayerMoney(Double playerMoney) {
        this.playerMoney = playerMoney;
    }

    public List<PlayerStockDto> getStocks() {
        return stocks;
    }

    public void setStocks(List<PlayerStockDto> stocks) {
        this.stocks = stocks;
    }

    // ===== Builder 패턴 =====
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PlayerStockListDto dto = new PlayerStockListDto();

        public Builder playerId(String playerId) {
            dto.playerId = playerId;
            return this;
        }

        public Builder playerMoney(Double playerMoney) {
            dto.playerMoney = playerMoney;
            return this;
        }

        public Builder stocks(List<PlayerStockDto> stocks) {
            dto.stocks = stocks;
            return this;
        }

        public PlayerStockListDto build() {
            return dto;
        }
    }
}
