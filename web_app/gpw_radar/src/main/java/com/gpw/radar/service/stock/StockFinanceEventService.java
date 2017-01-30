package com.gpw.radar.service.stock;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.stock.StockFinanceEvent;
import com.gpw.radar.repository.stock.StockFinanceEventRepository;
import com.gpw.radar.service.UserService;
import com.gpw.radar.service.mapper.DtoMapper;
import com.gpw.radar.web.rest.dto.stock.StockWithStockFinanceEventDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockFinanceEventService {

    private final StockFinanceEventRepository stockFinanceEventRepository;
    private final UserService userService;
    private final DtoMapper<StockFinanceEvent, StockWithStockFinanceEventDTO> mapper = new DtoMapper<>(StockWithStockFinanceEventDTO.class);

    @Autowired
    public StockFinanceEventService(StockFinanceEventRepository stockFinanceEventRepository, UserService userService) {
        this.stockFinanceEventRepository = stockFinanceEventRepository;
        this.userService = userService;
    }

    public ResponseEntity<List<StockWithStockFinanceEventDTO>> getAllStockFinanceEvent() {
        List<StockFinanceEvent> list = stockFinanceEventRepository.getAllFetchStock();
        List<StockWithStockFinanceEventDTO> dto = mapper.mapToDto(list);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    public ResponseEntity<List<StockWithStockFinanceEventDTO>> getStocksFinanceEventFollowedByUser() {
        User user = userService.getUserWithAuthorities();
        List<StockFinanceEvent> stockFinanceEventsFollowedByUser = stockFinanceEventRepository.getFollowedStockFinanceEvent(user.getId());
        List<StockWithStockFinanceEventDTO> dto = mapper.mapToDto(stockFinanceEventsFollowedByUser);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
