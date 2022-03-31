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
import io.leofalves.dscatalog.entities.Category;
import io.leofalves.dscatalog.repositories.CategoryRepository;
import io.leofalves.dscatalog.services.exceptions.DatabaseException;
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

	@Transactional
	public CategoryDto update(Long id, CategoryDto dto) {
		try {
			Category category = categoryRepository.getOne(id); // getOne => Não acessa o banco de dados para fazer o SELECT
			category.setName(dto.getName());
			category = categoryRepository.save(category);
			return new CategoryDto(category);
		} catch (javax.persistence.EntityNotFoundException e) {
			throw new EntityNotFoundException("id not found " + id);
		}
	}

	public void delete(Long id) {
		try {
			categoryRepository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e)
		{
			throw new EntityNotFoundException("id not found " + id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}

	public Page<CategoryDto> findAllPaged(PageRequest pageRequest) {
		Page<Category> page = categoryRepository.findAll(pageRequest);
		return page.map(c -> new CategoryDto(c));
	}
}
