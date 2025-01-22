package com.hanafi.hanafihotel.service.impl;

import com.hanafi.hanafihotel.exception.InternalServerException;
import com.hanafi.hanafihotel.exception.ResourceNotFoundException;
import com.hanafi.hanafihotel.model.Room;
import com.hanafi.hanafihotel.repository.RoomRepository;
import com.hanafi.hanafihotel.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    @Override
    public Room addNewRoom(MultipartFile file, String roomType, BigDecimal roomPrice) throws IOException, SQLException {
        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        if(!file.isEmpty()){
            byte[] photoBytes = file.getBytes();
            SerialBlob photoBlob = new SerialBlob(photoBytes);
            room.setPhoto(photoBlob);

        }
        return roomRepository.save(room);
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException, ResourceNotFoundException {
        Optional<Room> roomOpt= roomRepository.findById(roomId);
        if(roomOpt.isEmpty()){
            throw new ResourceNotFoundException("Sorry, room is not found!");
        }

        Blob photo = roomOpt.get().getPhoto();
        if(photo != null){
            return photo.getBytes(1,(int) photo.length());
        }
        return null;
    }

    @Override
    public void deleteRoom(Long id) {
        Optional<Room> room = roomRepository.findById(id);

        if(room.isPresent()){
            roomRepository.deleteById(id);
        }
    }

    @Override
    public Room updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoByte) throws ResourceNotFoundException, InternalServerException {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room is not found"));

        if(roomType != null) room.setRoomType(roomType);
        if(roomPrice != null) room.setRoomPrice(roomPrice);
        if(photoByte !=null && photoByte.length>0){
            try{
                room.setPhoto(new SerialBlob(photoByte));
            } catch (SQLException e) {
                throw new InternalServerException("Error updating room");
            }
        }
        return roomRepository.save(room);
    }

    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return Optional.of(roomRepository.findById(roomId).get());

    }

    @Override
    public List<Room> getAvailableRoom(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        return roomRepository.findAvailableRoomByDatesAndType(checkInDate,checkOutDate,roomType);
    }


}
