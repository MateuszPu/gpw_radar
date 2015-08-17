package com.gpw.radar.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.repository.StockDetailsRepository;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.web.rest.util.PaginationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing StockDetails.
 */
@RestController
@RequestMapping("/api")
public class StockDetailsResource {

	private final Logger log = LoggerFactory.getLogger(StockDetailsResource.class);

	@Inject
	private StockDetailsRepository stockDetailsRepository;

	/**
	 * POST /stockDetailss -> Create a new stockDetails.
	 */
	@RequestMapping(value = "/stockDetailss", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
	public ResponseEntity<StockDetails> create(@Valid @RequestBody StockDetails stockDetails) throws URISyntaxException {
		log.debug("REST request to save StockDetails : {}", stockDetails);
		if (stockDetails.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new stockDetails cannot already have an ID").body(null);
		}
		StockDetails result = stockDetailsRepository.save(stockDetails);
		return ResponseEntity.created(new URI("/api/stockDetailss/" + stockDetails.getId())).body(result);
	}

//	/**
//	 * PUT /stockDetailss -> Updates an existing stockDetails.
//	 */
//	@RequestMapping(value = "/stockDetailss", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	@PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
//	public ResponseEntity<StockDetails> update(@Valid @RequestBody StockDetails stockDetails) throws URISyntaxException {
//		log.debug("REST request to update StockDetails : {}", stockDetails);
//		if (stockDetails.getId() == null) {
//			return create(stockDetails);
//		}
//		StockDetails result = stockDetailsRepository.save(stockDetails);
//		return ResponseEntity.ok().body(result);
//	}

	/**
	 * GET /stockDetailss -> get all the stockDetailss.
	 */
	@RequestMapping(value = "/stockDetailss", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StockDetails>> getAll(@RequestParam(value = "page", required = false) Integer offset, @RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
		Page<StockDetails> page = stockDetailsRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/stockDetailss", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /stockDetailss/:id -> get the "id" stockDetails.
	 */
	@RequestMapping(value = "/stockDetailss/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<StockDetails> get(@PathVariable Long id) {
		log.debug("REST request to get StockDetails : {}", id);
		return Optional.ofNullable(stockDetailsRepository.findOne(id)).map(stockDetails -> new ResponseEntity<>(stockDetails, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /stockDetailss/:id -> delete the "id" stockDetails.
	 */
	@RequestMapping(value = "/stockDetailss/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
	public void delete(@PathVariable Long id) {
		log.debug("REST request to delete StockDetails : {}", id);
		stockDetailsRepository.delete(id);
	}
}
