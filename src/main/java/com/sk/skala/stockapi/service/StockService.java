package com.sk.skala.stockapi.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sk.skala.stockapi.config.Error;
import com.sk.skala.stockapi.data.common.PagedList;
import com.sk.skala.stockapi.data.common.Response;
import com.sk.skala.stockapi.data.table.Stock;
import com.sk.skala.stockapi.exception.ParameterException;
import com.sk.skala.stockapi.exception.ResponseException;
import com.sk.skala.stockapi.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    private Response ok(Object body) {
        Response response = new Response();
        response.setResult(Response.SUCCESS);
        response.setBody(body);
        return response;
    }

    // 전체 주식 목록 조회
    public Response getAllStocks(int offset, int count) {
        Pageable pageable = PageRequest.of(offset, count);
        Page<Stock> page = stockRepository.findAll(pageable);

        PagedList pagedList = new PagedList();
        pagedList.setTotal(page.getTotalElements());
        pagedList.setCount(page.getNumberOfElements());
        pagedList.setOffset(offset);
        pagedList.setList(page.getContent());

        return ok(pagedList);
    }

    // 개별 주식 상세 조회
    public Response getStockById(Long id) {
        Optional<Stock> stockOpt = stockRepository.findById(id);
        if (stockOpt.isEmpty()) {
            throw new ResponseException(Error.DATA_NOT_FOUND);
        }
        return ok(stockOpt.get());
    }

// 주식 등록
public Response createStock(Stock stock) {
    if (stock == null || isBlank(stock.getStockName())
            || stock.getStockPrice() == null || stock.getStockPrice() <= 0) {
        throw new ParameterException("stockName", "stockPrice");
    }

    if (stockRepository.findByStockName(stock.getStockName()).isPresent()) {
        throw new ResponseException(Error.DATA_DUPLICATED);
    }

    // ✅ ID는 세팅하지 않습니다. (DB가 자동 생성)
    Stock saved = stockRepository.save(stock);

    return ok(saved);
}
    // 주식 수정
    public Response updateStock(Stock stock) {
        if (stock == null || stock.getId() == null || isBlank(stock.getStockName())
                || stock.getStockPrice() == null || stock.getStockPrice() <= 0) {
            throw new ParameterException("stockName", "stockPrice");
        }

        if (stockRepository.findById(stock.getId()).isEmpty()) {
            throw new ResponseException(Error.DATA_NOT_FOUND);
        }

        Stock saved = stockRepository.save(stock);
        return ok(saved);
    }

    // 주식 삭제
    public Response deleteStock(Stock stock) {
        if (stock == null || stock.getId() == null) {
            throw new ParameterException("id");
        }

        Stock target = stockRepository.findById(stock.getId())
                .orElseThrow(() -> new ResponseException(Error.DATA_NOT_FOUND));

        stockRepository.delete(target);
        return ok(true);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
