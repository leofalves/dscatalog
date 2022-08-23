package io.leofalves.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.leofalves.dscatalog.dto.RoleDto;
import io.leofalves.dscatalog.dto.UserDto;
import io.leofalves.dscatalog.dto.UserInsertDto;
import io.leofalves.dscatalog.dto.UserUpdateDto;
import io.leofalves.dscatalog.entities.Role;
import io.leofalves.dscatalog.entities.User;
import io.leofalves.dscatalog.repositories.RoleRepository;
import io.leofalves.dscatalog.repositories.UserRepository;
import io.leofalves.dscatalog.services.exceptions.DatabaseException;
import io.leofalves.dscatalog.services.exceptions.EntityNotFoundException;

@Service
public class UserService {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Transactional(readOnly = true)
	public List<UserDto> findAll(){
		List<User> list = repository.findAll();
		return list.stream().map(c -> new UserDto(c)).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public UserDto findById(Long id) {
		Optional<User> obj = repository.findById(id);
		User user = obj.orElseThrow(() -> new EntityNotFoundException("Entity Not Found"));
		return new UserDto(user);
	}

	@Transactional
	public UserDto insert(UserInsertDto dto) {
		User user = new User();
		copyDtoToEntity(dto, user);
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		user = repository.save(user);
		return new UserDto(user);
	}

	@Transactional
	public UserDto update(Long id, UserUpdateDto dto) {
		try {
			User user = repository.getOne(id); // getOne => Não acessa o banco de dados para fazer o SELECT
			copyDtoToEntity(dto, user);
			user = repository.save(user);
			return new UserDto(user);
		} catch (javax.persistence.EntityNotFoundException e) {
			throw new EntityNotFoundException("id not found " + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}
		catch (EmptyResultDataAccessException e)
		{
			throw new EntityNotFoundException("id not found " + id);
		}
		catch (DataIntegrityViolationException e) {
			throw new DatabaseException("Integrity violation");
		}
	}

	public Page<UserDto> findAllPaged(Pageable pageable) {
		Page<User> page = repository.findAll(pageable);
		return page.map(c -> new UserDto(c));
	}
	
	private void copyDtoToEntity(UserDto dto, User entity) {
		entity.setFirstName(dto.getFirstName()); 
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());

		entity.getRoles().clear();
		for (RoleDto roleDto : dto.getRoles()) {
			Role role = roleRepository.getOne(roleDto.getId());
			entity.getRoles().add(role);
		}
	}	
}
