package io.leofalves.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.leofalves.dscatalog.dto.CategoryDto;
import io.leofalves.dscatalog.dto.ProductDto;
import io.leofalves.dscatalog.entities.Category;
import io.leofalves.dscatalog.entities.Product;
import io.leofalves.dscatalog.repositories.CategoryRepository;
import io.leofalves.dscatalog.repositories.ProductRepository;
import io.leofalves.dscatalog.services.exceptions.DatabaseException;
import io.leofalves.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public List<ProductDto> findAll(){
		List<Product> list = productRepository.findAll();
		return list.stream().map(c -> new ProductDto(c)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public ProductDto findById(Long id) {
		Optional<Product> obj = productRepository.findById(id);
		Product product = obj.orElseThrow(() -> new EntityNotFoundException("Entity Not Found"));
		return new ProductDto(product, product.getCategories());
	}

	@Transactional
	public ProductDto insert(ProductDto dto) {
		Product product = new Product();
		copyDtoToEntity(dto, product);
		product = productRepository.save(product);
		return new ProductDto(product);
	}

	@Transactional
	public ProductDto update(Long id, ProductDto dto) {
		try {
			Product product = productRepository.getOne(id); // getOne => Não acessa o banco de dados para fazer o SELECT
			copyDtoToEntity(dto, product);
			product = productRepository.save(product);
			return new ProductDto(product);
		} catch (javax.persistence.EntityNotFoundException e) {
			throw new EntityNotFoundException("id not found " + id);
		}
	}

	public void delete(Long id) {
		try {
			productRepository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e)
		{
			throw new EntityNotFoundException("id not found " + id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}

	public Page<ProductDto> findAllPaged(PageRequest pageRequest) {
		Page<Product> page = productRepository.findAll(pageRequest);
		return page.map(c -> new ProductDto(c));
	}
	
	private void copyDtoToEntity(ProductDto dto, Product product) {
		product.setName(dto.getName());
		product.setDescription(dto.getDescription());
		product.setDate(dto.getDate());
		product.setImgUrl(dto.getImgUrl());
		product.setPrice(dto.getPrice());
		
		product.getCategories().clear();
		for (CategoryDto catDto : dto.getCategories()) {
			Category category = categoryRepository.getOne(catDto.getId());
			product.getCategories().add(category);
		}
	}	
}
