package com.sk.skala.stockapi.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.dto.PlayerSession;
import com.sk.skala.stockapi.data.dto.PlayerStockDto;
import com.sk.skala.stockapi.data.dto.PlayerStockListDto;
import com.sk.skala.stockapi.data.dto.StockOrder;
import com.sk.skala.stockapi.data.table.Player;
import com.sk.skala.stockapi.data.table.PlayerStock;
import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.PlayerRepository;
import com.sk.skala.stockapi.repository.PlayerStockRepository;
import com.sk.skala.stockapi.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private final StockRepository stockRepository;
    private final PlayerRepository playerRepository;
    private final PlayerStockRepository playerStockRepository;
    private final SessionHandler sessionHandler;

    // ===== 공통 성공 응답 =====
    private Response ok(Object body) {
        Response response = new Response();
        response.setResult(Response.SUCCESS);
        response.setBody(body);
        return response;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // =========================
    // 1) 전체 플레이어 목록 조회
    // =========================
    public Response getAllPlayers(int offset, int count) {
        Pageable pageable = PageRequest.of(offset, count);
        Page<Player> page = playerRepository.findAll(pageable);

        PagedList pagedList = new PagedList();
        pagedList.setTotal(page.getTotalElements());
        pagedList.setCount(page.getNumberOfElements());
        pagedList.setOffset(offset);
        pagedList.setList(page.getContent());

        return ok(pagedList);
    }

    // ==========================================
    // 2) 단일 플레이어 + 보유 주식 목록 조회
    // ==========================================
    @Transactional(readOnly = true)
    public Response getPlayerById(String playerId) {
        if (isBlank(playerId)) {
            throw new ParameterException("playerId");
        }

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "Player not found"));

        List<PlayerStock> owned = playerStockRepository.findByPlayer_PlayerId(playerId);

        List<PlayerStockDto> stocks = owned.stream()
                .map(ps -> PlayerStockDto.builder()
                        .stockId(ps.getStock().getId())
                        .stockName(ps.getStock().getStockName())
                        .stockPrice(ps.getStock().getStockPrice())
                        .quantity(ps.getQuantity())
                        .build())
                .collect(Collectors.toList());

        PlayerStockListDto dto = PlayerStockListDto.builder()
                .playerId(player.getPlayerId())
                .playerMoney(player.getPlayerMoney())
                .stocks(stocks)
                .build();

        return ok(dto);
    }

    // =========================
    // 3) 플레이어 생성
    // =========================
    public Response createPlayer(Player playerSession) {
        if (playerSession == null || isBlank(playerSession.getPlayerId()) || isBlank(playerSession.getPlayerPassword())) {
            throw new ParameterException("playerId", "playerPassword");
        }

        if (playerRepository.existsById(playerSession.getPlayerId())) {
            throw new ResponseException(Error.DATA_DUPLICATED);
        }

        Player saved = playerRepository.save(playerSession);
        return ok(saved);
    }

    // =========================
    // 4) 플레이어 로그인
    // =========================
    public Response loginPlayer(PlayerSession playerSession) {
        if (playerSession == null || isBlank(playerSession.getPlayerId()) || isBlank(playerSession.getPlayerPassword())) {
            throw new ParameterException("playerId", "playerPassword");
        }

        Player player = playerRepository.findById(playerSession.getPlayerId())
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

        if (!playerSession.getPlayerPassword().equals(player.getPlayerPassword())) {
            throw new ResponseException(Error.NOT_AUTHENTICATED);
        }

        // 로그인 성공 → JWT 쿠키 저장(패스워드 숨김 처리 포함)
        PlayerSession stored = sessionHandler.storeAccessToken(playerSession);

        return ok(stored);
    }

    // =========================
    // 5) 플레이어 정보 업데이트
    // =========================
    public Response updatePlayer(Player player) {
        if (player == null || isBlank(player.getPlayerId())) {
            throw new ParameterException("playerId");
        }

        Player target = playerRepository.findById(player.getPlayerId())
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

        // 실습 설명: playerId와 playerMoney 업데이트 중심
        if (player.getPlayerMoney() != null) {
            target.setPlayerMoney(player.getPlayerMoney());
        }
        // 비번 변경을 허용할지 여부는 과제 정책에 따라(원하면 열어둠)
        if (!isBlank(player.getPlayerPassword())) {
            target.setPlayerPassword(player.getPlayerPassword());
        }

        Player saved = playerRepository.save(target);
        return ok(saved);
    }

    // =========================
    // 6) 플레이어 삭제
    // =========================
    public Response deletePlayer(Player player) {
        if (player == null || isBlank(player.getPlayerId())) {
            throw new ParameterException("playerId");
        }

        Player target = playerRepository.findById(player.getPlayerId())
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

        playerRepository.delete(target);
        return ok(true);
    }

