package com.gpw.radar.repository.auto.update;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gpw.radar.domain.database.DailyStockDetailsParser;
import com.gpw.radar.service.auto.update.stockDetails.ParserMethod;

public interface DailyStockDetailsParserRepository extends JpaRepository<DailyStockDetailsParser, ParserMethod> {

	@Query(value="select * from stock_details_parser_method limit 1", nativeQuery = true)
	DailyStockDetailsParser findMethod();
	
	@Modifying
	@Query(value="update stock_details_parser_method set method = :method", nativeQuery = true)
	void setStockDetailsParserMethod(@Param("method") String method); 
}
