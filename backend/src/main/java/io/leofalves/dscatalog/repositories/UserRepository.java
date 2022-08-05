package io.leofalves.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.leofalves.dscatalog.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
