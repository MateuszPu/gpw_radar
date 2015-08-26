package com.gpw.radar.web.rest;

import java.io.IOException;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gpw.radar.domain.StockDetails;
import com.gpw.radar.service.StockDetailsService;

/**
 * REST controller for managing StockDetails.
 */
@RestController
@RequestMapping("/api")
public class StockDetailsResource {
	
	@Inject
	private StockDetailsService stockDetailsService;
	
	@RequestMapping(value = "/get/top/by/date", method = RequestMethod.GET)
	public ResponseEntity<StockDetails> getTopStockDetailsByDate() throws IOException
	{
		return stockDetailsService.findTopByDate();
	}	

}
//
//	@Inject
//	private StockDetailsService stockDetailsService;
//
//	/**
//	 * POST /stockDetailss -> Create a new stockDetails.
//	 */
//	@RequestMapping(value = "/stockDetailss", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	@RolesAllowed(AuthoritiesConstants.ADMIN)
//	public ResponseEntity<StockDetails> create(@Valid @RequestBody StockDetails stockDetails) throws URISyntaxException {
//		return stockDetailsService.save(stockDetails);
//	}
//
////	/**
////	 * PUT /stockDetailss -> Updates an existing stockDetails.
////	 */
////	@RequestMapping(value = "/stockDetailss", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
////	@Timed
////	@PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
////	public ResponseEntity<StockDetails> update(@Valid @RequestBody StockDetails stockDetails) throws URISyntaxException {
////		log.debug("REST request to update StockDetails : {}", stockDetails);
////		if (stockDetails.getId() == null) {
////			return create(stockDetails);
////		}
////		StockDetails result = stockDetailsRepository.save(stockDetails);
////		return ResponseEntity.ok().body(result);
////	}
//
//	/**
//	 * GET /stockDetailss -> get all the stockDetailss.
//	 */
//	@RequestMapping(value = "/stockDetailss", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<List<StockDetails>> getAll(@RequestParam(value = "page", required = false) Integer offset, @RequestParam(value = "per_page", required = false) Integer limit) throws URISyntaxException {
////		Page<StockDetails> page = stockDetailsRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
//		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/stockDetailss", offset, limit);
//		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
//	}
//
//	/**
//	 * GET /stockDetailss/:id -> get the "id" stockDetails.
//	 */
//	@RequestMapping(value = "/stockDetailss/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<StockDetails> get(@PathVariable Long id) {
//		log.debug("REST request to get StockDetails : {}", id);
//		return Optional.ofNullable(stockDetailsRepository.findOne(id)).map(stockDetails -> new ResponseEntity<>(stockDetails, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//	}
//
//	/**
//	 * DELETE /stockDetailss/:id -> delete the "id" stockDetails.
//	 */
//	@RequestMapping(value = "/stockDetailss/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	@RolesAllowed(AuthoritiesConstants.ADMIN)
//	public void delete(@PathVariable Long id) {
//		log.debug("REST request to delete StockDetails : {}", id);
//		stockDetailsRepository.delete(id);
//	}
//}
