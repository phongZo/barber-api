package com.barber.api.repository;

import com.barber.api.model.Booking;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long>,
    JpaSpecificationExecutor<Booking> {

  Boolean existsByCustomerId(Long customerId);

  Boolean existsByBranchId(Long branchId);

  Boolean existsByCustomerIdAndBookingDateAndStatusIn(Long customerId, Date bookingDate, List<Integer> statuses);

  @Query(value =
      "SELECT CASE WHEN EXISTS ( " +
          "   SELECT 1 FROM db_barber_booking b " +
          "   WHERE b.booking_date = :bookingDate " +
          "   AND b.status IN (:statuses) " +
          "   AND JSON_UNQUOTE(JSON_EXTRACT(b.customer_info, '$.email')) = :email " +
          ") THEN 1 ELSE 0 END",
      nativeQuery = true
  )
  Integer existsByEmailAndBookingDateAndStatus(@Param("email") String email, @Param("bookingDate") Date bookingDate, @Param("statuses") List<Integer> statuses);
}
