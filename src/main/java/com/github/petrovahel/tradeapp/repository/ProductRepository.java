package com.github.petrovahel.tradeapp.repository;

import com.github.petrovahel.tradeapp.entity.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Integer> {

}
