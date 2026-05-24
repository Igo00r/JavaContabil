package com.fiap.financecontrol.presentation;

import com.fiap.financecontrol.domains.CentroCusto;
import com.fiap.financecontrol.domains.Conta;
import com.fiap.financecontrol.domains.RegistroContabil;
import com.fiap.financecontrol.presentation.dtos.RelatorioSaudeDto;
import com.fiap.financecontrol.presentation.dtos.request.RegistroContabilRequestDto;
import com.fiap.financecontrol.presentation.dtos.response.RegistroContabilResponseDto;
import com.fiap.financecontrol.services.registroContabil.*;
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

import java.math.BigDecimal;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/fiap/registros-contabeis")
@RequiredArgsConstructor
@Tag(name = "Registros Contábeis", description = "Endpoints para gerenciamento de registros contábeis e relatórios")
public class RegistroContabilController {

    private final CreateRegistroContabilService createRegistroContabilService;
    private final UpdateRegistroContabilService updateRegistroContabilService;
    private final ListRegistrosContabeisService listRegistrosContabeisService;
    private final FindByIdRegistroContabilService findByIdRegistroContabilService;
    private final DeleteRegistroContabilService deleteRegistroContabilService;
    private final RelatorioContabilService relatorioContabilService;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar registro contábil por ID", description = "Retorna os detalhes de um registro contábil específico.")
    @ApiResponse(responseCode = "200", description = "Registro contábil encontrado")
    @ApiResponse(responseCode = "404", description = "Registro contábil não encontrado")
    public ResponseEntity<EntityModel<RegistroContabilResponseDto>> getRegistroContabil(@PathVariable Long id) {
        RegistroContabil registro = findByIdRegistroContabilService.executeOrThrow(id);
        RegistroContabilResponseDto dto = RegistroContabilResponseDto.fromEntity(registro);
        EntityModel<RegistroContabilResponseDto> model = EntityModel.of(dto);
        addLinksToRegistroContabil(model, id, dto.getContaId(), dto.getCentroCustoId());
        return ResponseEntity.ok(model);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar registros contábeis", description = "Retorna uma lista paginada de registros contábeis com filtros opcionais.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<PagedModel<EntityModel<RegistroContabilResponseDto>>> getRegistrosContabeis(
            @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Direção da ordenação (ASC, DESC)") @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filtrar por ID da conta") @RequestParam(required = false) Long contaId,
            @Parameter(description = "Filtrar por ID do centro de custo") @RequestParam(required = false) Long centroCustoId,
            @Parameter(description = "Valor mínimo para filtro de intervalo") @RequestParam(required = false) BigDecimal valorMinimo,
            @Parameter(description = "Valor máximo para filtro de intervalo") @RequestParam(required = false) BigDecimal valorMaximo
    ) {
        Page<RegistroContabil> registros;

        if (contaId != null) {
            registros = listRegistrosContabeisService.listarRegistrosPorConta(contaId, page, size, direction);
        } else if (centroCustoId != null) {
            registros = listRegistrosContabeisService.listarRegistrosPorCentroCusto(centroCustoId, page, size, direction);
        } else if (valorMinimo != null && valorMaximo != null) {
            registros = listRegistrosContabeisService.listarRegistrosPorValor(valorMinimo, valorMaximo, page, size, direction);
        } else {
            registros = listRegistrosContabeisService.listarRegistrosContabeis(page, size, direction);
        }

        Page<RegistroContabilResponseDto> response = registros.map(RegistroContabilResponseDto::fromEntity);

        if (registros.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        PagedModel.PageMetadata pageMetadata = new PagedModel.PageMetadata(
                registros.getSize(),
                registros.getNumber(),
                registros.getTotalElements(),
                registros.getTotalPages()
        );

        PagedModel<EntityModel<RegistroContabilResponseDto>> pagedModel = PagedModel.of(
                response.map(dto -> {
                    EntityModel<RegistroContabilResponseDto> model = EntityModel.of(dto);
                    addLinksToRegistroContabil(model, dto.getId(), dto.getContaId(), dto.getCentroCustoId());
                    return model;
                }).toList(),
                pageMetadata
        );

        pagedModel.add(linkTo(methodOn(RegistroContabilController.class).getRegistrosContabeis(0, direction, size, contaId, centroCustoId, valorMinimo, valorMaximo)).withRel("first"));
        if (registros.hasPrevious()) {
            pagedModel.add(linkTo(methodOn(RegistroContabilController.class).getRegistrosContabeis(registros.getNumber() - 1, direction, size, contaId, centroCustoId, valorMinimo, valorMaximo)).withRel("prev"));
        }
        pagedModel.add(linkTo(methodOn(RegistroContabilController.class).getRegistrosContabeis(registros.getNumber(), direction, size, contaId, centroCustoId, valorMinimo, valorMaximo)).withSelfRel());
        if (registros.hasNext()) {
            pagedModel.add(linkTo(methodOn(RegistroContabilController.class).getRegistrosContabeis(registros.getNumber() + 1, direction, size, contaId, centroCustoId, valorMinimo, valorMaximo)).withRel("next"));
        }
        pagedModel.add(linkTo(methodOn(RegistroContabilController.class).getRegistrosContabeis(registros.getTotalPages() - 1, direction, size, contaId, centroCustoId, valorMinimo, valorMaximo)).withRel("last"));

        return ResponseEntity.ok(pagedModel);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar novo registro contábil", description = "Adiciona um novo registro contábil associado a uma conta e centro de custo.")
    @ApiResponse(responseCode = "201", description = "Registro contábil criado com sucesso")
    public ResponseEntity<EntityModel<RegistroContabilResponseDto>> createRegistroContabil(@RequestBody @Valid RegistroContabilRequestDto registroDto) {
        RegistroContabil registro = registroDto.toEntity();

        registro.setConta(Conta.builder().id(registroDto.getContaId()).build());
        registro.setCentroCusto(CentroCusto.builder().id(registroDto.getCentroCustoId()).build());

        RegistroContabil registroCriado = createRegistroContabilService.execute(registro);
        RegistroContabilResponseDto dto = RegistroContabilResponseDto.fromEntity(registroCriado);
        EntityModel<RegistroContabilResponseDto> model = EntityModel.of(dto);
        addLinksToRegistroContabil(model, registroCriado.getId(), dto.getContaId(), dto.getCentroCustoId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(linkTo(methodOn(RegistroContabilController.class).getRegistroContabil(registroCriado.getId())).toUri())
                .body(model);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar registro contábil", description = "Atualiza os dados de um registro contábil existente.")
    @ApiResponse(responseCode = "200", description = "Registro contábil atualizado com sucesso")
    public ResponseEntity<EntityModel<RegistroContabilResponseDto>> updateRegistroContabil(@PathVariable Long id, @RequestBody @Valid RegistroContabilRequestDto registroDto) {
        RegistroContabil registro = registroDto.toEntity();
        registro.setId(id);

        registro.setConta(Conta.builder().id(registroDto.getContaId()).build());
        registro.setCentroCusto(CentroCusto.builder().id(registroDto.getCentroCustoId()).build());

        RegistroContabil registroAtualizado = updateRegistroContabilService.execute(registro);
        RegistroContabilResponseDto dto = RegistroContabilResponseDto.fromEntity(registroAtualizado);
        EntityModel<RegistroContabilResponseDto> model = EntityModel.of(dto);
        addLinksToRegistroContabil(model, id, dto.getContaId(), dto.getCentroCustoId());
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Excluir registro contábil", description = "Remove um registro contábil do sistema pelo seu ID.")
    @ApiResponse(responseCode = "204", description = "Registro contábil excluído com sucesso")
    public ResponseEntity<Void> deleteRegistroContabil(@PathVariable Long id) {
        deleteRegistroContabilService.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/saude-financeira")
    @Operation(summary = "Obter relatório de saúde financeira", description = "Gera um relatório de saúde financeira baseado no centro de custo, mês e ano.")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    public ResponseEntity<EntityModel<RelatorioSaudeDto>> getSaudeFinanceira(
            @Parameter(description = "ID do centro de custo") @RequestParam Long centroCustoId,
            @Parameter(description = "Mês de referência (1-12)") @RequestParam int mes,
            @Parameter(description = "Ano de referência") @RequestParam int ano) {

        RelatorioSaudeDto relatorio = relatorioContabilService.gerarRelatorio(centroCustoId, mes, ano);
        EntityModel<RelatorioSaudeDto> model = EntityModel.of(relatorio);

        model.add(linkTo(methodOn(RegistroContabilController.class).getSaudeFinanceira(centroCustoId, mes, ano)).withSelfRel());
        model.add(linkTo(methodOn(CentroCustoController.class).getCentroCusto(centroCustoId)).withRel("centro-custo"));
        model.add(linkTo(methodOn(RegistroContabilController.class).getRegistrosContabeis(0, Sort.Direction.ASC, 10, null, centroCustoId, null, null)).withRel("registros-filtrados"));

        return ResponseEntity.ok(model);
    }

    private void addLinksToRegistroContabil(EntityModel<RegistroContabilResponseDto> model, Long id, Long contaId, Long centroCustoId) {
        model.add(linkTo(methodOn(RegistroContabilController.class).getRegistroContabil(id)).withSelfRel());
        model.add(linkTo(methodOn(RegistroContabilController.class).updateRegistroContabil(id, null)).withRel("update"));
        model.add(linkTo(methodOn(RegistroContabilController.class).deleteRegistroContabil(id)).withRel("delete"));
        if (contaId != null) {
            model.add(linkTo(methodOn(ContaController.class).getConta(contaId)).withRel("conta"));
        }
        if (centroCustoId != null) {
            model.add(linkTo(methodOn(CentroCustoController.class).getCentroCusto(centroCustoId)).withRel("centro-custo"));
        }
        model.add(linkTo(methodOn(VendasController.class).getVendas(0, Sort.Direction.ASC, 10, null, id)).withRel("vendas"));
        model.add(linkTo(methodOn(RegistroContabilController.class).getRegistrosContabeis(0, Sort.Direction.ASC, 10, null, null, null, null)).withRel("collection"));
    }
}