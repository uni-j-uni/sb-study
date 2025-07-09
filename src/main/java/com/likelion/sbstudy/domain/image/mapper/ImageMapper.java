package com.likelion.sbstudy.domain.image.mapper;

import com.likelion.sbstudy.domain.image.dto.response.ImageResponse;
import com.likelion.sbstudy.domain.image.entity.Image;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

  public ImageResponse toImageResponse(List<Image> images) {
    return ImageResponse.builder()
        .imagesUrl(
            images.stream()
                .map(Image::getImageUrl)
                .toList()
        )
        .build();
  }

}