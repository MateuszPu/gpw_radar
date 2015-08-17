package com.gpw.radar.web.rest;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.gpw.radar.domain.Stock;
import com.gpw.radar.domain.StockFinanceEvent;
import com.gpw.radar.domain.User;
import com.gpw.radar.repository.StockFinanceEventRepository;
import com.gpw.radar.repository.UserRepository;
import com.gpw.radar.security.AuthoritiesConstants;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

	private final Logger log = LoggerFactory.getLogger(UserResource.class);

	@Inject
	private UserRepository userRepository;

	@Inject
	private StockFinanceEventRepository stockFinanceEventRepository;

	/**
	 * GET /users -> get all users.
	 */
	@RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<User> getAll() {
		log.debug("REST request to get all Users");
		return userRepository.findAll();
	}

	/**
	 * GET /users/:login -> get the "login" user.
	 */
	@RequestMapping(value = "/users/{login}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@RolesAllowed(AuthoritiesConstants.ADMIN)
	ResponseEntity<User> getUser(@PathVariable String login) {
		log.debug("REST request to get User : {}", login);
		return userRepository.findOneByLogin(login).map(user -> new ResponseEntity<>(user, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@RequestMapping(value = "/users/stocks/followed", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed(AuthoritiesConstants.USER)
	Set<Stock> getListStocksFollowedByUser(Principal principal) {
		String name = principal.getName();
		Optional<User> user = userRepository.findOneByLogin(name);
		User activeUser = user.get();
		return activeUser.getStocks();
	}

	@RequestMapping(value = "/users/stocks/followed/finance/event", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@RolesAllowed(AuthoritiesConstants.USER)
	Set<StockFinanceEvent> getListFinanceEventsStocksFollowedByUser(Principal principal) {
		Set<StockFinanceEvent> stockFinanveEvents = new HashSet<StockFinanceEvent>();
		String name = principal.getName();
		Optional<User> user = userRepository.findOneByLogin(name);
		User activeUser = user.get();
		for (Stock element : activeUser.getStocks()) {
			stockFinanveEvents.addAll(stockFinanceEventRepository.findByStock(element));
		}
		return stockFinanveEvents;
	}
}
