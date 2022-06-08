package io.leofalves.dscatalog.resources;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import io.leofalves.dscatalog.dto.ProductDto;
import io.leofalves.dscatalog.services.ProductService;
import io.leofalves.dscatalog.tests.Factory;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service; // para testes de camada web usar preferencialmente o @MockBean ao invés do @Mock
	
	private PageImpl<ProductDto> page;
	private ProductDto productDto;
	
	@BeforeEach
	void setUp() throws Exception {
		
		productDto = Factory.createProductDto();
		page = new PageImpl<>(List.of(productDto));
		
		when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		mockMvc.perform(get("/products")).andExpect(status().isOk());
	}

}
