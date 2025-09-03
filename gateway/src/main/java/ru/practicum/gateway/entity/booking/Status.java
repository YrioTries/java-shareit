package ru.practicum.gateway.entity.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum Status {
    ALL("ALL"),
    CURRENT("CURRENT"),
    WAITING("WAITING"),
    FUTURE("FUTURE"),
    PAST("PAST"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    CANCELED("CANCELED");

    private final String status;

    public static boolean isCorrectStatus(String testedValue) {
        for (Status status : values()) {
            if (status.status.equals(testedValue)) {
                return true;
            }
        }
        return false;
    }

    public static Optional<Status> from(String stringState) {
        for (Status state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
