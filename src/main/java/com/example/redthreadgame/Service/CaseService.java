package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.CaseIn;
import com.example.redthreadgame.DTO.OUT.CaseOut;
import com.example.redthreadgame.Enums.GameSessionStatusType;
import com.example.redthreadgame.Model.*;
import com.example.redthreadgame.Repository.AdminRepository;
import com.example.redthreadgame.Repository.CaseRepository;
import com.example.redthreadgame.Repository.PlayerRepository;
import com.example.redthreadgame.Repository.SessionPlayerRepository;
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
    private final PlayerRepository playerRepository;
    private final SessionPlayerRepository sessionPlayerRepository;

    public List<CaseOut> getAllCases() {
        List<CaseOut> cases = new ArrayList<>();
        for (Case c : caseRepository.findAll()) {
            cases.add(modelMapper.map(c, CaseOut.class));
        }
        return cases;

    }
    public void updateCase(Integer adminId, String password, Integer caseId, CaseIn dto) {
        adminService.verifyAdmin(adminId, password);
        Case old = checkCase(caseId);
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

//casse more group lost
public CaseOut getMostLost() {
    List<Case> publishedCases = caseRepository.findCasesByStatus("PUBLISHED");
    if (publishedCases.isEmpty())
        throw new ApiException("No published cases available");

    Case hardestCase = null;
    int maxLosses = -1;

    for (Case c : publishedCases) {
        int losses = 0;
        for (GameSession session : c.getGameSessions()) {
            if (session.getStatus() == GameSessionStatusType.LOST) {
                losses++;
            }
        }
        if (losses > maxLosses) {
            maxLosses = losses;
            hardestCase = c;
        }
    }

    if (hardestCase == null || maxLosses == 0)
        throw new ApiException("No data available yet");

    return modelMapper.map(hardestCase, CaseOut.class);
}

// case most group won
public CaseOut getMostWon() {
    List<Case> publishedCases = caseRepository.findCasesByStatus("PUBLISHED");
    if (publishedCases.isEmpty())
        throw new ApiException("No published cases available");

    Case easiestCase = null;
    int maxWins = -1;

    for (Case c : publishedCases) {
        int wins = 0;
        for (GameSession session : c.getGameSessions()) {
            if (session.getStatus() == GameSessionStatusType.WON) {
                wins++;
            }
        }
        if (wins > maxWins) {
            maxWins = wins;
            easiestCase = c;
        }
    }

    if (easiestCase == null || maxWins == 0)
        throw new ApiException("No data available yet");

    return modelMapper.map(easiestCase, CaseOut.class);
}

//cases that not played by player
public List<CaseOut> getNotPlayedCases(Integer playerId) {
    Player player = playerRepository.findPlayerById(playerId);
    if (player == null) throw new ApiException("Player not found");

    // كل القضايا المنشورة
    List<Case> publishedCases = caseRepository.findCasesByStatus("PUBLISHED");

    // القضايا اللي لعبها اللاعب
    List<Integer> playedCaseIds = new ArrayList<>();
    for (SessionPlayer s : sessionPlayerRepository.findAllByPlayerId(playerId)) {
        if (s.getGameSession().getSessionCase() != null) {
            playedCaseIds.add(s.getGameSession().getSessionCase().getId());
        }
    }

    // حذف اللي لعبها
    List<CaseOut> notPlayed = new ArrayList<>();
    for (Case c : publishedCases) {
        if (!playedCaseIds.contains(c.getId())) {
            notPlayed.add(modelMapper.map(c, CaseOut.class));
        }
    }

    if (notPlayed.isEmpty())
        throw new ApiException("You have played all available cases");

    return notPlayed;
}

//helper method
    public Case checkCase(Integer id) {
        Case c = caseRepository.findCaseById(id);
        if (c == null)
            throw new ApiException("Case not found");
        return c;

    }

}

