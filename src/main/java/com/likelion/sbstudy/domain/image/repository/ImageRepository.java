package com.likelion.sbstudy.domain.image.repository;

import com.likelion.sbstudy.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}