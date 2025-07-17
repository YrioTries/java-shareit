package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);
    @Query("SELECT i FROM Item i " +
            "WHERE i.available = true AND " +
            "(LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))")
    List<Item> searchAvailableItems(String text);

    List<Item> findByRequestId(Long requestId);

    @Query("SELECT i FROM Item i " +
            "WHERE i.request IS NOT NULL AND " +
            "i.request.id IN :requestIds")
    List<Item> findByRequestIds(List<Long> requestIds);

    boolean existsByIdAndOwnerId(Long itemId, Long ownerId);
}