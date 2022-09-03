package io.leofalves.dscatalog.dto;

import io.leofalves.dscatalog.services.validation.UserInsertValid;

@UserInsertValid
public class UserInsertDto extends UserDto{
	private static final long serialVersionUID = 1L;
	
	private String password;

	UserInsertDto() {
		super();
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
