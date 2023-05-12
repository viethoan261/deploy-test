package vn.fs.converter;

import org.springframework.stereotype.Component;

import vn.fs.entities.FavoriteEntity;
import vn.fs.model.dto.FavoriteDto;

@Component
public class FavoriteConverter {
	public FavoriteDto toDto (FavoriteEntity favoriteEntity) {
		FavoriteDto favoriteDto = new FavoriteDto();
		favoriteDto.setFavoriteId(favoriteEntity.getFavoriteId());
		favoriteDto.setUserId(favoriteEntity.getUser().getUserId());
		favoriteDto.setProductId(favoriteEntity.getProduct().getProductId());
		return favoriteDto;
	}
}
