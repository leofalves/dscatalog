package io.leofalves.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.leofalves.dscatalog.dto.CategoryDto;
import io.leofalves.dscatalog.entities.Category;
import io.leofalves.dscatalog.repositories.CategoryRepository;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Transactional(readOnly = true)
	public List<CategoryDto> findAll(){
		List<Category> list = categoryRepository.findAll();
		return list.stream().map(c -> new CategoryDto(c)).collect(Collectors.toList());
	}
}
