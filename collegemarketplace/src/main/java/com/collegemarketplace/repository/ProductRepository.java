package com.collegemarketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.collegemarketplace.entity.Products;

public interface ProductRepository extends JpaRepository<Products, Long>{
	public List<Products> findByproductCatagory(String productCatagory);
	public List<Products> findByuser_id(long userid);
}
