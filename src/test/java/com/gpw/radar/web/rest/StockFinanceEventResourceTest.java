package com.gpw.radar.web.rest;

import com.gpw.radar.Application;
import com.gpw.radar.domain.StockFinanceEvent;
import com.gpw.radar.repository.StockFinanceEventRepository;

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
import org.joda.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the StockFinanceEventResource REST controller.
 *
 * @see StockFinanceEventResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class StockFinanceEventResourceTest {


    private static final LocalDate DEFAULT_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_DATE = new LocalDate();
    private static final String DEFAULT_MESSAGE = "SAMPLE_TEXT";
    private static final String UPDATED_MESSAGE = "UPDATED_TEXT";

    @Inject
    private StockFinanceEventRepository stockFinanceEventRepository;

    private MockMvc restStockFinanceEventMockMvc;

    private StockFinanceEvent stockFinanceEvent;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        StockFinanceEventResource stockFinanceEventResource = new StockFinanceEventResource();
        ReflectionTestUtils.setField(stockFinanceEventResource, "stockFinanceEventRepository", stockFinanceEventRepository);
        this.restStockFinanceEventMockMvc = MockMvcBuilders.standaloneSetup(stockFinanceEventResource).build();
    }

    @Before
    public void initTest() {
        stockFinanceEvent = new StockFinanceEvent();
        stockFinanceEvent.setDate(DEFAULT_DATE);
        stockFinanceEvent.setMessage(DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    public void createStockFinanceEvent() throws Exception {
        int databaseSizeBeforeCreate = stockFinanceEventRepository.findAll().size();

        // Create the StockFinanceEvent
        restStockFinanceEventMockMvc.perform(post("/api/stockFinanceEvents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stockFinanceEvent)))
                .andExpect(status().isCreated());

        // Validate the StockFinanceEvent in the database
        List<StockFinanceEvent> stockFinanceEvents = stockFinanceEventRepository.findAll();
        assertThat(stockFinanceEvents).hasSize(databaseSizeBeforeCreate + 1);
        StockFinanceEvent testStockFinanceEvent = stockFinanceEvents.get(stockFinanceEvents.size() - 1);
        assertThat(testStockFinanceEvent.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testStockFinanceEvent.getMessage()).isEqualTo(DEFAULT_MESSAGE);
    }

    @Test
    @Transactional
    public void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockFinanceEventRepository.findAll().size();
        // set the field null
        stockFinanceEvent.setDate(null);

        // Create the StockFinanceEvent, which fails.
        restStockFinanceEventMockMvc.perform(post("/api/stockFinanceEvents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stockFinanceEvent)))
                .andExpect(status().isBadRequest());

        List<StockFinanceEvent> stockFinanceEvents = stockFinanceEventRepository.findAll();
        assertThat(stockFinanceEvents).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkMessageIsRequired() throws Exception {
        int databaseSizeBeforeTest = stockFinanceEventRepository.findAll().size();
        // set the field null
        stockFinanceEvent.setMessage(null);

        // Create the StockFinanceEvent, which fails.
        restStockFinanceEventMockMvc.perform(post("/api/stockFinanceEvents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stockFinanceEvent)))
                .andExpect(status().isBadRequest());

        List<StockFinanceEvent> stockFinanceEvents = stockFinanceEventRepository.findAll();
        assertThat(stockFinanceEvents).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllStockFinanceEvents() throws Exception {
        // Initialize the database
        stockFinanceEventRepository.saveAndFlush(stockFinanceEvent);

        // Get all the stockFinanceEvents
        restStockFinanceEventMockMvc.perform(get("/api/stockFinanceEvents"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(stockFinanceEvent.getId().intValue())))
                .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
                .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE.toString())));
    }

    @Test
    @Transactional
    public void getStockFinanceEvent() throws Exception {
        // Initialize the database
        stockFinanceEventRepository.saveAndFlush(stockFinanceEvent);

        // Get the stockFinanceEvent
        restStockFinanceEventMockMvc.perform(get("/api/stockFinanceEvents/{id}", stockFinanceEvent.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(stockFinanceEvent.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingStockFinanceEvent() throws Exception {
        // Get the stockFinanceEvent
        restStockFinanceEventMockMvc.perform(get("/api/stockFinanceEvents/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateStockFinanceEvent() throws Exception {
        // Initialize the database
        stockFinanceEventRepository.saveAndFlush(stockFinanceEvent);

		int databaseSizeBeforeUpdate = stockFinanceEventRepository.findAll().size();

        // Update the stockFinanceEvent
        stockFinanceEvent.setDate(UPDATED_DATE);
        stockFinanceEvent.setMessage(UPDATED_MESSAGE);
        restStockFinanceEventMockMvc.perform(put("/api/stockFinanceEvents")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(stockFinanceEvent)))
                .andExpect(status().isOk());

        // Validate the StockFinanceEvent in the database
        List<StockFinanceEvent> stockFinanceEvents = stockFinanceEventRepository.findAll();
        assertThat(stockFinanceEvents).hasSize(databaseSizeBeforeUpdate);
        StockFinanceEvent testStockFinanceEvent = stockFinanceEvents.get(stockFinanceEvents.size() - 1);
        assertThat(testStockFinanceEvent.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testStockFinanceEvent.getMessage()).isEqualTo(UPDATED_MESSAGE);
    }

    @Test
    @Transactional
    public void deleteStockFinanceEvent() throws Exception {
        // Initialize the database
        stockFinanceEventRepository.saveAndFlush(stockFinanceEvent);

		int databaseSizeBeforeDelete = stockFinanceEventRepository.findAll().size();

        // Get the stockFinanceEvent
        restStockFinanceEventMockMvc.perform(delete("/api/stockFinanceEvents/{id}", stockFinanceEvent.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<StockFinanceEvent> stockFinanceEvents = stockFinanceEventRepository.findAll();
        assertThat(stockFinanceEvents).hasSize(databaseSizeBeforeDelete - 1);
    }
}
