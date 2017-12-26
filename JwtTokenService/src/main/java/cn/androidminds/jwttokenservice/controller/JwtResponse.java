package cn.androidminds.jwttokenservice.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JwtResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String token;
}
