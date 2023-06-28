package com.ztech.subtly.controller.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ztech.subtly.model.AudioData;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface AudioDataRepository extends JpaRepository<AudioData, Integer> {

}