package com.sk.skala.stockapi.data.table;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Player {

    @Id
    private String playerId;

    private String playerPassword;
    private Double playerMoney;

    // JPA 기본 생성자
    protected Player() {
    }

    // 플레이어 ID + 초기 투자금 생성자
    public Player(String playerId, Double playerMoney) {
        this.playerId = playerId;
        this.playerMoney = playerMoney;
    }

    // Getter / Setter
    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerPassword() {
        return playerPassword;
    }

    public void setPlayerPassword(String playerPassword) {
        this.playerPassword = playerPassword;
    }

    public Double getPlayerMoney() {
        return playerMoney;
    }

    public void setPlayerMoney(Double playerMoney) {
        this.playerMoney = playerMoney;
    }
}
