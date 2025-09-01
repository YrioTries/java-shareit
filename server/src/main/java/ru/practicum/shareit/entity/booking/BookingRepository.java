package ru.practicum.shareit.entity.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.entity.booking.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.booker.id = :userId AND " +
            "b.start <= :now AND " +
            "b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByBookerId(@Param("userId") Long userId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.booker.id = :userId AND " +
            "b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastByBookerId(@Param("userId") Long userId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.booker.id = :userId AND " +
            "b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByBookerId(@Param("userId") Long userId, @Param("now") LocalDateTime now, Pageable pageable);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Status status, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.item.owner.id = :ownerId AND " +
            "b.start <= :now AND " +
            "b.end >= :now " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.item.owner.id = :ownerId AND " +
            "b.end < :now " +
            "ORDER BY b.start DESC")
    List<Booking> findPastByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.item.owner.id = :ownerId AND " +
            "b.start > :now " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByOwnerId(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END " +
            "FROM Booking b WHERE " +
            "b.item.id = :itemId AND " +
            "b.status = 'APPROVED' AND " +
            "(:start BETWEEN b.start AND b.end OR " +
            ":end BETWEEN b.start AND b.end OR " +
            "(b.start <= :start AND b.end >= :end))")
    boolean existsApprovedBookingsForItemBetweenDates(
            @Param("itemId") Long itemId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.item.id = :itemId AND " +
            "b.status = 'APPROVED' AND " +
            "b.end < :now " +
            "ORDER BY b.end DESC")
    List<Booking> findLastBookingForItem(
            @Param("itemId") Long itemId,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE " +
            "b.item.id = :itemId AND " +
            "b.status = 'APPROVED' AND " +
            "b.start > :now " +
            "ORDER BY b.start ASC")
    List<Booking> findNextBookingForItem(
            @Param("itemId") Long itemId,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b WHERE b.booker.id = :bookerId AND b.item.id = :itemId AND b.end < :endDate")
    boolean existsByBookerIdAndItemIdAndEndIsBefore(
            @Param("bookerId") Long bookerId,
            @Param("itemId") Long itemId,
            @Param("endDate") LocalDateTime endDate);

    @Modifying
    @Query("DELETE FROM Booking b WHERE b.booker.id = :userId")
    void deleteByBookerId(@Param("userId") Long userId);

    @Query(value = "SELECT b.* FROM bookings as b " +
            "JOIN items as i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND i.id = ?2 " +
            "AND b.status = 'APPROVED' ", nativeQuery = true)
    List<Booking> findAllByUserBookings(Long userId, Long itemId, LocalDateTime now);
}