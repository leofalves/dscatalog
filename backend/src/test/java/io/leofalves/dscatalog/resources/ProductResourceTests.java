//package io.leofalves.dscatalog.resources;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import io.leofalves.dscatalog.dto.ProductDto;
//import io.leofalves.dscatalog.services.ProductService;
//import io.leofalves.dscatalog.services.exceptions.DatabaseException;
//import io.leofalves.dscatalog.services.exceptions.EntityNotFoundException;
//import io.leofalves.dscatalog.tests.Factory;
//
//@WebMvcTest(ProductResource.class)
//public class ProductResourceTests {
//	
//	@Autowired
//	private MockMvc mockMvc;
//	
//	@MockBean
//	private ProductService service; // para testes de camada web usar preferencialmente o @MockBean ao invés do @Mock
//	
//	@Autowired
//	private ObjectMapper objectMapper;
//	
//	private PageImpl<ProductDto> page;
//	private ProductDto productDto;
//	private Long existingId;
//	private Long nonExistingId;
//	private Long dependentId;
//	
//	@BeforeEach
//	void setUp() throws Exception {
//		
//		existingId = 1L;
//		nonExistingId = 2L;
//		dependentId = 3L;
//		
//		productDto = Factory.createProductDto();
//		page = new PageImpl<>(List.of(productDto));
//		
//		// Simulando o comportamento do service.findAllPaged
//		when(service.findAllPaged(any())).thenReturn(page);
//		
//		//Simulando o service.findById
//		when(service.findById(existingId)).thenReturn(productDto);
//		when(service.findById(nonExistingId)).thenThrow(EntityNotFoundException.class);
//		
//		//Simulando o service.update
//		when(service.update(eq(existingId), any())).thenReturn(productDto);
//		when(service.update(eq(nonExistingId),any())).thenThrow(EntityNotFoundException.class);
//		
//		doNothing().when(service).delete(existingId);
//		doThrow(EntityNotFoundException.class).when(service).delete(nonExistingId);
//		doThrow(DatabaseException.class).when(service).delete(dependentId);
//		
//		when(service.insert(any())).thenReturn(productDto);
//	}
//	
//	
//	@Test
//	public void deleteShouldReturnBadRequestWhenDependentId() throws Exception {
//		ResultActions result = 
//				mockMvc.perform(delete("/products/{id}", dependentId)
//						.accept(MediaType.APPLICATION_JSON));
//						
//		result.andExpect(status().isBadRequest());
//	}
//	
//	@Test
//	public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
//		ResultActions result = 
//				mockMvc.perform(delete("/products/{id}", nonExistingId)
//						.accept(MediaType.APPLICATION_JSON));
//						
//		result.andExpect(status().isNotFound());
//	}
//	
//	@Test
//	public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
//		ResultActions result = 
//				mockMvc.perform(delete("/products/{id}", existingId)
//						.accept(MediaType.APPLICATION_JSON));
//						
//		result.andExpect(status().isNoContent());
//	}
//	
//	
//	@Test
//	public void insertShouldReturnProductDtoCreated() throws Exception {
//		String jsonBody = objectMapper.writeValueAsString(productDto);
//		
//		ResultActions result = 
//				mockMvc.perform(post("/products")
//						.content(jsonBody)
//						.contentType(MediaType.APPLICATION_JSON)
//						.accept(MediaType.APPLICATION_JSON));
//		
//		result.andExpect(status().isCreated());
//		result.andExpect(jsonPath("$.id").exists());
//		result.andExpect(jsonPath("$.name").exists());
//		result.andExpect(jsonPath("$.description").exists());		
//	}
//	
//	
//	@Test
//	public void updateShoulReturnNotFoundWhenIdDoesNotExists() throws Exception {
//		String jsonBody = objectMapper.writeValueAsString(productDto);
//		
//		ResultActions result = 
//				mockMvc.perform(put("/products/{id}", nonExistingId)
//						.content(jsonBody)
//						.contentType(MediaType.APPLICATION_JSON)
//						.accept(MediaType.APPLICATION_JSON));
//		
//		result.andExpect(status().isNotFound());
//		
//	}
//	
//	@Test
//	public void updateShouldReturnProductDtoWhenIdExists() throws Exception {
//		
//		String jsonBody = objectMapper.writeValueAsString(productDto);
//		
//		ResultActions result = 
//				mockMvc.perform(put("/products/{id}", existingId)
//						.content(jsonBody)
//						.contentType(MediaType.APPLICATION_JSON)
//						.accept(MediaType.APPLICATION_JSON));
//		
//		result.andExpect(status().isOk());
//		result.andExpect(jsonPath("$.id").exists());
//		result.andExpect(jsonPath("$.name").exists());
//		result.andExpect(jsonPath("$.description").exists());
//	}
//	
//	@Test
//	public void findAllShouldReturnPage() throws Exception {
//		
//		ResultActions result = 
//				mockMvc.perform(get("/products")
//						.accept(MediaType.APPLICATION_JSON));
//		
//		result.andExpect(status().isOk());
//		
//		/*
//		Encadeando as chamadas
//		mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
//		*/
//	}
//	
//	@Test
//	public void findByIdShouldReturnProductWhenIdExists() throws Exception {
//		ResultActions result = 
//				mockMvc.perform(get("/products/{id}", existingId)
//						.accept(MediaType.APPLICATION_JSON));
//		
//		result.andExpect(status().isOk());
//		result.andExpect(jsonPath("$.id").exists());
//		result.andExpect(jsonPath("$.name").exists());
//		result.andExpect(jsonPath("$.description").exists());
//	}
//	
//	@Test
//	public void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
//		ResultActions result = 
//				mockMvc.perform(get("/products/{id}", nonExistingId)
//						.accept(MediaType.APPLICATION_JSON));
//		
//		result.andExpect(status().isNotFound());
//	}
//
//}
