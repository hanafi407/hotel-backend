package com.hanafi.hanafihotel.service;

import com.hanafi.hanafihotel.model.BookedRoom;

import java.util.List;
import java.util.Optional;

public interface BookingRoomService {
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    List<BookedRoom> getAllBookings();

    BookedRoom findByBookingConfirmationCode(String confirmationCode);

    BookedRoom findByBookingId(Long bookingId);
    List<BookedRoom> findBookingByEmail(String email);

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    void cancelBooking(Long roomId);
}
