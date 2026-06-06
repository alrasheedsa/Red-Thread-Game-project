package com.example.redthreadgame.Service;

import com.example.redthreadgame.Api.ApiException;
import com.example.redthreadgame.DTO.IN.GameSessionIn;
import com.example.redthreadgame.DTO.OUT.GameSessionOut;
import com.example.redthreadgame.Enums.SessionPlayerRole;
import com.example.redthreadgame.Enums.SessionPlayerStatus;
import com.example.redthreadgame.Model.*;
import com.example.redthreadgame.Repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.redthreadgame.Enums.GameSessionStatusType.*;

@Service
@RequiredArgsConstructor
public class GameSessionService {

    private final ModelMapper modelMapper;
    private final GameSessionRepository gameSessionRepository;
    private final CaseRepository caseRepository;
    private final PlayerRepository playerRepository;
    private final InvitationRepository invitationRepository;
    private final SessionPlayerRepository sessionPlayerRepository;
    private final EmailService emailService;


    //BASIC CRUD
    public List<GameSessionOut> getAllGameSessions(){
        List<GameSessionOut> gameSessions = new ArrayList<>();
        for(GameSession g: gameSessionRepository.findAll()){
            gameSessions.add(modelMapper.map(g, GameSessionOut.class));
        }
        return gameSessions;
    }

    public void addGameSession(Integer caseId,Integer playerId,GameSessionIn gameSessionIn){
        Case sessionCase = checkCase(caseId);
        Player player = checkPlayer(playerId);
        String code = generateCode();

        GameSession gameSession = modelMapper.map(gameSessionIn, GameSession.class);
        gameSession.setSessionCode(gameSession.getIsPrivate() ? code : null);
        gameSession.setSessionCase(sessionCase);
        gameSession.setOwner(player);
        gameSession.setStatus(PENDING);
        if(sessionCase.getDifficulty().equalsIgnoreCase("hard"))
            gameSession.setScore(100);
        else if (sessionCase.getDifficulty().equalsIgnoreCase("medium"))
            gameSession.setScore(80);
        else gameSession.setScore(60);

        gameSessionRepository.save(gameSession);

        // add owner as session player
        SessionPlayer sessionPlayer = new SessionPlayer(null, SessionPlayerRole.HOST, SessionPlayerStatus.JOINED, LocalDateTime.now(), gameSession, player);
        sessionPlayerRepository.save(sessionPlayer);
    }

    public void updateGameSession(Integer id, GameSessionIn gameSessionIn){
        GameSession oldGameSession = checkGameSession(id);

        oldGameSession.setPlayersCount(gameSessionIn.getPlayersCount());
        oldGameSession.setIsPrivate(gameSessionIn.getIsPrivate());

        gameSessionRepository.save(oldGameSession);
    }

    public void deleteGameSession(Integer id){
        GameSession gameSession = checkGameSession(id);
        gameSessionRepository.delete(gameSession);
    }


    //EXTRA ENDPOINTS
    public List<GameSessionOut> getPublicGameSessions(){
        List<GameSessionOut> gameSessions = new ArrayList<>();
        for(GameSession g: gameSessionRepository.findAllByIsPrivateFalse()){
            gameSessions.add(modelMapper.map(g, GameSessionOut.class));
        }
        return gameSessions;
    }

    public void joinPublicGameSession(Integer gameSessionId, Integer playerId) {
        Player player = checkPlayer(playerId);
        GameSession gameSession = checkGameSession(gameSessionId);

        // check session is public
        if (gameSession.getIsPrivate())
            throw new ApiException("This session is private");

        validateJoin(player, gameSession);

        joinMember(player, gameSession);
    }

    public void joinPrivateGameSession(String sessionCode, Integer playerId) {
        // check player
        Player player = checkPlayer(playerId);

        // check session code
        GameSession gameSession = gameSessionRepository.findBySessionCode(sessionCode);
        if (gameSession == null)
            throw new ApiException("Invalid session code");

        // check invitation
        Invitation invitation = invitationRepository.findByGameSessionIdAndPlayerId(gameSession.getId(), playerId);
        if (invitation == null)
            throw new ApiException("You are not invited to this game session");

        validateJoin(player, gameSession);

        // join
        joinMember(player, gameSession);

    }

