package com.gpw.radar.service.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gpw.radar.domain.stock.StockIndicators;
import com.gpw.radar.jackson.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JsonViewConverter {

    private final Logger logger = LoggerFactory.getLogger(JsonViewConverter.class);

    public String convertStockIndicatorsToJsonView(List<StockIndicators> stockIndicators) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        String jsonResult = "";
        try {
            jsonResult = mapper.writerWithView(View.StockIndicators.StockAndPercentReturn.class).writeValueAsString(stockIndicators);
        } catch (JsonProcessingException e) {
            logger.error("Error occurs: " + e.getMessage());
        }
        return jsonResult;
    }
}
