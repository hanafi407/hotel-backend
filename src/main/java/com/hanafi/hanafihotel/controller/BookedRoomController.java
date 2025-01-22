package com.hanafi.hanafihotel.controller;

import com.hanafi.hanafihotel.exception.InvalidBookingRequestException;
import com.hanafi.hanafihotel.exception.ResourceNotFoundException;
import com.hanafi.hanafihotel.model.BookedRoom;
import com.hanafi.hanafihotel.model.Room;
import com.hanafi.hanafihotel.response.BookingResponse;
import com.hanafi.hanafihotel.response.RoomResponse;
import com.hanafi.hanafihotel.service.BookingRoomService;
import com.hanafi.hanafihotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookedRoomController {
    private final BookingRoomService bookingRoomService;
    private final RoomService roomService;

    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookedRoom> bookings = bookingRoomService.getAllBookings();
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for (BookedRoom booking : bookings) {
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId) {
        try {
            BookedRoom bookedRoom = bookingRoomService.findByBookingId(bookingId);
            BookingResponse bookingResponse = getBookingResponse(bookedRoom);

            return ResponseEntity.ok(bookingResponse);
        } catch (ResourceNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getBookingByEmail(@PathVariable String email) {
        try {
            List<BookedRoom> bookings = bookingRoomService.findBookingByEmail(email);
            List<BookingResponse> bookingResponses = new ArrayList();
            for(BookedRoom booking : bookings){
                BookingResponse bookingResponse = getBookingResponse(booking);
                bookingResponses.add(bookingResponse);
            }

            return ResponseEntity.ok(bookingResponses);
        } catch (ResourceNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
    }

    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookConfirmationCode(@PathVariable String confirmationCode) {
        try {
            BookedRoom bookedRoom = bookingRoomService.findByBookingConfirmationCode(confirmationCode);
            BookingResponse bookingResponse = getBookingResponse(bookedRoom);

            return ResponseEntity.ok(bookingResponse);
        } catch (ResourceNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());

        }
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(
            @PathVariable Long roomId,
            @RequestBody BookedRoom bookingRequest
    ) {
        try {
            String confirmationCode = bookingRoomService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok("Room booked successfully, your booking confirmation code is: " + confirmationCode);
        } catch (InvalidBookingRequestException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @DeleteMapping("/{bookingId}/delete")
    public void cancelBooking(@PathVariable Long bookingId) {
        bookingRoomService.cancelBooking(bookingId);
    }

    private BookingResponse getBookingResponse(BookedRoom booking) {
        Room theRoom = roomService.getRoomById(booking.getRoom().getId()).get();
        RoomResponse roomResponse = new RoomResponse(
                theRoom.getId(),
                theRoom.getRoomType(),
                theRoom.getRoomPrice()
        );

        return new BookingResponse(
                booking.getBookingId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuestFullName(),
                booking.getGuestEmail(),
                booking.getNumOfChildren(),
                booking.getNumOfAdult(),
                booking.getTotalNumOfGuest(),
                booking.getBookingConfirmationCode(),
                roomResponse
        );
    }
}
