package vn.fs.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.fs.converter.CategoryConverter;
import vn.fs.entities.CategoryEntity;
import vn.fs.model.dto.CategoryDto;
import vn.fs.repository.CategoryRepository;
import vn.fs.service.ICategoryService;
@Service
public class CategoryService implements ICategoryService{

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private CategoryConverter categoryConverter;
	
	@Override
	public List<CategoryDto> findAllCategoryOfPage(Pageable pageable) {
		// TODO Auto-generated method stub
		List<CategoryDto> categoryDtos = new ArrayList<>();
		List<CategoryEntity> categoryEntities = categoryRepository.findAll(pageable).getContent();
		for (CategoryEntity categoryEntity : categoryEntities) {
			CategoryDto categoryDto = categoryConverter.toDto(categoryEntity);
			categoryDtos.add(categoryDto);
		}
		return categoryDtos;
	}

	@Override
	public int getTotalItem() {
		// TODO Auto-generated method stub
		int totalItem = categoryRepository.getTotalItem();
		return totalItem;
	}

	@Override
	public int getTotalItem(String name) {
		// TODO Auto-generated method stub
		int totalItem = categoryRepository.getTotalItem(name);
		return totalItem;
	}

	@Override
	public List<CategoryDto> findCategoryOfName(String name, Pageable pageable) {
		// TODO Auto-generated method stub
		List<CategoryDto> categoryDtos = new ArrayList<>();
		Page<CategoryEntity> page= categoryRepository.findByName(name,pageable);
		List<CategoryEntity> categoryEntities = page.getContent();
		for (CategoryEntity categoryEntity : categoryEntities) {
			CategoryDto categoryDto = categoryConverter.toDto(categoryEntity);
			categoryDtos.add(categoryDto);
		}
		return categoryDtos;
	}

	@Override
	public List<CategoryDto> findAllCategory() {
		// TODO Auto-generated method stub
		List<CategoryDto> categoryDtos = new ArrayList<>();
		List<CategoryEntity> categoryEntities = categoryRepository.findAll();
		for (CategoryEntity categoryEntity : categoryEntities) {
			CategoryDto categoryDto = categoryConverter.toDto(categoryEntity);
			categoryDtos.add(categoryDto);
		}
		return categoryDtos;
	}

	@Override
	public CategoryDto getByID(Long id) {
		// TODO Auto-generated method stub
		CategoryEntity categoryEntity = categoryRepository.getById(id);
		CategoryDto categoryDto = categoryConverter.toDto(categoryEntity);
		return categoryDto;
	}

	@Override
	@Transactional
	public CategoryDto insert(CategoryDto categoryDto) {
		// TODO Auto-generated method stub
		CategoryEntity categoryEntity = categoryConverter.toEntity(categoryDto);
		categoryEntity.setStatus(true);
		categoryEntity = categoryRepository.save(categoryEntity);
		categoryDto = categoryConverter.toDto(categoryEntity);
		return categoryDto;
	}

	@Override
	@Transactional
	public CategoryDto delete(CategoryDto categoryDto) {
		// TODO Auto-generated method stub
		CategoryEntity categoryEntity = categoryConverter.toEntity(categoryDto);
		categoryEntity.setStatus(false);
		categoryEntity = categoryRepository.save(categoryEntity);
		categoryDto = categoryConverter.toDto(categoryEntity);
		return categoryDto;
	}
}
