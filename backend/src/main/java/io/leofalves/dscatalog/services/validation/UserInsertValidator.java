package io.leofalves.dscatalog.services.validation;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import io.leofalves.dscatalog.dto.UserInsertDto;
import io.leofalves.dscatalog.entities.User;
import io.leofalves.dscatalog.repositories.UserRepository;
import io.leofalves.dscatalog.resources.exceptions.FieldMessage;

public class UserInsertValidator implements ConstraintValidator<UserInsertValid, UserInsertDto> {

	@Autowired
	private UserRepository repository;
	
	@Override
	public void initialize(UserInsertValid ann) {
	}

	@Override
	public boolean isValid(UserInsertDto dto, ConstraintValidatorContext context) {

		List<FieldMessage> list = new ArrayList<>();

		User user = repository.findByEmail(dto.getEmail());
		if (user != null) {
			list.add(new FieldMessage("email", "E-mail address already exists"));
		}
		

		// Insere na lista do Beans Validation os erros encontrados e armazenados na lista de FieldMessage 
		for (FieldMessage e : list) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
					.addConstraintViolation();
		}
		return list.isEmpty();
	}
}