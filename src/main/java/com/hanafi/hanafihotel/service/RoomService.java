package com.hanafi.hanafihotel.service;

import com.hanafi.hanafihotel.exception.InternalServerException;
import com.hanafi.hanafihotel.exception.ResourceNotFoundException;
import com.hanafi.hanafihotel.model.Room;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    Room addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice) throws IOException, SQLException;

    List<String> getAllRoomTypes();

    List<Room> getAllRooms();

    byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException, ResourceNotFoundException;

    void deleteRoom(Long id);

    Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoByte) throws ResourceNotFoundException, InternalServerException;

    Optional<Room> getRoomById(Long roomId);

    List<Room> getAvailableRoom(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
}
