package com.hanafi.hanafihotel.controller;

import com.hanafi.hanafihotel.exception.InternalServerException;
import com.hanafi.hanafihotel.exception.PhotoRetrievalException;
import com.hanafi.hanafihotel.exception.ResourceNotFoundException;
import com.hanafi.hanafihotel.model.BookedRoom;
import com.hanafi.hanafihotel.model.Room;
import com.hanafi.hanafihotel.response.RoomResponse;
import com.hanafi.hanafihotel.service.impl.BookingRoomServiceImpl;
import com.hanafi.hanafihotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;
    private final BookingRoomServiceImpl bookingRoomServiceImpl;

    @PostMapping("/add/new-room")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice
    ) throws SQLException, IOException {
        Room room = roomService.addNewRoom(photo, roomType, roomPrice);
        RoomResponse roomResponse = new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice());

        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/types")
    public List<String> getRoomTypes() {
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException, ResourceNotFoundException {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : rooms) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            String base64Photo = Base64.encodeBase64String(photoBytes);
            RoomResponse roomResponse = getRoomResponse(room);
            roomResponse.setPhoto(base64Photo);
            roomResponses.add(roomResponse);
        }
        return ResponseEntity.ok(roomResponses);
    }

    @PutMapping("/update/{roomId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long roomId,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) BigDecimal roomPrice,
            @RequestParam(required = false) MultipartFile photo
    ) throws SQLException, ResourceNotFoundException, IOException, InternalServerException {
        byte[] photoBytes = photo != null && !photo.isEmpty() ?
                photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);

        Blob photoBlob = photoBytes != null && photoBytes.length > 0 ?
                new SerialBlob(photoBytes) : null;

        Room room = roomService.updateRoom(roomId, roomType, roomPrice, photoBytes);

        room.setPhoto(photoBlob);

        RoomResponse roomResponse = getRoomResponse(room);

        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId) throws ResourceNotFoundException {

        System.out.println("/rooms/roomId");
        Optional<Room> optionalRoom = roomService.getRoomById(roomId);
        return optionalRoom.map(room -> {
                    RoomResponse roomResponse = null;
                    try {
                        roomResponse = getRoomResponse(room);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return ResponseEntity.ok(Optional.of(roomResponse));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Room is not found"));
    }

    @GetMapping("/available-room")
    public ResponseEntity<List<RoomResponse>> getAvailableRoom(
            @RequestParam("checkInDate") @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso= DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam("roomType") String roomType) throws SQLException {

        List<Room> availableRoom = roomService.getAvailableRoom(checkInDate,checkOutDate,roomType);
        List<RoomResponse> roomResponses = new ArrayList<>();
        for(Room room: availableRoom){
            byte[] photoByte = roomService.getRoomPhotoByRoomId(room.getId());
            if(photoByte!= null && photoByte.length > 0){
                String photoBase64= Base64.encodeBase64String(photoByte);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(photoBase64);
                roomResponses.add(roomResponse);

            }
        }

        if(roomResponses.isEmpty()){
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.ok(roomResponses);
        }

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private RoomResponse getRoomResponse(Room room) throws SQLException {
//        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
//        List<BookingResponse> bookingInfo = bookings.stream().map(booking-> new BookingResponse(
//                booking.getBookingId(),
//                booking.getCheckInDate(),
//                booking.getCheckOutDate(),
//                booking.getBookingConfirmationCode()
//        )).toList();

        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();

        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo!");
            }
        }

        return new RoomResponse(
                room.getId(),
                room.getRoomType(),
                room.getRoomPrice(),
                room.isBooked(),
                photoBytes
//                bookingInfo
        );
    }

    private List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRoomServiceImpl.getAllBookingsByRoomId(roomId);
    }
}
