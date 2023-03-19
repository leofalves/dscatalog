package io.leofalves.dscatalog.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.leofalves.dscatalog.dto.ProductDto;
import io.leofalves.dscatalog.entities.Category;
import io.leofalves.dscatalog.entities.Product;
import io.leofalves.dscatalog.repositories.CategoryRepository;
import io.leofalves.dscatalog.repositories.ProductRepository;
import io.leofalves.dscatalog.services.exceptions.DatabaseException;
import io.leofalves.dscatalog.services.exceptions.EntityNotFoundException;
import io.leofalves.dscatalog.tests.Factory;

// Para testes de classes services
@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	private long existingId;
	private long nonExistingId;
	private long dependentId;
	private Product product;
	private Category category;
	private ProductDto productDto;
	private PageImpl<Product> page;

	@InjectMocks
	private ProductService service;

	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;

	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;

		product = Factory.createProduct();
		category = Factory.createCategory();
		productDto = Factory.createProductDto();
		page = new PageImpl<>(List.of(product));

		// Mock do repository para testes do método deleteById
		doNothing().when(repository).deleteById(existingId); // Mockar o método deleteById para retornar nada, quando o
																// Id existir
		doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId); // Mockar o método
																									// deleteById para
																									// retornar
																									// exception, quando
																									// o Id não existir
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId); // Mockar o método
																									// deleteById para
																									// retornar
																									// exception, quando
																									// o Id for
																									// referencial

		// Mock do repository para o findAll
		Mockito.when(repository.findAll((Pageable) any())).thenReturn(page);

		// Mock do repository para o save
		Mockito.when(repository.save(any())).thenReturn(product);

		// Mokc para o findById
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
		Mockito.when(repository.search(any(), any(), any())).thenReturn(page);
		
		// Mock para o update
		Mockito.when(repository.getOne(existingId)).thenReturn(product);
		Mockito.when(repository.getOne(nonExistingId)).thenThrow(javax.persistence.EntityNotFoundException.class);
		Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(javax.persistence.EntityNotFoundException.class);

	}

	@Test
	public void updateShouldThrowEntityNotFoundExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			service.update(nonExistingId, productDto);
		});
	}
	
	@Test
	public void updateShouldUpdateProductDtoWhenIdExists() {
			
		ProductDto result = service.update(existingId, productDto);		
		
		Assertions.assertNotNull(result);		
		Mockito.verify(repository).save(product);
		
	}
	
	@Test
	public void findByIdShouldThrowEntityNotFoundExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});

		Mockito.verify(repository).findById(nonExistingId);
	}

	@Test
	public void findByIdShouldReturnProductDtoWhenIdExists() {
		ProductDto productDto = service.findById(existingId);

		Assertions.assertNotNull(productDto);
		Mockito.verify(repository).findById(existingId);
	}

	@Test
	public void findAlldShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<ProductDto> result = service.findAllPaged(0L, "", pageable);

		Assertions.assertNotNull(result);
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});

		// Verifica se chama 1 vez o método deleteById com o existintID
		verify(repository, times(1)).deleteById(dependentId);
	}

	@Test
	public void deleteShouldThrowEntityNotFoundExceptionExceptionWhenIdNotExists() {
		Assertions.assertThrows(EntityNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

		// Verifica se chama 1 vez o método deleteById com o existintID
		verify(repository, times(1)).deleteById(nonExistingId);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});

		// Verifica se chama 1 vez o método deleteById com o existintID
		verify(repository, times(1)).deleteById(existingId);
	}
}