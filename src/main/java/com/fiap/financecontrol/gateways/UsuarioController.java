package com.fiap.financecontrol.gateways;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.gateways.dtos.ClienteRequestDto;
import com.fiap.financecontrol.gateways.dtos.ClienteResponseDto;
import com.fiap.financecontrol.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/fiap/clientes")
@RequiredArgsConstructor
public class UsuarioController {

    private final CreateUsuarioService createClienteService;
    private final UpdateUsuarioService updateClienteService;
    private final ListClientesService listClientesService;
    private final FindByIdClienteService findByIdClienteService;
    private final DeleteClienteService deleteClienteService;

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ClienteResponseDto>> getCliente(@PathVariable Long id) {
        Usuario usuario = findByIdClienteService.executeOrThrow(id);
        ClienteResponseDto dto = ClienteResponseDto.fromEntity(usuario);
        EntityModel<ClienteResponseDto> model = EntityModel.of(dto);
        addLinksToCliente(model, id);
        return ResponseEntity.ok(model);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ClienteResponseDto>>> getClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome
    ) {
        Page<Usuario> clientes;
        
        if (nome != null && !nome.trim().isEmpty()) {
            clientes = listClientesService.buscarClientesPorNome(nome, page, size, direction);
        } else {
            clientes = listClientesService.listarClientesAtivos(page, size, direction);
        }

        Page<ClienteResponseDto> response = clientes.map(ClienteResponseDto::fromEntity);

        if (clientes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                clientes.getSize(),
                clientes.getNumber(),
                clientes.getTotalElements(),
                clientes.getTotalPages()
        );

        PagedModel<EntityModel<ClienteResponseDto>> pagedModel = PagedModel.of(
                response.map(dto -> {
                    EntityModel<ClienteResponseDto> model = EntityModel.of(dto);
                    addLinksToCliente(model, dto.getId());
                    return model;
                }).toList(),
                pageMetadata
        );

        // Links de navegação
        pagedModel.add(linkTo(methodOn(UsuarioController.class).getClientes(0, direction, size, nome)).withRel("first"));
        if (clientes.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(UsuarioController.class).getClientes(clientes.getNumber() - 1, direction, size, nome)).withRel("prev"));
        }
        pagedModel.add(linkTo(methodOn(UsuarioController.class).getClientes(clientes.getNumber(), direction, size, nome)).withSelfRel());
        if (clientes.hasNext()) {
            pagedModel.add(linkTo(methodOn(UsuarioController.class).getClientes(clientes.getNumber() + 1, direction, size, nome)).withRel("next"));
        }
        pagedModel.add(linkTo(methodOn(UsuarioController.class).getClientes(clientes.getTotalPages() - 1, direction, size, nome)).withRel("last"));

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping
    public ResponseEntity<EntityModel<ClienteResponseDto>> createCliente(@RequestBody @Valid ClienteRequestDto clienteDto) {
        Usuario usuario = createClienteService.execute(clienteDto.toEntity());
        ClienteResponseDto dto = ClienteResponseDto.fromEntity(usuario);
        EntityModel<ClienteResponseDto> model = EntityModel.of(dto);
        addLinksToCliente(model, usuario.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(linkTo(methodOn(UsuarioController.class).getCliente(usuario.getId())).toUri())
                .body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ClienteResponseDto>> updateCliente(@PathVariable Long id, @RequestBody @Valid ClienteRequestDto clienteDto) {
        Usuario usuario = clienteDto.toEntity();
        usuario.setId(id);
        Usuario usuarioAtualizado = updateClienteService.execute(usuario);
        ClienteResponseDto dto = ClienteResponseDto.fromEntity(usuarioAtualizado);
        EntityModel<ClienteResponseDto> model = EntityModel.of(dto);
        addLinksToCliente(model, id);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCliente(@PathVariable Long id) {
        deleteClienteService.execute(id);
    }

    private void addLinksToCliente(EntityModel<ClienteResponseDto> model, Long id) {
        model.add(linkTo(methodOn(UsuarioController.class).getCliente(id)).withSelfRel());
        model.add(linkTo(methodOn(UsuarioController.class).updateCliente(id, null)).withRel("update"));
        model.add(linkTo(UsuarioController.class).slash(id).withRel("delete"));
        model.add(linkTo(methodOn(ContaController.class).getContas(0, Sort.Direction.ASC, 10, null, null)).withRel("contas"));
        model.add(linkTo(methodOn(VendasController.class).getVendas(0, Sort.Direction.ASC, 10, id, null)).withRel("vendas"));
    }
}
