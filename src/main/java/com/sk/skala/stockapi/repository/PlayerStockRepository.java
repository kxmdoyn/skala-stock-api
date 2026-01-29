package com.sk.skala.stockapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.PlayerStock;
import com.sk.skala.stockapi.data.table.Stock;

public interface PlayerStockRepository extends JpaRepository<PlayerStock, Long> {

    // 플레이어가 보유한 주식 목록 조회
    List<PlayerStock> findByPlayer_PlayerId(String playerId);

    // 특정 플레이어가 특정 주식을 보유하고 있는지 확인(있으면 1건)
    Optional<PlayerStock> findByPlayerAndStock(Player player, Stock stock);
}
