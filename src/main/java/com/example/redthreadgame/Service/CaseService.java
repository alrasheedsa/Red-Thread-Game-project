package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.CaseIn;
import com.example.redthreadgame.DTO.OUT.CaseOut;
import com.example.redthreadgame.Model.Admin;
import com.example.redthreadgame.Model.Case;
import com.example.redthreadgame.Repository.AdminRepository;
import com.example.redthreadgame.Repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor

public class CaseService {

    private final ModelMapper modelMapper;
    private final CaseRepository caseRepository;
     private final AdminService adminService;

    public List<CaseOut> getAllCases() {
        List<CaseOut> cases = new ArrayList<>();
        for (Case c : caseRepository.findAll()) {
            cases.add(modelMapper.map(c, CaseOut.class));
        }
        return cases;

    }
    public void updateCase(Integer adminId, String password, Integer caseId, CaseIn dto) {
        adminService.verifyAdmin(adminId, password);
        Case old = checkCase(adminId);
        old.setTitle(dto.getTitle());
        old.setScenario(dto.getScenario());
        old.setDifficulty(dto.getDifficulty());


        caseRepository.save(old);
    }
    public void deleteCase(Integer adminId, String password, Integer caseId) {
        adminService.verifyAdmin(adminId, password);
        caseRepository.delete(checkCase(caseId));
    }
    //---------------------------------------------------END CRED-----------------------------------------------------------------------

    //endpoint admin publish case
public void publishCase(Integer adminId, String password, Integer caseId){
    adminService.verifyAdmin(adminId, password);
    Case c = checkCase(caseId);
    if (c.getStatus().equals("PUBLISHED"))
        throw new ApiException("Case is already published");
    c.setStatus("PUBLISHED");

    caseRepository.save(c);
}

//move to derft by admin
public void moveCaseToDraft(Integer adminId, String password, Integer caseId) {
    adminService.verifyAdmin(adminId,password );
    Case c = checkCase(caseId);
    if (c.getStatus().equals("DRAFT"))
        throw new ApiException("Case is already in DRAFT");
    c.setStatus("DRAFT");
    caseRepository.save(c);
}
//players show case
public List<CaseOut> getPublishedCases() {
    List<CaseOut> cases = new ArrayList<>();
    for (Case c : caseRepository.findCasesByStatus("PUBLISHED")) {
        cases.add(modelMapper.map(c, CaseOut.class));
    }
    return cases;
}
//helper method
    public Case checkCase(Integer id) {
        Case c = caseRepository.findCaseById(id);
        if (c == null)
            throw new ApiException("Case not found");
        return c;

    }

}

