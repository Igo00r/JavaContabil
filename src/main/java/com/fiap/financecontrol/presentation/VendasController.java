package com.fiap.financecontrol.presentation;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.domains.Vendas;
import com.fiap.financecontrol.presentation.dtos.VendasRequestDto;
import com.fiap.financecontrol.presentation.dtos.VendasResponseDto;
import com.fiap.financecontrol.presentation.dtos.request.VendaCreateDto;
import com.fiap.financecontrol.services.venda.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/fiap/vendas")
@RequiredArgsConstructor
@Tag(name = "Vendas", description = "Endpoints para gerenciamento de vendas")
public class VendasController {

    private final UpdateVendasService updateVendasService;
    private final ListVendasService listVendasService;
    private final FindByIdVendasService findByIdVendasService;
    private final DeleteVendasService deleteVendasService;
    private final VendaService vendaService;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar venda por ID", description = "Retorna os detalhes de uma venda específica.")
    @ApiResponse(responseCode = "200", description = "Venda encontrada")
    @ApiResponse(responseCode = "404", description = "Venda não encontrada")
    public ResponseEntity<EntityModel<VendasResponseDto>> getVenda(@PathVariable Long id) {
        Vendas venda = findByIdVendasService.executeOrThrow(id);
        VendasResponseDto dto = VendasResponseDto.fromEntity(venda);
        EntityModel<VendasResponseDto> model = EntityModel.of(dto);
        addLinksToVenda(model, id, dto.getClienteId(), dto.getRegistroContabilId());
        return ResponseEntity.ok(model);
    }

    @GetMapping
    @Operation(summary = "Listar vendas", description = "Retorna uma lista paginada de vendas, filtrando opcionalmente por cliente ou registro contábil.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<PagedModel<EntityModel<VendasResponseDto>>> getVendas(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Direção da ordenação (ASC, DESC)") @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filtro por ID do cliente") @RequestParam(required = false) Long clienteId,
            @Parameter(description = "Filtro por ID do registro contábil") @RequestParam(required = false) Long registroContabilId
    ) {
        Page<Vendas> vendas;

        if (clienteId != null) {
            vendas = listVendasService.listarVendasPorCliente(clienteId, page, size, direction);
        } else if (registroContabilId != null) {
            vendas = listVendasService.listarVendasPorRegistroContabil(registroContabilId, page, size, direction);
        } else {
            vendas = listVendasService.listarVendas(page, size, direction);
        }

        Page<VendasResponseDto> response = vendas.map(VendasResponseDto::fromEntity);

        if (vendas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                vendas.getSize(),
                vendas.getNumber(),
                vendas.getTotalElements(),
                vendas.getTotalPages()
        );

        PagedModel<EntityModel<VendasResponseDto>> pagedModel = PagedModel.of(
                response.map(dto -> {
                    EntityModel<VendasResponseDto> model = EntityModel.of(dto);
                    addLinksToVenda(model, dto.getId(), dto.getClienteId(), dto.getRegistroContabilId());
                    return model;
                }).toList(),
                pageMetadata
        );

        pagedModel.add(linkTo(methodOn(VendasController.class).getVendas(0, direction, size, clienteId, registroContabilId)).withRel("first"));
        if (vendas.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(VendasController.class).getVendas(vendas.getNumber() - 1, direction, size, clienteId, registroContabilId)).withRel("prev"));
        }
        pagedModel.add(linkTo(methodOn(VendasController.class).getVendas(vendas.getNumber(), direction, size, clienteId, registroContabilId)).withSelfRel());
        if (vendas.hasNext()) {
            pagedModel.add(linkTo(methodOn(VendasController.class).getVendas(vendas.getNumber() + 1, direction, size, clienteId, registroContabilId)).withRel("next"));
        }
        pagedModel.add(linkTo(methodOn(VendasController.class).getVendas(vendas.getTotalPages() - 1, direction, size, clienteId, registroContabilId)).withRel("last"));

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping
    @Operation(summary = "Criar nova venda", description = "Adiciona uma nova venda ao sistema vinculada ao usuário autenticado.")
    @ApiResponse(responseCode = "201", description = "Venda criada com sucesso")
    public ResponseEntity<EntityModel<VendasResponseDto>> criarVenda(@Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario,
                                                                     @RequestBody @Valid VendaCreateDto vendaDto
    ){
        VendasResponseDto vendasResponseDto = vendaService.execute(vendaDto, usuario.getId());
        EntityModel<VendasResponseDto> model = EntityModel.of(vendasResponseDto);
        addLinksToVenda(model, vendasResponseDto.getId(), vendasResponseDto.getClienteId(), vendasResponseDto.getRegistroContabilId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .location(linkTo(methodOn(VendasController.class).getVenda(vendasResponseDto.getId())).toUri())
                .body(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar venda", description = "Atualiza os dados de uma venda existente.")
    @ApiResponse(responseCode = "200", description = "Venda atualizada com sucesso")
    public ResponseEntity<EntityModel<VendasResponseDto>> updateVenda(@Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario, @PathVariable Long id, @RequestBody @Valid VendasRequestDto vendaDto) {
        Vendas venda = vendaDto.toEntity();
        venda.setId(id);

        venda.setUsuario(Usuario.builder().id(usuario.getId()).build());

        Vendas vendaAtualizada = updateVendasService.execute(venda);
        VendasResponseDto dto = VendasResponseDto.fromEntity(vendaAtualizada);
        EntityModel<VendasResponseDto> model = EntityModel.of(dto);
        addLinksToVenda(model, id, dto.getClienteId(), dto.getRegistroContabilId());
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir venda", description = "Remove uma venda do sistema pelo seu ID.")
    @ApiResponse(responseCode = "204", description = "Venda excluída com sucesso")
    public ResponseEntity<Void> deleteVenda(@PathVariable Long id) {
        deleteVendasService.execute(id);
        return ResponseEntity.noContent().build();
    }

    private void addLinksToVenda(EntityModel<VendasResponseDto> model, Long id, Long clienteId, Long registroContabilId) {
        model.add(linkTo(methodOn(VendasController.class).getVenda(id)).withSelfRel());
        model.add(linkTo(methodOn(VendasController.class).updateVenda(null, id, null)).withRel("update"));
        model.add(linkTo(methodOn(VendasController.class).deleteVenda(id)).withRel("delete"));
        if (clienteId != null) {
            model.add(linkTo(methodOn(UsuarioController.class).getUsuario(clienteId)).withRel("usuario"));
        }
        if (registroContabilId != null) {
            model.add(linkTo(methodOn(RegistroContabilController.class).getRegistroContabil(registroContabilId)).withRel("registro-contabil"));
        }
        model.add(linkTo(methodOn(VendasController.class).getVendas(0, Sort.Direction.ASC, 10, null, null)).withRel("collection"));
    }
}