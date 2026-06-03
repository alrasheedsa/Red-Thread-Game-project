package com.example.redthreadgame.Repository;
import com.example.redthreadgame.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findAdminById(Integer id);
    //for more validation
    Admin findAdminByEmail(String email);
    Admin findAdminByUsername(String username);
}