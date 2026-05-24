package com.fiap.financecontrol.presentation;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.domains.Conta;
import com.fiap.financecontrol.domains.TipoConta;
import com.fiap.financecontrol.presentation.dtos.request.ContaRequestDto;
import com.fiap.financecontrol.presentation.dtos.response.ContaResponseDto;
import com.fiap.financecontrol.services.conta.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/fiap/contas")
@RequiredArgsConstructor
@Tag(name = "Contas", description = "Endpoints para gerenciamento de contas")
public class ContaController {

    private final CreateContaService createContaService;
    private final UpdateContaService updateContaService;
    private final ListContasService listContasService;
    private final FindByIdContaService findByIdContaService;
    private final DeleteContaService deleteContaService;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta por ID", description = "Retorna os detalhes de uma conta específica.")
    @ApiResponse(responseCode = "200", description = "Conta encontrada")
    @ApiResponse(responseCode = "404", description = "Conta não encontrada")
    public ResponseEntity<EntityModel<ContaResponseDto>> getConta(@PathVariable Long id) {
        Conta conta = findByIdContaService.executeOrThrow(id);
        ContaResponseDto dto = ContaResponseDto.fromEntity(conta);
        EntityModel<ContaResponseDto> model = EntityModel.of(dto);
        addLinksToConta(model, id, dto.getUsuarioId());
        return ResponseEntity.ok(model);
    }

    @GetMapping
    @Operation(summary = "Listar contas", description = "Retorna uma lista paginada de contas, podendo filtrar por tipo e nome.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<PagedModel<EntityModel<ContaResponseDto>>> getContas(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Direção da ordenação (ASC, DESC)") @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filtro pelo tipo de conta") @RequestParam(required = false) TipoConta tipo,
            @Parameter(description = "Filtro pelo nome da conta") @RequestParam(required = false) String nome
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
                    addLinksToConta(model, dto.getId(), dto.getUsuarioId());
                    return model;
                }).toList(),
                pageMetadata
        );

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
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar nova conta", description = "Adiciona uma nova conta ao sistema vinculada ao usuário autenticado.")
    @ApiResponse(responseCode = "201", description = "Conta criada com sucesso")
    public ResponseEntity<EntityModel<ContaResponseDto>> createConta(@RequestBody @Valid ContaRequestDto contaDto, @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario) {
        Conta conta = contaDto.toEntity();

        if (usuario.getId() != null) {
            conta.setUsuario(Usuario.builder().id(usuario.getId()).build());
        }

        Conta contaCriada = createContaService.criarConta(conta);
        ContaResponseDto dto = ContaResponseDto.fromEntity(contaCriada);
        EntityModel<ContaResponseDto> model = EntityModel.of(dto);
        addLinksToConta(model, contaCriada.getId(), dto.getUsuarioId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(linkTo(methodOn(ContaController.class).getConta(contaCriada.getId())).toUri())
                .body(model);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar conta", description = "Atualiza os dados de uma conta existente.")
    @ApiResponse(responseCode = "200", description = "Conta atualizada com sucesso")
    public ResponseEntity<EntityModel<ContaResponseDto>> updateConta(@PathVariable Long id, @RequestBody @Valid ContaRequestDto contaDto, @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario) {
        Conta conta = contaDto.toEntity();
        conta.setId(id);

        if (usuario.getId() != null) {
            conta.setUsuario(Usuario.builder().id(usuario.getId()).build());
        }

        Conta contaAtualizada = updateContaService.execute(conta);
        ContaResponseDto dto = ContaResponseDto.fromEntity(contaAtualizada);
        EntityModel<ContaResponseDto> model = EntityModel.of(dto);
        addLinksToConta(model, id, dto.getUsuarioId());
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir conta", description = "Remove uma conta do sistema pelo seu ID.")
    @ApiResponse(responseCode = "204", description = "Conta excluída com sucesso")
    public ResponseEntity<Void> deleteConta(@PathVariable Long id, @Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario) {
        deleteContaService.execute(id);
        return ResponseEntity.noContent().build();
    }

    private void addLinksToConta(EntityModel<ContaResponseDto> model, Long id, Long usuarioId) {
        model.add(linkTo(methodOn(ContaController.class).getConta(id)).withSelfRel());
        model.add(linkTo(methodOn(ContaController.class).updateConta(id, null, null)).withRel("update"));
        model.add(linkTo(methodOn(ContaController.class).deleteConta(id, null)).withRel("delete"));
        if (usuarioId != null) {
            model.add(linkTo(methodOn(UsuarioController.class).getUsuario(usuarioId)).withRel("usuario"));
        }
        model.add(linkTo(methodOn(RegistroContabilController.class).getRegistrosContabeis(0, Sort.Direction.ASC, 10, id, null, null, null)).withRel("registros-contabeis"));
        model.add(linkTo(methodOn(ContaController.class).getContas(0, Sort.Direction.ASC, 10, null, null)).withRel("collection"));
    }
}