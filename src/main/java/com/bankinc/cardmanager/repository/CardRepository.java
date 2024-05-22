package com.bankinc.cardmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankinc.cardmanager.model.entity.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {
}
