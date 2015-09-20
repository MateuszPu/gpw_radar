package com.gpw.radar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gpw.radar.domain.database.FillDataStatus;
import com.gpw.radar.domain.database.Type;

public interface FillDataStatusRepository extends JpaRepository<FillDataStatus, Type> {

	@Modifying
	@Query(value = "update FILLED_DATA_STATUS set filled = true where data_type = :type", nativeQuery = true)
	void updateType(@Param("type") String type);
}