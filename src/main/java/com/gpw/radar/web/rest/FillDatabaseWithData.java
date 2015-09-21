//package com.gpw.radar.web.rest;
//
//import java.io.IOException;
//
//import javax.annotation.security.RolesAllowed;
//import javax.inject.Inject;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.gpw.radar.security.AuthoritiesConstants;
//import com.gpw.radar.service.FillDataBaseWithDataService;
//
//@RestController
//@RequestMapping("/api/prepare/app")
//@RolesAllowed(AuthoritiesConstants.ADMIN)
//public class FillDatabaseWithData {
//
//    @Inject
//    private FillDataBaseWithDataService fillDataBaseWithDataService;
//    
//    @RequestMapping(value = "/1")
//    public ResponseEntity<Void> fillDataBaseWithStocks() throws IOException {
//        return fillDataBaseWithDataService.fillDataBaseWithStocks();
//    }
//
//    @RequestMapping(value = "/2")
//    public ResponseEntity<Void> fillDataBaseWithStockDetails() {
//    	return fillDataBaseWithDataService.fillDataBaseWithStockDetails();
//    }
//
//    @RequestMapping(value = "/3")
//    public ResponseEntity<Void> fillDataBaseWithStockFinanceEvent() throws IOException {
//    	return fillDataBaseWithDataService.fillDataBaseWithStockFinanceEvent();
//    }
//}