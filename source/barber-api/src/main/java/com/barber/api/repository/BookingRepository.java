package com.barber.api.repository;

import com.barber.api.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookingRepository extends JpaRepository<Booking, Long>,
    JpaSpecificationExecutor<Booking> {

}
