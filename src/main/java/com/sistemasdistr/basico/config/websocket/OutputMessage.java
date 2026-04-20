package com.sistemasdistr.basico.config.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

@Getter
@Setter
@AllArgsConstructor
public class OutputMessage {

    private String from;
    private String text;
    private String time;
    private String to;
}
