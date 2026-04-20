package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.config.websocket.OutputMessage;
import com.sistemasdistr.basico.dto.Message;
import com.sistemasdistr.basico.dto.UserDTO;
import com.sistemasdistr.basico.model.User;
import com.sistemasdistr.basico.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Controller
public class AppWebSocketController {

    @GetMapping("/mensajes")
    public String mensajes(ModelMap interfazConPantalla){
        return "mensajes";
    }

    @GetMapping("/mensajesRegistro")
    public String mensajesRegistro(ModelMap interfazConPantalla){
        return "mensajes";
    }

    public UserService userService;

    @MessageMapping("/chat")
    @SendTo("/topic/message")
    public OutputMessage send(Message message) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        User userfrom = userService.repo.findUserByUsername(message.getFrom());
        User userto = userService.repo.findUserByUsername(message.getTo());

        Cipher aesCipher = Cipher.getInstance("AES");

        byte[] decodedKey = Base64.getDecoder().decode(userfrom.getPrivateKey());
        SecretKey originalPrivateKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            aesCipher.init(Cipher.ENCRYPT_MODE, originalPrivateKey);

        byte[] encryptedMessage = aesCipher.doFinal(message.getText().getBytes());

        Cipher rsaCipher = Cipher.getInstance("RSA");

        byte[] decodedPublicKey = Base64.getDecoder().decode(userfrom.getPublickey());
        SecretKey originalPublicKey = new SecretKeySpec(decodedPublicKey, 0, decodedPublicKey.length, "AES");
            rsaCipher.init(Cipher.ENCRYPT_MODE, originalPublicKey);

        byte[] encryptedMessagefinal = aesCipher.doFinal(message.getText().getBytes());

        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time, message.getTo());
    }

    @MessageMapping("/register")
    @SendTo("/topic/register")
    public OutputMessage register(UserDTO dto) throws Exception{
        System.out.printf("Registrando usuario:" + dto.getUsername());
        setSecurityContextFrom(dto.getUsername());
        System.out.print(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        userService.register(dto.getUsername(), "nolausoaun");
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(dto.getUsername(), "Me he registrado enviame un mensaje", time, "todos");
    }

    public void setSecurityContextFrom(String username){
        Authentication auth = new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of(new SimpleGrantedAuthority("USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
