package vn.fs.service;

import vn.fs.model.dto.FavoriteDto;

public interface IFavoriteService {
	public Integer selectCountFavoriteSave(long userID);
	public FavoriteDto selectSaves (long productId, long userId);
}
