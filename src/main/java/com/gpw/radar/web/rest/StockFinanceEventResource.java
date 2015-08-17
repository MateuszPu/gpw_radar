package com.gpw.radar.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.gpw.radar.domain.StockFinanceEvent;
import com.gpw.radar.repository.StockFinanceEventRepository;
import com.gpw.radar.security.AuthoritiesConstants;
import com.gpw.radar.web.rest.util.PaginationUtil;

/**
 * REST controller for managing StockFinanceEvent.
 */
@RestController
@RequestMapping("/api")
public class StockFinanceEventResource {

	private final Logger log = LoggerFactory.getLogger(StockFinanceEventResource.class);

	@Inject
	private StockFinanceEventRepository stockFinanceEventRepository;

	/**
	 * POST /stockFinanceEvents -> Create a new stockFinanceEvent.
	 */
	@RequestMapping(value = "/stockFinanceEvents", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public ResponseEntity<StockFinanceEvent> create(@Valid @RequestBody StockFinanceEvent stockFinanceEvent) throws URISyntaxException {
		log.debug("REST request to save StockFinanceEvent : {}", stockFinanceEvent);
		if (stockFinanceEvent.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new stockFinanceEvent cannot already have an ID").body(null);
		}
		StockFinanceEvent result = stockFinanceEventRepository.save(stockFinanceEvent);
		return ResponseEntity.created(new URI("/api/stockFinanceEvents/" + stockFinanceEvent.getId())).body(result);
	}

//	/**
//	 * PUT /stockFinanceEvents -> Updates an existing stockFinanceEvent.
//	 */
//	@RequestMapping(value = "/stockFinanceEvents", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<StockFinanceEvent> update(@Valid @RequestBody StockFinanceEvent stockFinanceEvent) throws URISyntaxException {
//		log.debug("REST request to update StockFinanceEvent : {}", stockFinanceEvent);
//		if (stockFinanceEvent.getId() == null) {
//			return create(stockFinanceEvent);
//		}
//		StockFinanceEvent result = stockFinanceEventRepository.save(stockFinanceEvent);
//		return ResponseEntity.ok().body(result);
//	}

	/**
	 * GET /stockFinanceEvents -> get all the stockFinanceEvents.
	 */
	@RequestMapping(value = "/stockFinanceEvents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
	public ResponseEntity<List<StockFinanceEvent>> getAll(@RequestParam(value = "page", required = false) Integer offset, @RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
		Page<StockFinanceEvent> page = stockFinanceEventRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/stockFinanceEvents", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET /stockFinanceEvents/:id -> get the "id" stockFinanceEvent.
	 */
	@RequestMapping(value = "/stockFinanceEvents/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<StockFinanceEvent> get(@PathVariable Long id) {
		log.debug("REST request to get StockFinanceEvent : {}", id);
		return Optional.ofNullable(stockFinanceEventRepository.findOne(id)).map(stockFinanceEvent -> new ResponseEntity<>(stockFinanceEvent, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /stockFinanceEvents/:id -> delete the "id" stockFinanceEvent.
	 */
	@RequestMapping(value = "/stockFinanceEvents/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	public void delete(@PathVariable Long id) {
		log.debug("REST request to delete StockFinanceEvent : {}", id);
		stockFinanceEventRepository.delete(id);
	}
}
