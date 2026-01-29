package com.sk.skala.stockapi.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.dto.PlayerSession;
import com.sk.skala.stockapi.data.dto.StockOrder;
import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.service.PlayerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    // =========================
    // 전체 플레이어 목록 조회
    // GET /api/players/list?offset=0&count=10
    // =========================
    @GetMapping("/list")
    public Response getAllPlayers(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int count) {
        return playerService.getAllPlayers(offset, count);
    }

    // =========================
    // 단일 플레이어 조회 + 보유 주식
    // GET /api/players/{playerId}
    // =========================
    @GetMapping("/{playerId}")
    public Response getPlayerById(@PathVariable String playerId) {
        return playerService.getPlayerById(playerId);
    }

    // =========================
    // 플레이어 등록
    // POST /api/players
    // =========================
    @PostMapping("")
    public Response createPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player);
    }

    // =========================
    // 플레이어 로그인
    // POST /api/players/login
    // =========================
    @PostMapping("/login")
    public Response loginPlayer(@RequestBody PlayerSession playerSession) {
        return playerService.loginPlayer(playerSession);
    }

    // =========================
    // 플레이어 정보 수정
    // PUT /api/players
    // =========================
    @PutMapping("")
    public Response updatePlayer(@RequestBody Player player) {
        return playerService.updatePlayer(player);
    }

    // =========================
    // 플레이어 삭제
    // DELETE /api/players
    // =========================
    @DeleteMapping("")
    public Response deletePlayer(@RequestBody Player player) {
        return playerService.deletePlayer(player);
    }

    // =========================
    // 주식 매수
    // POST /api/players/buy
    // =========================
    @PostMapping("/buy")
    public Response buyPlayerStock(@Valid @RequestBody StockOrder order) {
        return playerService.buyPlayerStock(order);
    }

    // =========================
    // 주식 매도
    // POST /api/players/sell
    // =========================
    @PostMapping("/sell")
    public Response sellPlayerStock(@Valid @RequestBody StockOrder order) {
        return playerService.sellPlayerStock(order);
    }
// =========================
// @Valid 테스트용 (세션 없이 검증만 확인)
// POST /api/players/test/valid
// =========================
@PostMapping("/test/valid")
public Response testValid(@Valid @RequestBody StockOrder order) {
    Response response = new Response();
    response.setBody(order);
    return response;
}

}
