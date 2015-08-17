package com.gpw.radar.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import com.gpw.radar.Application;
import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.repository.StockDetailsRepository;


/**
 * Test class for the StockDetailsResource REST controller.
 *
 * @see StockDetailsResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class StockDetailsResourceTest {


    private static final LocalDate DEFAULT_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_DATE = new LocalDate();

    private static final BigDecimal DEFAULT_OPEN_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_OPEN_PRICE = new BigDecimal(1);

    private static final BigDecimal DEFAULT_MAX_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_MAX_PRICE = new BigDecimal(1);

    private static final BigDecimal DEFAULT_MIN_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_MIN_PRICE = new BigDecimal(1);

    private static final BigDecimal DEFAULT_CLOSE_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_CLOSE_PRICE = new BigDecimal(1);

    private static final Long DEFAULT_VOLUME = 0L;
    private static final Long UPDATED_VOLUME = 1L;

    @Inject
    private StockDetailsRepository stockDetailsRepository;

    private MockMvc restStockDetailsMockMvc;

    private StockDetails stockDetails;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StockDetailsResource stockDetailsResource = new StockDetailsResource();
        ReflectionTestUtils.setField(stockDetailsResource, "stockDetailsRepository", stockDetailsRepository);
        this.restStockDetailsMockMvc = MockMvcBuilders.standaloneSetup(stockDetailsResource).build();
    }

    @Before
    public void initTest() {
        stockDetails = new StockDetails();
        stockDetails.setDate(DEFAULT_DATE);
        stockDetails.setOpenPrice(DEFAULT_OPEN_PRICE);
        stockDetails.setMaxPrice(DEFAULT_MAX_PRICE);
        stockDetails.setMinPrice(DEFAULT_MIN_PRICE);
        stockDetails.setClosePrice(DEFAULT_CLOSE_PRICE);
        stockDetails.setVolume(DEFAULT_VOLUME);
    }

    @Test
    @Transactional
    public void createStockDetails() throws Exception {
        int databaseSizeBeforeCreate = stockDetailsRepository.findAll().size();

        // Create the StockDetails
        restStockDetailsMockMvc.perform(post("/api/stockDetailss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stockDetails)))
                .andExpect(status().isCreated());

        // Validate the StockDetails in the database
        List<StockDetails> stockDetailss = stockDetailsRepository.findAll();
        assertThat(stockDetailss).hasSize(databaseSizeBeforeCreate + 1);
        StockDetails testStockDetails = stockDetailss.get(stockDetailss.size() - 1);
        assertThat(testStockDetails.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testStockDetails.getOpenPrice()).isEqualTo(DEFAULT_OPEN_PRICE);
        assertThat(testStockDetails.getMaxPrice()).isEqualTo(DEFAULT_MAX_PRICE);
        assertThat(testStockDetails.getMinPrice()).isEqualTo(DEFAULT_MIN_PRICE);
        assertThat(testStockDetails.getClosePrice()).isEqualTo(DEFAULT_CLOSE_PRICE);
        assertThat(testStockDetails.getVolume()).isEqualTo(DEFAULT_VOLUME);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockDetailsRepository.findAll().size();
        // set the field null
        stockDetails.setDate(null);

        // Create the StockDetails, which fails.
        restStockDetailsMockMvc.perform(post("/api/stockDetailss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stockDetails)))
                .andExpect(status().isBadRequest());

        List<StockDetails> stockDetailss = stockDetailsRepository.findAll();
        assertThat(stockDetailss).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOpenPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockDetailsRepository.findAll().size();
        // set the field null
        stockDetails.setOpenPrice(null);

        // Create the StockDetails, which fails.
        restStockDetailsMockMvc.perform(post("/api/stockDetailss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stockDetails)))
                .andExpect(status().isBadRequest());

        List<StockDetails> stockDetailss = stockDetailsRepository.findAll();
        assertThat(stockDetailss).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMaxPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockDetailsRepository.findAll().size();
        // set the field null
        stockDetails.setMaxPrice(null);

        // Create the StockDetails, which fails.
        restStockDetailsMockMvc.perform(post("/api/stockDetailss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stockDetails)))
                .andExpect(status().isBadRequest());

        List<StockDetails> stockDetailss = stockDetailsRepository.findAll();
        assertThat(stockDetailss).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMinPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockDetailsRepository.findAll().size();
        // set the field null
        stockDetails.setMinPrice(null);

        // Create the StockDetails, which fails.
        restStockDetailsMockMvc.perform(post("/api/stockDetailss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stockDetails)))
                .andExpect(status().isBadRequest());

        List<StockDetails> stockDetailss = stockDetailsRepository.findAll();
        assertThat(stockDetailss).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkClosePriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockDetailsRepository.findAll().size();
        // set the field null
        stockDetails.setClosePrice(null);

        // Create the StockDetails, which fails.
        restStockDetailsMockMvc.perform(post("/api/stockDetailss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stockDetails)))
                .andExpect(status().isBadRequest());

        List<StockDetails> stockDetailss = stockDetailsRepository.findAll();
        assertThat(stockDetailss).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkVolumeIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockDetailsRepository.findAll().size();
        // set the field null
        stockDetails.setVolume(null);

        // Create the StockDetails, which fails.
        restStockDetailsMockMvc.perform(post("/api/stockDetailss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stockDetails)))
                .andExpect(status().isBadRequest());

        List<StockDetails> stockDetailss = stockDetailsRepository.findAll();
        assertThat(stockDetailss).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStockDetailss() throws Exception {
        // Initialize the database
        stockDetailsRepository.saveAndFlush(stockDetails);

        // Get all the stockDetailss
        restStockDetailsMockMvc.perform(get("/api/stockDetailss"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(stockDetails.getId().intValue())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].openPrice").value(hasItem(DEFAULT_OPEN_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].maxPrice").value(hasItem(DEFAULT_MAX_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].minPrice").value(hasItem(DEFAULT_MIN_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].closePrice").value(hasItem(DEFAULT_CLOSE_PRICE.intValue())))
                .andExpect(jsonPath("$.[*].volume").value(hasItem(DEFAULT_VOLUME.intValue())));
    }

    @Test
    @Transactional
    public void getStockDetails() throws Exception {
        // Initialize the database
        stockDetailsRepository.saveAndFlush(stockDetails);

        // Get the stockDetails
        restStockDetailsMockMvc.perform(get("/api/stockDetailss/{id}", stockDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(stockDetails.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.openPrice").value(DEFAULT_OPEN_PRICE.intValue()))
            .andExpect(jsonPath("$.maxPrice").value(DEFAULT_MAX_PRICE.intValue()))
            .andExpect(jsonPath("$.minPrice").value(DEFAULT_MIN_PRICE.intValue()))
            .andExpect(jsonPath("$.closePrice").value(DEFAULT_CLOSE_PRICE.intValue()))
            .andExpect(jsonPath("$.volume").value(DEFAULT_VOLUME.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingStockDetails() throws Exception {
        // Get the stockDetails
        restStockDetailsMockMvc.perform(get("/api/stockDetailss/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStockDetails() throws Exception {
        // Initialize the database
        stockDetailsRepository.saveAndFlush(stockDetails);

		int databaseSizeBeforeUpdate = stockDetailsRepository.findAll().size();

        // Update the stockDetails
        stockDetails.setDate(UPDATED_DATE);
        stockDetails.setOpenPrice(UPDATED_OPEN_PRICE);
        stockDetails.setMaxPrice(UPDATED_MAX_PRICE);
        stockDetails.setMinPrice(UPDATED_MIN_PRICE);
        stockDetails.setClosePrice(UPDATED_CLOSE_PRICE);
        stockDetails.setVolume(UPDATED_VOLUME);
        restStockDetailsMockMvc.perform(put("/api/stockDetailss")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stockDetails)))
                .andExpect(status().isOk());

        // Validate the StockDetails in the database
        List<StockDetails> stockDetailss = stockDetailsRepository.findAll();
        assertThat(stockDetailss).hasSize(databaseSizeBeforeUpdate);
        StockDetails testStockDetails = stockDetailss.get(stockDetailss.size() - 1);
        assertThat(testStockDetails.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testStockDetails.getOpenPrice()).isEqualTo(UPDATED_OPEN_PRICE);
        assertThat(testStockDetails.getMaxPrice()).isEqualTo(UPDATED_MAX_PRICE);
        assertThat(testStockDetails.getMinPrice()).isEqualTo(UPDATED_MIN_PRICE);
        assertThat(testStockDetails.getClosePrice()).isEqualTo(UPDATED_CLOSE_PRICE);
        assertThat(testStockDetails.getVolume()).isEqualTo(UPDATED_VOLUME);
    }

    @Test
    @Transactional
    public void deleteStockDetails() throws Exception {
        // Initialize the database
        stockDetailsRepository.saveAndFlush(stockDetails);

		int databaseSizeBeforeDelete = stockDetailsRepository.findAll().size();

        // Get the stockDetails
        restStockDetailsMockMvc.perform(delete("/api/stockDetailss/{id}", stockDetails.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<StockDetails> stockDetailss = stockDetailsRepository.findAll();
        assertThat(stockDetailss).hasSize(databaseSizeBeforeDelete - 1);
    }
}
