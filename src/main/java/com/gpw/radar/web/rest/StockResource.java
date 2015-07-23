package com.gpw.radar.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.gpw.radar.domain.Stock;
import com.gpw.radar.repository.StockRepository;
import com.gpw.radar.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Stock.
 */
@RestController
@RequestMapping("/api")
public class StockResource {

    private final Logger log = LoggerFactory.getLogger(StockResource.class);

    @Inject
    private StockRepository stockRepository;

    /**
     * POST  /stocks -> Create a new stock.
     */
    @RequestMapping(value = "/stocks",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Stock> create(@Valid @RequestBody Stock stock) throws URISyntaxException {
        log.debug("REST request to save Stock : {}", stock);
        if (stock.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new stock cannot already have an ID").body(null);
        }
        Stock result = stockRepository.save(stock);
        return ResponseEntity.created(new URI("/api/stocks/" + stock.getId())).body(result);
    }

    /**
     * PUT  /stocks -> Updates an existing stock.
     */
    @RequestMapping(value = "/stocks",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Stock> update(@Valid @RequestBody Stock stock) throws URISyntaxException {
        log.debug("REST request to update Stock : {}", stock);
        if (stock.getId() == null) {
            return create(stock);
        }
        Stock result = stockRepository.save(stock);
        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /stocks -> get all the stocks.
     */
    @RequestMapping(value = "/stocks",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Stock>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
        throws URISyntaxException {
        Page<Stock> page = stockRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/stocks", offset, limit);
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /stocks/:id -> get the "id" stock.
     */
    @RequestMapping(value = "/stocks/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Stock> get(@PathVariable Long id) {
        log.debug("REST request to get Stock : {}", id);
        return Optional.ofNullable(stockRepository.findOne(id))
            .map(stock -> new ResponseEntity<>(
                stock,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /stocks/:id -> delete the "id" stock.
     */
    @RequestMapping(value = "/stocks/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Stock : {}", id);
        stockRepository.delete(id);
    }
}
