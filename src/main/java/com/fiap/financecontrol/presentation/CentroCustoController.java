package com.fiap.financecontrol.presentation;

import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.presentation.dtos.request.CentroCustoRequestDto;
import com.fiap.financecontrol.presentation.dtos.response.CentroCustoResponseDto;
import com.fiap.financecontrol.services.centroCusto.*;
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
public class CentroCustoController {
    private final CreateCentroCustoService createCentroCustoService;
    private final UpdateCentroCustoService updateCentroCustoService;
    private final ListCentrosCustoService listCentrosCustoService;
    private final FindByIdCentroCustoService findByIdCentroCustoService;
    private final DeleteCentroCustoService deleteCentroCustoService;


    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CentroCustoResponseDto>> getCentroCusto(@PathVariable Long id) {
        CentroCusto centroCusto = findByIdCentroCustoService.executeOrThrow(id);
        CentroCustoResponseDto dto = CentroCustoResponseDto.fromEntity(centroCusto);
        EntityModel<CentroCustoResponseDto> model = EntityModel.of(dto);
        addLinksToCentroCusto(model, id);
        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<CentroCustoResponseDto>>> getCentrosCusto(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome
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

        // Links de navegação
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCentroCusto(@PathVariable Long id) {
        deleteCentroCustoService.execute(id);
    }

    private void addLinksToCentroCusto(EntityModel<CentroCustoResponseDto> model, Long id) {
        model.add(linkTo(methodOn(CentroCustoController.class).getCentroCusto(id)).withSelfRel());
        model.add(linkTo(methodOn(CentroCustoController.class).updateCentroCusto(id, null)).withRel("update"));
        model.add(linkTo(CentroCustoController.class).slash(id).withRel("delete"));
    }
}
