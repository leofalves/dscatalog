package io.leofalves.dscatalog.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.leofalves.dscatalog.repositories.ProductRepository;
import io.leofalves.dscatalog.services.exceptions.DatabaseException;
import io.leofalves.dscatalog.services.exceptions.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	private long existingId;
	private long nonExistingId;
	private long dependentId;

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;

	@BeforeEach
	void setUp() throws Exception{
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		
		// Mockar o método deleteById para retornar nada, quando o Id existir
		doNothing().when(repository).deleteById(existingId);
		
		// Mockar o método deleteById para retornar exception, quando o Id não existir
		doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);

		// Mockar o método deleteById para retornar exception, quando o Id for referencial
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

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