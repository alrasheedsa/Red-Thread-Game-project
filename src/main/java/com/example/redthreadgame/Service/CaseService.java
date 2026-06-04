package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.CaseIn;
import com.example.redthreadgame.DTO.OUT.CaseOut;
import com.example.redthreadgame.Model.Admin;
import com.example.redthreadgame.Model.Case;
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
    public void addCase(Integer adminId, CaseIn dto) {
        adminService.checkAdmin(adminId);
        Case c = modelMapper.map(dto, Case.class);
        c.setStatus("DRAFT");
        caseRepository.save(c);
    }



    public void updateCase(Integer id, CaseIn dto) {
        Case old = checkCase(id);
        old.setTitle(dto.getTitle());
        old.setScenario(dto.getScenario());
        old.setDifficulty(dto.getDifficulty());


        caseRepository.save(old);
    }
    public void deleteCase(Integer id) {

        caseRepository.delete(checkCase(id));

    }

    //endpoint admin publish case
public void publishCase(Integer id){
    Case c = checkCase(id);
    if (c.getStatus().equals("PUBLISHED"))
        throw new ApiException("Case is already published");
    c.setStatus("PUBLISHED");

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
 // for ai to genarte



//helper method
    public Case checkCase(Integer id) {

        Case c = caseRepository.findCaseById(id);

        if (c == null) throw new ApiException("Case not found");

        return c;

    }

}

