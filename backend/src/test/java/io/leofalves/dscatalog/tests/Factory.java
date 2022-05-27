package io.leofalves.dscatalog.tests;

import java.time.Instant;

import io.leofalves.dscatalog.dto.ProductDto;
import io.leofalves.dscatalog.entities.Category;
import io.leofalves.dscatalog.entities.Product;

public class Factory {
	
	public static Product createProduct() {
		Product product = new Product(1L, "Phone", "Good Phone", 800.0, "http://img.com/img.png", Instant.now());
		product.getCategories().add(createCategory());
		return product;		
	}
	
	public static ProductDto createProductDto() {
		Product prod = createProduct();
		return new ProductDto(prod, prod.getCategories());
	}
	
	public static Category createCategory() {
		return new Category(2L, "Electronics");
	}

}
