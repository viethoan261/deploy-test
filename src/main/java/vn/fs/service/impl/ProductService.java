package vn.fs.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import vn.fs.converter.ProductConverter;
import vn.fs.entities.CategoryEntity;
import vn.fs.entities.FavoriteEntity;
import vn.fs.entities.ProductEntity;
import vn.fs.model.dto.ProductDto;
import vn.fs.model.dto.UserDto;
import vn.fs.model.response.CategoryResponse;
import vn.fs.repository.CategoryRepository;
import vn.fs.repository.FavoriteRepository;
import vn.fs.repository.ProductRepository;
import vn.fs.service.IProductService;

@Service
public class ProductService implements IProductService{
	
	@Value("${upload.path}")
	private String pathUploadImage;
	//Vị trí lưu file là :"/upload/images"
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductConverter productConverter;
	
	@Autowired
	private FavoriteRepository favoriteRepository;
	
	@Override
	public List<ProductDto> findAllProductOfPage(Pageable pageable) {
		// TODO Auto-generated method stub
		List<ProductDto> productDtos = new ArrayList<>();
		List<ProductEntity> productEntities = productRepository.findAll(pageable).getContent();
		for (ProductEntity productEntity : productEntities) {
			ProductDto productDto = productConverter.toDto(productEntity);
			productDtos.add(productDto);
		}
		return productDtos;
	}

	@Override
	public int getTotalItem() {
		// TODO Auto-generated method stub
		int totalItem = productRepository.getTotalItem();
		return totalItem;
	}

	@Override
	public int getTotalItem(String name) {
		// TODO Auto-generated method stub
		int totalItem = productRepository.getTotalItem(name);
		return totalItem;
	}

	@Override
	public List<ProductDto> findProductOfName(String name, Pageable pageable) {
		// TODO Auto-generated method stub
		List<ProductDto> productDtos = new ArrayList<>();
		List<ProductEntity> productEntities = productRepository.findByName(name, pageable).getContent();
		for (ProductEntity productEntity : productEntities) {
			ProductDto productDto = productConverter.toDto(productEntity);
			productDtos.add(productDto);
		}
		return productDtos;
	}

	@Override
	public ProductDto findById(Long id) {
		// TODO Auto-generated method stub
		ProductEntity productEntity = productRepository.findById(id).orElse(null);
		ProductDto productDto = productConverter.toDto(productEntity);
		return productDto;
	}

	@Override
	@Transactional
	public ProductDto insert(ProductDto productDto, MultipartFile file) {
		try {
			File convFile = new File(pathUploadImage + "/" + file.getOriginalFilename());
			FileOutputStream fos = new FileOutputStream(convFile);
			fos.write(file.getBytes());
			fos.close();
		}catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		// TODO Auto-generated method stub
		ProductEntity productEntity = productConverter.toEntity(productDto);
		CategoryEntity categoryEntity = categoryRepository.getById(productDto.getCategory().getCategoryId());
		productEntity.setProductImage(file.getOriginalFilename());
		productEntity.setCategory(categoryEntity);
		productEntity.setFavorite(false);
		productEntity.setStatus(true);
		productEntity = productRepository.save(productEntity);
		productDto = productConverter.toDto(productEntity);
		return productDto;
	}

	@Override
	public List<ProductDto> findListProductNewLimit() {
		// TODO Auto-generated method stub
		List<ProductDto> productDtos = new ArrayList<>();
		List<ProductEntity> productEntities = productRepository.findListProductNew20();
		for (ProductEntity productEntity : productEntities) {
			ProductDto productDto = productConverter.toDto(productEntity);
			productDtos.add(productDto);
		}
		return productDtos;
	}

	@Override
	public List<ProductDto> findTopProductBestSale(UserDto userDto) {
		// TODO Auto-generated method stub
		List<ProductDto> productDtos = new ArrayList<>(); 
		List<Object[]> objects = productRepository.bestSaleProduct20();
		if (objects != null) {
			ArrayList<Integer> listProductIdArrayList = new ArrayList<>();
			for (int i = 0; i < objects.size(); i++) {
				String idProduct = String.valueOf(objects.get(i)[0]);
				listProductIdArrayList.add(Integer.valueOf(idProduct));
			}
			List<ProductEntity> listProducts = productRepository.findByInventoryIds(listProductIdArrayList);
			List<ProductEntity> listProductNew = new ArrayList<>();
			for (ProductEntity product : listProducts) {
				ProductEntity productEntity = new ProductEntity();
				BeanUtils.copyProperties(product, productEntity);
				FavoriteEntity save = favoriteRepository.selectSaves(productEntity.getProductId(), userDto.getUserId());
				if (save != null) {
					productEntity.favorite = true;
				}else {
					productEntity.favorite = false;
				}
				listProductNew.add(productEntity);
			}
			for (ProductEntity productEntity : listProductNew) {
				ProductDto productDto= productConverter.toDto(productEntity);
				productDtos.add(productDto);
			}
		}
		return productDtos;
	}

	@Override
	public List<ProductDto> findProductByCategory(Long id) {
		// TODO Auto-generated method stub
		List<ProductDto> productDtos = new ArrayList<>();
		List<ProductEntity> productEntities = productRepository.listProductByCategory(id);
		for (ProductEntity productEntity : productEntities) {
			ProductDto productDto = productConverter.toDto(productEntity);
			productDtos.add(productDto);
		}
		return productDtos;
	}

	@Override
	public List<CategoryResponse> listCategoryByProductName() {
		// TODO Auto-generated method stub
		List<CategoryResponse> categoryResponses = new ArrayList<>();
		List<Object[]> objects = productRepository.listCategoryByProductName();
		for (int i = 0; i <objects.size();i++ ) {
			CategoryResponse categoryResponse = new CategoryResponse();
			String idCategory = String.valueOf(objects.get(i)[0]);
			categoryResponse.setCategory_id(Long.valueOf(idCategory));
			categoryResponse.setCategory_name(String.valueOf(objects.get(i)[1]));
			String countProduct = String.valueOf(objects.get(i)[2]);
			categoryResponse.setCountProduct(Integer.valueOf(countProduct));
			categoryResponses.add(categoryResponse);
		}
		if (categoryResponses != null) {
			return categoryResponses;
		}
		return null;
	}
}
