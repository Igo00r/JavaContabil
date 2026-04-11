package com.fiap.financecontrol.presentation;

import com.fiap.financecontrol.domains.Usuario;
import com.fiap.financecontrol.presentation.dtos.request.UsuarioRequestDto;
import com.fiap.financecontrol.presentation.dtos.response.UsuarioResponseDto;
import com.fiap.financecontrol.services.usuario.*;
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
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/fiap/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final CreateUsuarioService createUsuarioService;
    private final UpdateUsuarioService updateUsuarioService;
    private final ListUsuariosService listUsuariosService;
    private final FindByIdUsuarioService findByIdUsuarioService;
    private final DeleteUsuarioService deleteUsuario;


    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponseDto>> getUsuario(@PathVariable Long id) {
        Usuario usuario = findByIdUsuarioService.executeOrThrow(id);
        UsuarioResponseDto dto = UsuarioResponseDto.fromEntity(usuario);
        EntityModel<UsuarioResponseDto> model = EntityModel.of(dto);
        addLinksToUsuario(model, id);
        return ResponseEntity.ok(model);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedModel<EntityModel<UsuarioResponseDto>>> getUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String nome
    ) {
        Page<Usuario> usuarios;
        
        if (nome != null && !nome.trim().isEmpty()) {
            usuarios = listUsuariosService.buscarUsuarioPorNome(nome, page, size, direction);
        } else {
            usuarios = listUsuariosService.lsitarUsuarioAtivos(page, size, direction);
        }

        Page<UsuarioResponseDto> response = usuarios.map(UsuarioResponseDto::fromEntity);

        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                usuarios.getSize(),
                usuarios.getNumber(),
                usuarios.getTotalElements(),
                usuarios.getTotalPages()
        );

        PagedModel<EntityModel<UsuarioResponseDto>> pagedModel = PagedModel.of(
                response.map(dto -> {
                    EntityModel<UsuarioResponseDto> model = EntityModel.of(dto);
                    addLinksToUsuario(model, dto.getId());
                    return model;
                }).toList(),
                pageMetadata
        );

        // Links de navegação
        pagedModel.add(linkTo(methodOn(UsuarioController.class).getUsuarios(0, direction, size, nome)).withRel("first"));
        if (usuarios.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(UsuarioController.class).getUsuarios(usuarios.getNumber() - 1, direction, size, nome)).withRel("prev"));
        }
        pagedModel.add(linkTo(methodOn(UsuarioController.class).getUsuarios(usuarios.getNumber(), direction, size, nome)).withSelfRel());
        if (usuarios.hasNext()) {
            pagedModel.add(linkTo(methodOn(UsuarioController.class).getUsuarios(usuarios.getNumber() + 1, direction, size, nome)).withRel("next"));
        }
        pagedModel.add(linkTo(methodOn(UsuarioController.class).getUsuarios(usuarios.getTotalPages() - 1, direction, size, nome)).withRel("last"));

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping
    public ResponseEntity<EntityModel<UsuarioResponseDto>> createUsuario(@RequestBody @Valid UsuarioRequestDto clienteDto) {
        Usuario usuario = createUsuarioService.execute(clienteDto.toEntity());
        UsuarioResponseDto dto = UsuarioResponseDto.fromEntity(usuario);
        EntityModel<UsuarioResponseDto> model = EntityModel.of(dto);
        addLinksToUsuario(model, usuario.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(linkTo(methodOn(UsuarioController.class).getUsuario(usuario.getId())).toUri())
                .body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UsuarioResponseDto>> updateUsuario(@PathVariable Long id, @RequestBody @Valid UsuarioRequestDto usuarioRequestDto) {
        Usuario usuario = usuarioRequestDto.toEntity();
        usuario.setId(id);
        Usuario usuarioAtualizado = updateUsuarioService.execute(usuario);
        UsuarioResponseDto dto = UsuarioResponseDto.fromEntity(usuarioAtualizado);
        EntityModel<UsuarioResponseDto> model = EntityModel.of(dto);
        addLinksToUsuario(model, id);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCliente(@PathVariable Long id) {
        deleteUsuario.execute(id);
    }

    private void addLinksToUsuario(EntityModel<UsuarioResponseDto> model, Long id) {
        model.add(linkTo(methodOn(UsuarioController.class).getUsuario(id)).withSelfRel());
        model.add(linkTo(methodOn(UsuarioController.class).updateUsuario(id, null)).withRel("update"));
        model.add(linkTo(UsuarioController.class).slash(id).withRel("delete"));
        model.add(linkTo(methodOn(ContaController.class).getContas(0, Sort.Direction.ASC, 10, null, null)).withRel("contas"));
        model.add(WebMvcLinkBuilder.linkTo(methodOn(VendasController.class).getVendas(0, Sort.Direction.ASC, 10, id, null)).withRel("vendas"));
    }
}
