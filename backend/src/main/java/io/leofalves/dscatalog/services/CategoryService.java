package io.leofalves.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.leofalves.dscatalog.dto.CategoryDto;
import io.leofalves.dscatalog.entities.Category;
import io.leofalves.dscatalog.repositories.CategoryRepository;
import io.leofalves.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public List<CategoryDto> findAll(){
		List<Category> list = categoryRepository.findAll();
		return list.stream().map(c -> new CategoryDto(c)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public CategoryDto findById(Long id) {
		Optional<Category> obj = categoryRepository.findById(id);
		Category category = obj.orElseThrow(() -> new EntityNotFoundException("Entity Not Found"));
		return new CategoryDto(category);
	}

	public CategoryDto insert(CategoryDto dto) {
		Category category = new Category();
		category.setName(dto.getName());
		category = categoryRepository.save(category);
		return new CategoryDto(category);
	}
}
