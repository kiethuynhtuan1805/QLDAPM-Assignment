package vn.restaurant.management.service;

import vn.restaurant.management.dto.request.ReservationRequest;
import vn.restaurant.management.dto.request.ReservationsByDateRangeRequest;
import vn.restaurant.management.dto.response.ReservationResponse;

import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);
    ReservationResponse getReservationById(String reservationId);
    List<ReservationResponse> getAllReservations();
    ReservationResponse updateReservation(String reservationId, ReservationRequest request);
    boolean deleteReservation(String reservationId);
    List<ReservationResponse> getReservationsByDate(ReservationsByDateRangeRequest request);
}
