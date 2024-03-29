package io.leofalves.dscatalog.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import io.leofalves.dscatalog.entities.Category;
import io.leofalves.dscatalog.entities.Product;

public class ProductDto implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;	
	
	@Size(min = 5, max = 60, message = "Name must have between 5 and 60 characters")
	@NotBlank(message = "Required field")
	private String name;
	
	@NotBlank(message = "Required field")
	private String description;
	
	@Positive(message = "Price must be a positive value")
	private Double price;
	private String imgUrl;
	
	@PastOrPresent(message = "Date can not be in the future")
	private Instant date;

	List<CategoryDto> categories = new ArrayList<>();
	
	public ProductDto() {		
	}

	public ProductDto(Long id, String name, String description, Double price, String imgUrl, Instant date) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.price = price;
		this.imgUrl = imgUrl;
		this.date = date;
	}
	
	public ProductDto(Product product) {
		this.id = product.getId();
		this.name = product.getName();
		this.description = product.getDescription();
		this.price = product.getPrice();
		this.imgUrl = product.getImgUrl();
		this.date = product.getDate();
	}
	

	public ProductDto(Product product, Set<Category> categories) {
		this(product);
		categories.forEach(cat -> this.categories.add(new CategoryDto(cat)));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public Instant getDate() {
		return date;
	}

	public void setDate(Instant date) {
		this.date = date;
	}

	public List<CategoryDto> getCategories() {
		return categories;
	}
}
