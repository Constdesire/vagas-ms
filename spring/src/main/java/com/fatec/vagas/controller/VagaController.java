package com.fatec.vagas.controller;

import com.fatec.vagas.controller.adapter.VagaControllerAdapter;
import com.fatec.vagas.controller.dto.request.ReducaoRequest;
import com.fatec.vagas.controller.dto.request.VagaRequest;
import com.fatec.vagas.controller.dto.response.ReducaoResponse;
import com.fatec.vagas.controller.dto.response.VagaResponse;
import com.fatec.vagas.service.VagaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/vagas")
public class VagaController {

    private final VagaService service;

    public VagaController(VagaService service) {
        this.service = service;
    }

    // público no gateway (GET)
    @GetMapping
    public List<VagaResponse> listar() {
        return service.listar().stream().map(VagaControllerAdapter::castResponse).toList();
    }

    @GetMapping("/{id}")
    public VagaResponse buscar(@PathVariable Long id) {
        return VagaControllerAdapter.castResponse(service.buscar(id));
    }

    // o gateway só leva até aqui quem é ADMIN: /fatec/admin/vagas  ->  /vagas
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VagaResponse criar(@RequestBody VagaRequest request) {
        return VagaControllerAdapter.castResponse(service.criar(VagaControllerAdapter.castRequest(request)));
    }

    // INTERNO: só o reducao-ms chama (direto, serviço a serviço). Não existe rota no gateway para ele.
    @PostMapping("/{id}/reduzir")
    public ReducaoResponse reduzir(@PathVariable Long id, @RequestBody ReducaoRequest request) {
        return VagaControllerAdapter.castResponse(service.reduzir(id, request.candidaturaId(), request.usuario()));
    }
}
