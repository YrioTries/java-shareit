package ru.practicum.gateway.entity.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    WAITING("WAITING"),
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
}
