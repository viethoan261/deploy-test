package vn.fs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.fs.converter.FavoriteConverter;
import vn.fs.entities.FavoriteEntity;
import vn.fs.model.dto.FavoriteDto;
import vn.fs.repository.FavoriteRepository;
import vn.fs.service.IFavoriteService;

@Service
public class FavoriteService implements IFavoriteService{

	@Autowired
	private FavoriteRepository favoriteRepository;
	
	@Autowired
	private FavoriteConverter favoriteConverter;
	
	@Override
	public Integer selectCountFavoriteSave(long userID) {
		// TODO Auto-generated method stub
		Integer totalFavorite = favoriteRepository.selectCountSave(userID);
		return totalFavorite;
	}

	@Override
	public FavoriteDto selectSaves(long productId, long userId) {
		// TODO Auto-generated method stub
		FavoriteEntity favoriteEntity = favoriteRepository.selectSaves(productId, userId);
		if (favoriteEntity != null) {
			FavoriteDto favoriteDto = favoriteConverter.toDto(favoriteEntity);
			return favoriteDto;
		}	
		return null;
	}

}
