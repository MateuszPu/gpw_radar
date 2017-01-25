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

    private final DtoMapper<Stock, StockWithStockIndicatorsDTO> stockWithIndicatorsMapper;
    private final DtoMapper<StockIndicators, StockIndicatorsWithStocksDTO> stockIndicatorsMapper;
    private final DtoMapper<Stock, StockDTO> stockMapper;

    public StockMapperDtoFacade(DtoMapper<Stock, StockWithStockIndicatorsDTO> stockWithIndicatorsMapper,
                                DtoMapper<StockIndicators, StockIndicatorsWithStocksDTO> stockIndicatorsMapper,
                                DtoMapper<Stock, StockDTO> stockMapper) {
        this.stockWithIndicatorsMapper = stockWithIndicatorsMapper;
        this.stockIndicatorsMapper = stockIndicatorsMapper;
        this.stockMapper = stockMapper;
    }

    public List<StockWithStockIndicatorsDTO> mapToStockWithIndicatorsDto(Collection<Stock> stocks) {
        return stockWithIndicatorsMapper.mapToDto(stocks, StockWithStockIndicatorsDTO.class);
    }

    public List<StockIndicatorsWithStocksDTO> mapToStockIndiactorsDto(Collection<StockIndicators> stockIndicators) {
        return stockIndicatorsMapper.mapToDto(stockIndicators, StockIndicatorsWithStocksDTO.class);
    }

    public List<StockDTO> mapToStocksDto(Collection<Stock> stocks) {
        return stockMapper.mapToDto(stocks, StockDTO.class);
    }
}
