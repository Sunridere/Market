package org.sunrider.market.product.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.sunrider.market.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @EntityGraph(attributePaths = {"category", "images"})
    List<Product> findAllByDeletedFalse();

    @EntityGraph(attributePaths = {"category", "images"})
    Optional<Product> findByIdAndDeletedFalse(UUID id);

    @EntityGraph(attributePaths = {"category", "images"})
    List<Product> findAllByIdInAndDeletedFalse(Collection<UUID> ids);

}
