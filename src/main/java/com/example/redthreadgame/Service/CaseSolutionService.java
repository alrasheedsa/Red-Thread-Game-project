package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.CaseSolutionIn;
import com.example.redthreadgame.DTO.OUT.CaseSolutionOut;
import com.example.redthreadgame.Model.Case;
import com.example.redthreadgame.Model.CaseSolution;
import com.example.redthreadgame.Repository.CaseSolutionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaseSolutionService {
    private final ModelMapper modelMapper;
    private final CaseSolutionRepository caseSolutionRepository;
    private final CaseService caseService;

    // endpoint  get solution for case for admin
    public CaseSolutionOut getCaseSolution(Integer caseId) {
        return modelMapper.map(checkCaseSolution(caseId), CaseSolutionOut.class);
    }
    //add right solution for cases and case must not have solution
    public void addCaseSolution(Integer caseId, CaseSolutionIn dto) {
       Case c = caseService.checkCase(caseId);
        if (c.getCaseSolution() != null) {
            throw new ApiException("This case already has a solution");
        }
    CaseSolution solution = modelMapper.map(dto, CaseSolution.class);
        solution.setSolutionCase(c);
        caseSolutionRepository.save(solution);

}
    public void updateCaseSolution(Integer caseId, CaseSolutionIn dto) {
        CaseSolution old = checkCaseSolution(caseId);

        old.setJustification(dto.getJustification());
        caseSolutionRepository.save(old);

    }
    public void deleteCaseSolution(Integer caseId) {
        caseSolutionRepository.delete(checkCaseSolution(caseId));

    }
    //---------------------------------------------------END CRED-----------------------------------------------------------------------

    public CaseSolution checkCaseSolution(Integer caseId) {
        CaseSolution solution = caseSolutionRepository.findCaseSolutionById(caseId);
        if (solution == null)
            throw new ApiException("Case solution not found");
        return solution;
    }
}
