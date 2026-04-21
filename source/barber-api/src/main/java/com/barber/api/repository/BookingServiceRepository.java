package com.barber.api.repository;

import com.barber.api.model.BookingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookingServiceRepository extends JpaRepository<BookingService, Long>,
    JpaSpecificationExecutor<BookingService> {

}
