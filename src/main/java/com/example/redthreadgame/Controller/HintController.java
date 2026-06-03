package com.example.redthreadgame.Controller;
import com.example.redthreadgame.Api.ApiResponse;
import com.example.redthreadgame.Service.HintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/hint")
@RestController
@RequiredArgsConstructor
public class HintController {

    private final HintService hintService;

//    @GetMapping("/get-by-session/{gameSessionId}")
//    public ResponseEntity<?> getHintsByGameSession(@PathVariable Integer gameSessionId) {
//        return ResponseEntity.status(200).body(hintService.getHintsByGameSession(gameSessionId));
//    }
//
//    @PostMapping("/add/{gameSessionId}")
//    public ResponseEntity<?> addHint(@PathVariable Integer gameSessionId, @RequestBody @Valid HintIn dto) {
//        hintService.addHint(gameSessionId, dto);
//        return ResponseEntity.status(200).body(new ApiResponse("Hint added successfully"));
//    }

    @DeleteMapping("/delete/{hintId}")
    public ResponseEntity<?> deleteHint(@PathVariable Integer hintId) {
        hintService.deleteHint(hintId);
        return ResponseEntity.status(200).body(new ApiResponse("Hint deleted successfully"));
    }
}