    public void leaveSession(Integer gameSessionId, Integer playerId){
        Player player = checkPlayer(playerId);
        GameSession gameSession = checkGameSession(gameSessionId);

        // check session status
        if(gameSession.getStatus() != PENDING)
            throw new ApiException("You can only leave a session that is pending");

        // check if player is in session
        SessionPlayer sessionPlayer = sessionPlayerRepository.findByGameSessionAndPlayer(gameSession, player);
        if(sessionPlayer == null)
            throw new ApiException("You are not in this session");

        // check if player is owner
        if(gameSession.getOwner().getId().equals(playerId))
            throw new ApiException("Owner cannot leave the session");

        sessionPlayer.setStatus(SessionPlayerStatus.LEFT);
        sessionPlayerRepository.save(sessionPlayer);

        notifyOwnerPlayerLeft(player, gameSession);
    }

    public void cancelSession(Integer gameSessionId, Integer playerId){
        GameSession gameSession = checkGameSession(gameSessionId);

        // check owner
        if(!gameSession.getOwner().getId().equals(playerId))
            throw new ApiException("Only the owner can cancel the session");

        // check status
        if(gameSession.getStatus() != PENDING)
            throw new ApiException("You can only cancel a pending session");

        gameSession.setStatus(CANCELLED);
        gameSessionRepository.save(gameSession);

        // notify players
        notifyPlayerSessionCancelled(gameSession);
    }

    public GameSessionOut getLobby(Integer gameSessionId){
        GameSession gameSession = checkGameSession(gameSessionId);
        return modelMapper.map(gameSession, GameSessionOut.class);
    }

    public List<GameSessionOut> getPendingGameSessions(){
        List<GameSessionOut> gameSessions = new ArrayList<>();
        for(GameSession g: gameSessionRepository.findAllByStatus(PENDING)){
            gameSessions.add(modelMapper.map(g, GameSessionOut.class));
        }
        return gameSessions;
    }

    public List<GameSessionOut> getPublicGameSessionsByCase(Integer caseId){
        List<GameSessionOut> gameSessions = new ArrayList<>();
        for(GameSession g: gameSessionRepository.findAllByIsPrivateFalseAndSessionCaseIdAndStatus(caseId, PENDING)){
            gameSessions.add(modelMapper.map(g, GameSessionOut.class));
        }
        return gameSessions;
    }


    //HELPER METHODS
    private GameSession checkGameSession(Integer id){
        GameSession gameSession = gameSessionRepository.findGameSessionById(id);
        if(gameSession == null) throw new ApiException("Game Session not found"); //check game session

        return gameSession;
    }

    private Case checkCase(Integer id){
        Case sessionCase = caseRepository.findCaseById(id);
        if(sessionCase == null) throw new ApiException("Case not found"); //check case

        return sessionCase;
    }

    private Player checkPlayer(Integer id){
        Player player = playerRepository.findPlayerById(id);
        if(player == null) throw new ApiException("player not found"); //check player

        return player;
    }

