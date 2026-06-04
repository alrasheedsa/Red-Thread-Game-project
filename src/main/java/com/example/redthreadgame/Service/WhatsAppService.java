package com.example.redthreadgame.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WhatsAppService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.number}")
    private String fromNumber;

//    public void sendAllergenWarning() {
//        Twilio.init(accountSid, authToken);
//
//        String message = "";
//
//        Message.creator(
//                new PhoneNumber("whatsapp:" + bbbb),
//                new PhoneNumber(fromNumber),
//                message
//        ).create();
//    }

}
