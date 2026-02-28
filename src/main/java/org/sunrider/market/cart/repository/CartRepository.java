package org.sunrider.market.cart.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sunrider.market.cart.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    @EntityGraph(attributePaths = {
        "items",
        "items.product"
    })
    Optional<Cart> findByUserId(UUID UserId);

    Optional<Cart> findById(UUID id);

}
