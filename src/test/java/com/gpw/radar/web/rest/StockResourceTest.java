package com.gpw.radar.web.rest;

import com.gpw.radar.Application;
import com.gpw.radar.domain.Stock;
import com.gpw.radar.repository.StockRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.gpw.radar.domain.enumeration.StockTicker;

/**
 * Test class for the StockResource REST controller.
 *
 * @see StockResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class StockResourceTest {


    private static final StockTicker DEFAULT_TICKER = StockTicker.wig20;
    private static final StockTicker UPDATED_TICKER = StockTicker.wig20;
    private static final String DEFAULT_STOCK_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_STOCK_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_STOCK_SHORT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_STOCK_SHORT_NAME = "UPDATED_TEXT";

    @Inject
    private StockRepository stockRepository;

    private MockMvc restStockMockMvc;

    private Stock stock;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StockResource stockResource = new StockResource();
        ReflectionTestUtils.setField(stockResource, "stockRepository", stockRepository);
        this.restStockMockMvc = MockMvcBuilders.standaloneSetup(stockResource).build();
    }

    @Before
    public void initTest() {
        stock = new Stock();
        stock.setticker(DEFAULT_TICKER);
        stock.setStockName(DEFAULT_STOCK_NAME);
        stock.setStockShortName(DEFAULT_STOCK_SHORT_NAME);
    }

    @Test
    @Transactional
    public void createStock() throws Exception {
        int databaseSizeBeforeCreate = stockRepository.findAll().size();

        // Create the Stock
        restStockMockMvc.perform(post("/api/stocks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stock)))
                .andExpect(status().isCreated());

        // Validate the Stock in the database
        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(databaseSizeBeforeCreate + 1);
        Stock testStock = stocks.get(stocks.size() - 1);
        assertThat(testStock.getticker()).isEqualTo(DEFAULT_TICKER);
        assertThat(testStock.getStockName()).isEqualTo(DEFAULT_STOCK_NAME);
        assertThat(testStock.getStockShortName()).isEqualTo(DEFAULT_STOCK_SHORT_NAME);
    }

    @Test
    @Transactional
    public void checktickerIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockRepository.findAll().size();
        // set the field null
        stock.setticker(null);

        // Create the Stock, which fails.
        restStockMockMvc.perform(post("/api/stocks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stock)))
                .andExpect(status().isBadRequest());

        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStocks() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get all the stocks
        restStockMockMvc.perform(get("/api/stocks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(stock.getId().intValue())))
                .andExpect(jsonPath("$.[*].ticker").value(hasItem(DEFAULT_TICKER.toString())))
                .andExpect(jsonPath("$.[*].stockName").value(hasItem(DEFAULT_STOCK_NAME.toString())))
                .andExpect(jsonPath("$.[*].stockShortName").value(hasItem(DEFAULT_STOCK_SHORT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

        // Get the stock
        restStockMockMvc.perform(get("/api/stocks/{id}", stock.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(stock.getId().intValue()))
            .andExpect(jsonPath("$.ticker").value(DEFAULT_TICKER.toString()))
            .andExpect(jsonPath("$.stockName").value(DEFAULT_STOCK_NAME.toString()))
            .andExpect(jsonPath("$.stockShortName").value(DEFAULT_STOCK_SHORT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStock() throws Exception {
        // Get the stock
        restStockMockMvc.perform(get("/api/stocks/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

		int databaseSizeBeforeUpdate = stockRepository.findAll().size();

        // Update the stock
        stock.setticker(UPDATED_TICKER);
        stock.setStockName(UPDATED_STOCK_NAME);
        stock.setStockShortName(UPDATED_STOCK_SHORT_NAME);
        restStockMockMvc.perform(put("/api/stocks")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stock)))
                .andExpect(status().isOk());

        // Validate the Stock in the database
        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(databaseSizeBeforeUpdate);
        Stock testStock = stocks.get(stocks.size() - 1);
        assertThat(testStock.getticker()).isEqualTo(UPDATED_TICKER);
        assertThat(testStock.getStockName()).isEqualTo(UPDATED_STOCK_NAME);
        assertThat(testStock.getStockShortName()).isEqualTo(UPDATED_STOCK_SHORT_NAME);
    }

    @Test
    @Transactional
    public void deleteStock() throws Exception {
        // Initialize the database
        stockRepository.saveAndFlush(stock);

		int databaseSizeBeforeDelete = stockRepository.findAll().size();

        // Get the stock
        restStockMockMvc.perform(delete("/api/stocks/{id}", stock.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Stock> stocks = stockRepository.findAll();
        assertThat(stocks).hasSize(databaseSizeBeforeDelete - 1);
    }
}