// =========================
// 7) 주식 매수
// =========================
@Transactional
public Response buyPlayerStock(StockOrder order) {

    // 로그인 세션에서 playerId 가져오기
    String playerId = sessionHandler.getPlayerId();
    if (isBlank(playerId)) {
        throw new ResponseException(Error.SESSION_NOT_FOUND);
    }

    // 입력값 검증 (Wrapper 타입: null 체크 필수)
    if (order == null
            || order.getStockId() == null || order.getStockId() <= 0
            || order.getStockQuantity() == null || order.getStockQuantity() <= 0) {
        throw new ParameterException("stockId", "stockQuantity");
    }

    Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "Player not found"));

    Stock stock = stockRepository.findById(order.getStockId())
            .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "Stock not found"));

    // 총 매수금액 계산
    double cost = stock.getStockPrice() * order.getStockQuantity();

    // 잔액 검증
    if (player.getPlayerMoney() == null || player.getPlayerMoney() < cost) {
        throw new ResponseException(Error.INSUFFICIENT_FUNDS);
    }

    // 보유 주식 있으면 수량 증가, 없으면 새로 생성
    PlayerStock ps = playerStockRepository.findByPlayerAndStock(player, stock)
            .orElseGet(() -> new PlayerStock(player, stock, 0));

    int currentQty = (ps.getQuantity() == null) ? 0 : ps.getQuantity();
    ps.setQuantity(currentQty + order.getStockQuantity());

    // 돈 차감
    player.setPlayerMoney(player.getPlayerMoney() - cost);

    // 저장
    playerRepository.save(player);
    playerStockRepository.save(ps);

    return ok(true);
}

// =========================
// 8) 주식 매도
// =========================
@Transactional
public Response sellPlayerStock(StockOrder order) {

    String playerId = sessionHandler.getPlayerId();
    if (isBlank(playerId)) {
        throw new ResponseException(Error.SESSION_NOT_FOUND);
    }

    // 입력값 검증 (Wrapper 타입: null 체크 필수)
    if (order == null
            || order.getStockId() == null || order.getStockId() <= 0
            || order.getStockQuantity() == null || order.getStockQuantity() <= 0) {
        throw new ParameterException("stockId", "stockQuantity");
    }

    Player player = playerRepository.findById(playerId)
            .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "Player not found"));

    Stock stock = stockRepository.findById(order.getStockId())
            .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "Stock not found"));

    PlayerStock ps = playerStockRepository.findByPlayerAndStock(player, stock)
            .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND, "PlayerStock not found"));

    int currentQty = (ps.getQuantity() == null) ? 0 : ps.getQuantity();
    if (currentQty < order.getStockQuantity()) {
        throw new ResponseException(Error.INSUFFICIENT_QUANTITY);
    }

    // 매도금액 계산 및 돈 증가
    double gain = stock.getStockPrice() * order.getStockQuantity();
    double currentMoney = (player.getPlayerMoney() == null) ? 0.0 : player.getPlayerMoney();
    player.setPlayerMoney(currentMoney + gain);

    // 수량 감소
    ps.setQuantity(currentQty - order.getStockQuantity());

    // 수량 0이면 삭제(선택), 아니면 저장
    if (ps.getQuantity() == 0) {
        playerStockRepository.delete(ps);
    } else {
        playerStockRepository.save(ps);
    }

    // 플레이어 잔액 저장
    playerRepository.save(player);

    return ok(true);
}
} 