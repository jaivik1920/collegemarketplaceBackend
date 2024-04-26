package com.collegemarketplace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.collegemarketplace.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
	public Users findByusername(String username);
}
