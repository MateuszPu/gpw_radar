package com.gpw.radar.service.stock;

import com.gpw.radar.domain.User;
import com.gpw.radar.domain.stock.StockFinanceEvent;
import com.gpw.radar.repository.stock.StockFinanceEventRepository;
import com.gpw.radar.service.UserService;
import com.gpw.radar.web.rest.dto.stock.StockWithStockFinanceEventDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.reflect.Type;
import java.util.List;

@Service
public class StockFinanceEventService {

    @Inject
    private StockFinanceEventRepository stockFinanceEventRepository;

    @Inject
    private UserService userService;

    public ResponseEntity<List<StockWithStockFinanceEventDTO>> getAllStockFinanceEvent() {
        List<StockFinanceEvent> list = stockFinanceEventRepository.getAllFetchStock();
        List<StockWithStockFinanceEventDTO> dto = getStockWithStockFinanceEventDTOs(list);
        return new ResponseEntity<List<StockWithStockFinanceEventDTO>>(dto, HttpStatus.OK);
    }

    public ResponseEntity<List<StockWithStockFinanceEventDTO>> getStocksFinanceEventFollowedByUser() {
        User user = userService.getUserWithAuthorities();
        List<StockFinanceEvent> stockFinanceEventsFollowedByUser = stockFinanceEventRepository.getFollowedStockFinanceEvent(user.getId());
        List<StockWithStockFinanceEventDTO> dto = getStockWithStockFinanceEventDTOs(stockFinanceEventsFollowedByUser);
        return new ResponseEntity<List<StockWithStockFinanceEventDTO>>(dto, HttpStatus.OK);
    }

    private List<StockWithStockFinanceEventDTO> getStockWithStockFinanceEventDTOs(List<StockFinanceEvent> stockFinanceEventsFollowedByUser) {
        ModelMapper modelMapper = new ModelMapper();
        Type dtoType = new TypeToken<List<StockWithStockFinanceEventDTO>>() {
        }.getType();
        return modelMapper.map(stockFinanceEventsFollowedByUser, dtoType);
    }
}
