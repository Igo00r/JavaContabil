package com.fiap.financecontrol.presentation;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.domains.RegistroContabil;
import com.fiap.financecontrol.domains.Vendas;
import com.fiap.financecontrol.presentation.dtos.VendasRequestDto;
import com.fiap.financecontrol.presentation.dtos.VendasResponseDto;
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
@RequestMapping("/fiap/vendas")
@RequiredArgsConstructor
public class VendasController {

    private final CreateVendasService createVendasService;
    private final UpdateVendasService updateVendasService;
    private final ListVendasService listVendasService;
    private final FindByIdVendasService findByIdVendasService;
    private final DeleteVendasService deleteVendasService;

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<VendasResponseDto>> getVenda(@PathVariable Long id) {
        Vendas venda = findByIdVendasService.executeOrThrow(id);
        VendasResponseDto dto = VendasResponseDto.fromEntity(venda);
        EntityModel<VendasResponseDto> model = EntityModel.of(dto);
        addLinksToVenda(model, id, dto.getClienteId(), dto.getRegistroContabilId());
        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<VendasResponseDto>>> getVendas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long registroContabilId
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

        // Links de navegação
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
    public ResponseEntity<EntityModel<VendasResponseDto>> createVenda(@RequestBody @Valid VendasRequestDto vendaDto) {
        Vendas venda = vendaDto.toEntity();
        
        venda.setUsuario(Usuario.builder().id(vendaDto.getClienteId()).build());
        venda.setRegistroContabil(RegistroContabil.builder().id(vendaDto.getRegistroContabilId()).build());
        
        Vendas vendaCriada = createVendasService.execute(venda);
        VendasResponseDto dto = VendasResponseDto.fromEntity(vendaCriada);
        EntityModel<VendasResponseDto> model = EntityModel.of(dto);
        addLinksToVenda(model, vendaCriada.getId(), dto.getClienteId(), dto.getRegistroContabilId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(linkTo(methodOn(VendasController.class).getVenda(vendaCriada.getId())).toUri())
                .body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<VendasResponseDto>> updateVenda(@PathVariable Long id, @RequestBody @Valid VendasRequestDto vendaDto) {
        Vendas venda = vendaDto.toEntity();
        venda.setId(id);
        
        venda.setUsuario(Usuario.builder().id(vendaDto.getClienteId()).build());
        venda.setRegistroContabil(RegistroContabil.builder().id(vendaDto.getRegistroContabilId()).build());
        
        Vendas vendaAtualizada = updateVendasService.execute(venda);
        VendasResponseDto dto = VendasResponseDto.fromEntity(vendaAtualizada);
        EntityModel<VendasResponseDto> model = EntityModel.of(dto);
        addLinksToVenda(model, id, dto.getClienteId(), dto.getRegistroContabilId());
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenda(@PathVariable Long id) {
        deleteVendasService.execute(id);
    }

    private void addLinksToVenda(EntityModel<VendasResponseDto> model, Long id, Long clienteId, Long registroContabilId) {
        model.add(linkTo(methodOn(VendasController.class).getVenda(id)).withSelfRel());
        model.add(linkTo(methodOn(VendasController.class).updateVenda(id, null)).withRel("update"));
        model.add(linkTo(VendasController.class).slash(id).withRel("delete"));
        if (clienteId != null) {
            model.add(linkTo(methodOn(UsuarioController.class).getCliente(clienteId)).withRel("usuario"));
        }
        if (registroContabilId != null) {
            model.add(WebMvcLinkBuilder.linkTo(methodOn(RegistroContabilController.class).getRegistroContabil(registroContabilId)).withRel("registro-contabil"));
        }
    }
}
