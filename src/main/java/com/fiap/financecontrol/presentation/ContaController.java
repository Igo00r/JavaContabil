package com.fiap.financecontrol.presentation;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.domains.Conta;
import com.fiap.financecontrol.domains.TipoConta;
import com.fiap.financecontrol.presentation.dtos.request.ContaRequestDto;
import com.fiap.financecontrol.presentation.dtos.response.ContaResponseDto;
import com.fiap.financecontrol.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/fiap/contas")
@RequiredArgsConstructor
public class ContaController {

    private final CreateContaService createContaService;
    private final UpdateContaService updateContaService;
    private final ListContasService listContasService;
    private final FindByIdContaService findByIdContaService;
    private final DeleteContaService deleteContaService;

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ContaResponseDto>> getConta(@PathVariable Long id) {
        Conta conta = findByIdContaService.executeOrThrow(id);
        ContaResponseDto dto = ContaResponseDto.fromEntity(conta);
        EntityModel<ContaResponseDto> model = EntityModel.of(dto);
        addLinksToConta(model, id, dto.getClienteId());
        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ContaResponseDto>>> getContas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) TipoConta tipo,
            @RequestParam(required = false) String nome
    ) {
        Page<Conta> contas;
        
        if (nome != null && !nome.trim().isEmpty() && tipo != null) {
            contas = listContasService.buscarContasPorNome(nome, tipo, page, size, direction);
        } else if (tipo != null) {
            contas = listContasService.listarContasPorTipo(tipo, page, size, direction);
        } else {
            contas = listContasService.listarContas(page, size, direction);
        }

        Page<ContaResponseDto> response = contas.map(ContaResponseDto::fromEntity);

        if (contas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                contas.getSize(),
                contas.getNumber(),
                contas.getTotalElements(),
                contas.getTotalPages()
        );

        PagedModel<EntityModel<ContaResponseDto>> pagedModel = PagedModel.of(
                response.map(dto -> {
                    EntityModel<ContaResponseDto> model = EntityModel.of(dto);
                    addLinksToConta(model, dto.getId(), dto.getClienteId());
                    return model;
                }).toList(),
                pageMetadata
        );

        // Links de navegação
        pagedModel.add(linkTo(methodOn(ContaController.class).getContas(0, direction, size, tipo, nome)).withRel("first"));
        if (contas.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(ContaController.class).getContas(contas.getNumber() - 1, direction, size, tipo, nome)).withRel("prev"));
        }
        pagedModel.add(linkTo(methodOn(ContaController.class).getContas(contas.getNumber(), direction, size, tipo, nome)).withSelfRel());
        if (contas.hasNext()) {
            pagedModel.add(linkTo(methodOn(ContaController.class).getContas(contas.getNumber() + 1, direction, size, tipo, nome)).withRel("next"));
        }
        pagedModel.add(linkTo(methodOn(ContaController.class).getContas(contas.getTotalPages() - 1, direction, size, tipo, nome)).withRel("last"));

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping
    public ResponseEntity<EntityModel<ContaResponseDto>> createConta(@RequestBody @Valid ContaRequestDto contaDto) {
        Conta conta = contaDto.toEntity();
        
        if (contaDto.getClienteId() != null) {
            conta.setUsuario(Usuario.builder().id(contaDto.getClienteId()).build());
        }
        
        Conta contaCriada = createContaService.execute(conta);
        ContaResponseDto dto = ContaResponseDto.fromEntity(contaCriada);
        EntityModel<ContaResponseDto> model = EntityModel.of(dto);
        addLinksToConta(model, contaCriada.getId(), dto.getClienteId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(linkTo(methodOn(ContaController.class).getConta(contaCriada.getId())).toUri())
                .body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ContaResponseDto>> updateConta(@PathVariable Long id, @RequestBody @Valid ContaRequestDto contaDto) {
        Conta conta = contaDto.toEntity();
        conta.setId(id);
        
        if (contaDto.getClienteId() != null) {
            conta.setUsuario(Usuario.builder().id(contaDto.getClienteId()).build());
        }
        
        Conta contaAtualizada = updateContaService.execute(conta);
        ContaResponseDto dto = ContaResponseDto.fromEntity(contaAtualizada);
        EntityModel<ContaResponseDto> model = EntityModel.of(dto);
        addLinksToConta(model, id, dto.getClienteId());
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConta(@PathVariable Long id) {
        deleteContaService.execute(id);
    }

    private void addLinksToConta(EntityModel<ContaResponseDto> model, Long id, Long clienteId) {
        model.add(linkTo(methodOn(ContaController.class).getConta(id)).withSelfRel());
        model.add(linkTo(methodOn(ContaController.class).updateConta(id, null)).withRel("update"));
        model.add(linkTo(ContaController.class).slash(id).withRel("delete"));
        if (clienteId != null) {
            model.add(WebMvcLinkBuilder.linkTo(methodOn(UsuarioController.class).getCliente(clienteId)).withRel("usuario"));
        }
        model.add(WebMvcLinkBuilder.linkTo(methodOn(RegistroContabilController.class).getRegistrosContabeis(0, Sort.Direction.ASC, 10, id, null, null, null)).withRel("registros-contabeis"));
    }
}
