package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.config.websocket.OutputMessage;
import com.sistemasdistr.basico.dto.Message;
import com.sistemasdistr.basico.dto.UserDTO;
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

import java.text.SimpleDateFormat;
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
    public OutputMessage send(Message message){
        String time = new SimpleDateFormat("HH:mm").format(new Date());
        return new OutputMessage(message.getFrom(), message.getText(), time, "");
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
