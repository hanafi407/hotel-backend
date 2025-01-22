package com.hanafi.hanafihotel.service.impl;

import com.hanafi.hanafihotel.exception.InvalidBookingRequestException;
import com.hanafi.hanafihotel.exception.ResourceNotFoundException;
import com.hanafi.hanafihotel.model.BookedRoom;
import com.hanafi.hanafihotel.model.Room;
import com.hanafi.hanafihotel.repository.BookingRepository;
import com.hanafi.hanafihotel.service.BookingRoomService;
import com.hanafi.hanafihotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingRoomServiceImpl implements BookingRoomService {
    private final BookingRepository bookingRepository;
    private final RoomService roomService;

    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    @Override
    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
        return bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(()-> new ResourceNotFoundException("No booking found with booking code: "+confirmationCode));
    }

    @Override
    public BookedRoom findByBookingId(Long bookingId) {
        return bookingRepository.findByBookingId(bookingId).orElseThrow(()-> new ResourceNotFoundException("No booking found with booking code: "+ bookingId) );
    }

    @Override
    public List<BookedRoom> findBookingByEmail(String email) {
        return bookingRepository.findByGuestEmail(email);
    }

    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
            throw new InvalidBookingRequestException("Check in date must be before check out date!");
        }

        Room room = roomService.getRoomById(roomId).get();

        List<BookedRoom> existingBookings = room.getBookings();

        boolean roomIsAvailable = isRoomAvailable(bookingRequest, existingBookings);

        if(roomIsAvailable){
            room.addBooking(bookingRequest);
            bookingRepository.save(bookingRequest);
        }else {
            throw new InvalidBookingRequestException("Sorry, this room is not available for the selected dates");
        }

        return bookingRequest.getBookingConfirmationCode();
    }

    private boolean isRoomAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {

        return existingBookings.stream().noneMatch(existingBooking ->
                bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                        || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                        || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate()) && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                        || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate()) && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                        || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate()) && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))
                        || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate()) && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))
                        || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate()) && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
        );
    }

    @Override
    public void cancelBooking(Long roomId) {
        bookingRepository.deleteById(roomId);
    }
}
