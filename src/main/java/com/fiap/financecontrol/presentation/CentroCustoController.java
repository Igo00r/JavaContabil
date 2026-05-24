package com.fiap.financecontrol.presentation;

import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.presentation.dtos.request.CentroCustoRequestDto;
import com.fiap.financecontrol.presentation.dtos.response.CentroCustoResponseDto;
import com.fiap.financecontrol.services.centroCusto.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/fiap/centros-custo")
@RequiredArgsConstructor
@Tag(name = "Centro de Custo", description = "Endpoints para gerenciamento de centros de custo")
public class CentroCustoController {
    private final CreateCentroCustoService createCentroCustoService;
    private final UpdateCentroCustoService updateCentroCustoService;
    private final ListCentrosCustoService listCentrosCustoService;
    private final FindByIdCentroCustoService findByIdCentroCustoService;
    private final DeleteCentroCustoService deleteCentroCustoService;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar centro de custo por ID", description = "Retorna os detalhes de um centro de custo específico.")
    @ApiResponse(responseCode = "200", description = "Centro de custo encontrado")
    @ApiResponse(responseCode = "404", description = "Centro de custo não encontrado")
    public ResponseEntity<EntityModel<CentroCustoResponseDto>> getCentroCusto(@PathVariable Long id) {
        CentroCusto centroCusto = findByIdCentroCustoService.executeOrThrow(id);
        CentroCustoResponseDto dto = CentroCustoResponseDto.fromEntity(centroCusto);
        EntityModel<CentroCustoResponseDto> model = EntityModel.of(dto);
        addLinksToCentroCusto(model, id);
        return ResponseEntity.ok(model);
    }

    @GetMapping
    @Operation(summary = "Listar centros de custo", description = "Retorna uma lista paginada de centros de custo, podendo filtrar por nome.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<PagedModel<EntityModel<CentroCustoResponseDto>>> getCentrosCusto(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Direção da ordenação (ASC, DESC)") @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filtro pelo nome do centro de custo") @RequestParam(required = false) String nome
    ) {
        Page<CentroCusto> centrosCusto;

        if (nome != null && !nome.trim().isEmpty()) {
            centrosCusto = listCentrosCustoService.buscarCentrosCustoPorNome(nome, page, size, direction);
        } else {
            centrosCusto = listCentrosCustoService.listarCentrosCusto(page, size, direction);
        }

        Page<CentroCustoResponseDto> response = centrosCusto.map(CentroCustoResponseDto::fromEntity);

        if (centrosCusto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                centrosCusto.getSize(),
                centrosCusto.getNumber(),
                centrosCusto.getTotalElements(),
                centrosCusto.getTotalPages()
        );

        PagedModel<EntityModel<CentroCustoResponseDto>> pagedModel = PagedModel.of(
                response.map(dto -> {
                    EntityModel<CentroCustoResponseDto> model = EntityModel.of(dto);
                    addLinksToCentroCusto(model, dto.getId());
                    return model;
                }).toList(),
                pageMetadata
        );

        pagedModel.add(linkTo(methodOn(CentroCustoController.class).getCentrosCusto(0, direction, size, nome)).withRel("first"));
        if (centrosCusto.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(CentroCustoController.class).getCentrosCusto(centrosCusto.getNumber() - 1, direction, size, nome)).withRel("prev"));
        }
        pagedModel.add(linkTo(methodOn(CentroCustoController.class).getCentrosCusto(centrosCusto.getNumber(), direction, size, nome)).withSelfRel());
        if (centrosCusto.hasNext()) {
            pagedModel.add(linkTo(methodOn(CentroCustoController.class).getCentrosCusto(centrosCusto.getNumber() + 1, direction, size, nome)).withRel("next"));
        }
        pagedModel.add(linkTo(methodOn(CentroCustoController.class).getCentrosCusto(centrosCusto.getTotalPages() - 1, direction, size, nome)).withRel("last"));

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar novo centro de custo", description = "Adiciona um novo centro de custo ao sistema.")
    @ApiResponse(responseCode = "201", description = "Centro de custo criado")
    public ResponseEntity<EntityModel<CentroCustoResponseDto>> createCentroCusto(@RequestBody @Valid CentroCustoRequestDto centroCustoDto) {
        CentroCusto centroCusto = createCentroCustoService.execute(centroCustoDto.toEntity());
        CentroCustoResponseDto dto = CentroCustoResponseDto.fromEntity(centroCusto);
        EntityModel<CentroCustoResponseDto> model = EntityModel.of(dto);
        addLinksToCentroCusto(model, centroCusto.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(linkTo(methodOn(CentroCustoController.class).getCentroCusto(centroCusto.getId())).toUri())
                .body(model);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar centro de custo", description = "Atualiza os dados de um centro de custo existente.")
    @ApiResponse(responseCode = "200", description = "Centro de custo atualizado")
    public ResponseEntity<EntityModel<CentroCustoResponseDto>> updateCentroCusto(@PathVariable Long id, @RequestBody @Valid CentroCustoRequestDto centroCustoDto) {
        CentroCusto centroCusto = centroCustoDto.toEntity();
        centroCusto.setId(id);
        CentroCusto centroCustoAtualizado = updateCentroCustoService.execute(centroCusto);
        CentroCustoResponseDto dto = CentroCustoResponseDto.fromEntity(centroCustoAtualizado);
        EntityModel<CentroCustoResponseDto> model = EntityModel.of(dto);
        addLinksToCentroCusto(model, id);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir centro de custo", description = "Remove um centro de custo pelo seu ID.")
    @ApiResponse(responseCode = "204", description = "Centro de custo excluído")
    public ResponseEntity<Void> deleteCentroCusto(@PathVariable Long id) {
        deleteCentroCustoService.execute(id);
        return ResponseEntity.noContent().build();
    }

    private void addLinksToCentroCusto(EntityModel<CentroCustoResponseDto> model, Long id) {
        model.add(linkTo(methodOn(CentroCustoController.class).getCentroCusto(id)).withSelfRel());
        model.add(linkTo(methodOn(CentroCustoController.class).updateCentroCusto(id, null)).withRel("update"));
        model.add(linkTo(methodOn(CentroCustoController.class).deleteCentroCusto(id)).withRel("delete"));
        model.add(linkTo(methodOn(CentroCustoController.class).getCentrosCusto(0, Sort.Direction.ASC, 10, null)).withRel("collection"));
    }
}
