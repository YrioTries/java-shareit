package ru.practicum.shareit.booking;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")  // Явно указываем имя таблицы в БД
@Getter
@Setter
@ToString
@NoArgsConstructor  // Добавляем конструктор без аргументов (требование JPA)
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime start;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime end;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)  // Внешний ключ
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)  // Многие бронирования к одному пользователю
    @JoinColumn(name = "booker_id", nullable = false)  // Внешний ключ
    private User booker;

    @Enumerated(EnumType.STRING)  // Сохраняем enum как строку в БД
    @Column(nullable = false, length = 20)  // Ограничение длины для статуса
    private Status status;
}