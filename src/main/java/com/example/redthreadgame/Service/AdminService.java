package com.example.redthreadgame.Service;
import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.AdminIn;
import com.example.redthreadgame.DTO.OUT.AdminOut;
import com.example.redthreadgame.Model.Admin;
import com.example.redthreadgame.Repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ModelMapper modelMapper;
    private final AdminRepository adminRepository;

    public List<AdminOut> getAllAdmins() {
        List<AdminOut> admins = new ArrayList<>();
        for (Admin a : adminRepository.findAll()) {
            admins.add(modelMapper.map(a, AdminOut.class));
        }
        return admins;
    }
    public void addAdmin(AdminIn dto) {
        if (adminRepository.findAdminByEmail(dto.getEmail()) != null)
            throw new ApiException("Email already exists");
        if (adminRepository.findAdminByUsername(dto.getUsername()) != null)
            throw new ApiException("Username already exists");
        Admin admin = modelMapper.map(dto, Admin.class);
        adminRepository.save(admin);
    }

    public void updateAdmin(Integer id, AdminIn dto) {
        Admin old = checkAdmin(id);
        old.setName(dto.getName());
        old.setUsername(dto.getUsername());
        old.setEmail(dto.getEmail());
        old.setPassword(dto.getPassword());

        adminRepository.save(old);
    }
    public void deleteAdmin(Integer id) {
        adminRepository.delete(checkAdmin(id));
    }
    //---------------------------------------------------END CRED-----------------------------------------------------------------------
//helper method
    public Admin checkAdmin(Integer id) {
        Admin admin = adminRepository.findAdminById(id);
        if (admin == null)
            throw new ApiException("Admin not found");
        return admin;
    }

    public Admin verifyAdmin(Integer adminId, String password) {
        Admin admin = checkAdmin(adminId);
        if (admin.getPassword() == null || !admin.getPassword().equals(password))
            throw new ApiException("Invalid password");
        return admin;
    }

}
