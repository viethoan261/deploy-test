package vn.fs.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import vn.fs.model.dto.CategoryDto;

public interface ICategoryService {
	public List<CategoryDto> findAllCategoryOfPage(Pageable pageable);
	int getTotalItem();
	int getTotalItem(String name);
	public List<CategoryDto> findCategoryOfName(String name, Pageable pageable);
	public List<CategoryDto> findAllCategory();
	public CategoryDto getByID(Long id);
	public CategoryDto insert (CategoryDto categoryDto);
	public CategoryDto delete (CategoryDto categoryDto);
}
