package com.gpw.radar.service.stock;

import com.gpw.radar.domain.stock.Stock;
import com.gpw.radar.domain.stock.StockIndicators;
import com.gpw.radar.service.mapper.DtoMapper;
import com.gpw.radar.web.rest.dto.stock.StockDTO;
import com.gpw.radar.web.rest.dto.stock.StockIndicatorsWithStocksDTO;
import com.gpw.radar.web.rest.dto.stock.StockWithStockIndicatorsDTO;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class StockMapperDtoFacade {

    private final DtoMapper<Stock, StockWithStockIndicatorsDTO> stockWithIndicatorsMapper = new DtoMapper<>(StockWithStockIndicatorsDTO.class);
    private final DtoMapper<StockIndicators, StockIndicatorsWithStocksDTO> stockIndicatorsMapper = new DtoMapper<>(StockIndicatorsWithStocksDTO.class);
    private final DtoMapper<Stock, StockDTO> stockMapper = new DtoMapper<>(StockDTO.class);

    public List<StockWithStockIndicatorsDTO> mapToStockWithIndicatorsDto(Collection<Stock> stocks) {
        return stockWithIndicatorsMapper.mapToDto(stocks);
    }

    public List<StockIndicatorsWithStocksDTO> mapToStockIndiactorsDto(Collection<StockIndicators> stockIndicators) {
        return stockIndicatorsMapper.mapToDto(stockIndicators);
    }

    public List<StockDTO> mapToStocksDto(Collection<Stock> stocks) {
        return stockMapper.mapToDto(stocks);
    }
}
