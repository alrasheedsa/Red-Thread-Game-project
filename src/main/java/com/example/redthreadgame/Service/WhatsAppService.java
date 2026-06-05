package com.example.redthreadgame.Service;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WhatsAppService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.whatsapp.number}")
    private String fromNumber;

    public void sendSessionCode(String phoneNumber, String sessionCode) {
        Twilio.init(accountSid, authToken);

        Message.creator(
                new PhoneNumber("whatsapp:" + phoneNumber),
                new PhoneNumber("whatsapp:" + fromNumber),
                "🕵️ Welcome to Red Thread!\n\n" +
                        "🔑 Your Session Code: " + sessionCode + "\n\n" +
                        "Enter the code and start investigating. 🔍\n\n" +
                        "Good luck, Detective! 🧩"
        ).create();
    }
}
