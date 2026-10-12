package com.fatec.vagas.controller;

import com.fatec.vagas.controller.dto.response.ErroResponse;
import com.fatec.vagas.service.exception.NaoEncontradoException;
import com.fatec.vagas.service.exception.RegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(RegraNegocioException.class)
    public ErroResponse regraNegocio(RegraNegocioException ex) {
        return new ErroResponse(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NaoEncontradoException.class)
    public ErroResponse naoEncontrado(NaoEncontradoException ex) {
        return new ErroResponse(ex.getMessage());
    }
}
