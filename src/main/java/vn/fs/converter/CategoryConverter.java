package vn.fs.converter;

import org.springframework.stereotype.Component;

import vn.fs.entities.CategoryEntity;
import vn.fs.model.dto.CategoryDto;
@Component
public class CategoryConverter {
	public CategoryDto toDto (CategoryEntity categoryEntity) {
		CategoryDto categoryDto = new CategoryDto();
		categoryDto.setCategoryId(categoryEntity.getCategoryId());
		categoryDto.setCategoryName(categoryEntity.getCategoryName());
		categoryDto.setCategoryImage(categoryEntity.getCategoryImage());
		categoryDto.setStatus(categoryEntity.getStatus());
		return categoryDto;
	}
	
	public CategoryEntity toEntity (CategoryDto categoryDto) {
		CategoryEntity categoryEntity = new CategoryEntity();
		categoryEntity.setCategoryId(categoryDto.getCategoryId());
		categoryEntity.setCategoryName(categoryDto.getCategoryName());
		categoryEntity.setCategoryImage(categoryDto.getCategoryImage());
		categoryEntity.setStatus(categoryDto.getStatus());
		return categoryEntity;
	}
}