    private String generateCode(){
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        } while (gameSessionRepository.existsBySessionCode(code));
        return code;
    }

    private void joinMember(Player player, GameSession gameSession){
        SessionPlayer sessionPlayer = new SessionPlayer(null, SessionPlayerRole.MEMBER, SessionPlayerStatus.JOINED, LocalDateTime.now(), gameSession, player);

        sessionPlayerRepository.save(sessionPlayer);
        notifyOwnerPlayerJoined(player, gameSession);
        checkAutoStart(gameSession);
    }

    private void checkAutoStart(GameSession gameSession){
        int currentPlayers = sessionPlayerRepository.countByGameSessionAndStatus(gameSession, SessionPlayerStatus.JOINED);
        if(currentPlayers >= gameSession.getPlayersCount()){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    gameSession.setStatus(IN_PROGRESS);
                    gameSession.setStartedAt(LocalDateTime.now());
                    gameSessionRepository.save(gameSession);
                    notifyPlayersSessionStarted(gameSession);
                }
            }, 10000);
        }
    }

    private void validateJoin(Player player, GameSession gameSession){
        // check if player is already in a pending or in progress session
        if(sessionPlayerRepository.existsByPlayerIdAndGameSession_StatusIn(player.getId(), List.of(PENDING, IN_PROGRESS)))
            throw new ApiException("You are already in an active session");

        // check session status
        if(gameSession.getStatus() != PENDING)
            throw new ApiException("Session is not available to join");

        // check if already joined
        if(sessionPlayerRepository.existsByGameSessionAndPlayer(gameSession, player))
            throw new ApiException("You already joined this session");

        // check players count
        if(sessionPlayerRepository.countByGameSessionAndStatus(gameSession, SessionPlayerStatus.JOINED) >= gameSession.getPlayersCount())
            throw new ApiException("Game session is full");

        // check if player already played this case
        if(sessionPlayerRepository.existsByPlayerIdAndGameSession_SessionCase_Id(player.getId(), gameSession.getSessionCase().getId()))
            throw new ApiException("You already played this case before");
    }

    private void notifyPlayersSessionStarted(GameSession gameSession) {
        List<SessionPlayer> sessionPlayers =
                sessionPlayerRepository.findAllByGameSessionId(gameSession.getId());

        for (SessionPlayer sp : sessionPlayers) {
            emailService.send(
                    sp.getPlayer().getEmail(),
                    "🕵️ Investigation Started! Case Session #" + gameSession.getId(),
                    "Hey Detective " + sp.getPlayer().getName() + "! 🔍\n\n" +
                            "The investigation is officially underway! 🚨\n\n" +
                            "📂 Session Details:\n" +
                            "🔑 Session Code: " + gameSession.getSessionCode() + "\n" +
                            "👥 Detectives: " + gameSession.getPlayersCount() + "\n" +
                            "⏰ Started At: " + gameSession.getStartedAt() + "\n\n" +
                            "🧩 Gather the clues.\n" +
                            "🗣️ Question the witnesses.\n" +
                            "😈 Find the culprit before anyone else!\n\n" +
                            "Good luck, Detective! 🕵️‍♂️\n\n" +
                            "🧵 Red Thread Team"
            );
        }
    }

    private void notifyOwnerPlayerJoined(Player player, GameSession gameSession) {
        emailService.send(
                gameSession.getOwner().getEmail(),
                "🎉 New Detective Joined Your Session!",
                "Hey Chief Detective " + gameSession.getOwner().getName() + "! 🕵️\n\n" +
                        "A detective has accepted the assignment and joined your case. \uD83D\uDCC2\n\n" +
                        "👤 Detective:\n" +
                        "📛 Name: " + player.getName() + "\n" +
                        "🏷️ Username: " + player.getUsername() + "\n\n" +
                        "💪 Your squad is getting stronger.\n" +
                        "🔎 Time to solve this mystery together!\n\n" +
                        "🧵 Red Thread Team"
        );
    }

    private void notifyOwnerPlayerLeft(Player player, GameSession gameSession) {
        emailService.send(
                gameSession.getOwner().getEmail(),
                "👋 Detective Left Your Session",
                "Hey Chief Detective " + gameSession.getOwner().getName() + "! 🕵️\n\n" +
                        "One of your detectives has left the investigation. 🚪\n\n" +
                        "👤 Detective Info:\n" +
                        "📛 Name: " + player.getName() + "\n" +
                        "🏷️ Username: " + player.getUsername() + "\n\n" +
                        "🔍 Don't worry! The mystery is still waiting to be solved.\n\n" +
                        "🧵 Red Thread Team"
        );
    }

    private void notifyPlayerSessionCancelled(GameSession gameSession) {
        List<SessionPlayer> sessionPlayers =
                sessionPlayerRepository.findAllByGameSessionId(gameSession.getId());

        for (SessionPlayer sp : sessionPlayers) {
            emailService.send(
                    sp.getPlayer().getEmail(),
                    "❌ Investigation Cancelled",
                    "Hey Detective " + sp.getPlayer().getName() + "! 🕵️\n\n" +
                            "The investigation session you joined has been cancelled. 📂❌\n\n" +
                            "👀 Looks like this mystery will remain unsolved... for now.\n\n" +
                            "🔍 Keep an eye out for the next case!\n\n" +
                            "🧵 Red Thread Team"
            );
        }
    }
}
