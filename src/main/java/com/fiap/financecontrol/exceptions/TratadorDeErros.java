package com.fiap.financecontrol.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.detalhar(ex));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> runTimeException(RuntimeException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(detalhar(ex));
    }
    @ExceptionHandler(ContaInexistente.class)
    public ResponseEntity<Map<String, String>> ContaInexistenteException(ContaInexistente ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(detalhar(ex));
    }

    @ExceptionHandler(ContaFinanceiraNaoEncontradaException.class)
    public ResponseEntity<Map<String, String>> ContaFinanceiraNaoEncontradaException(ContaFinanceiraNaoEncontradaException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detalhar(ex));
    }

    @ExceptionHandler(CentroCustoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> CentroCustoNaoEncontradoException(CentroCustoNaoEncontradoException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detalhar(ex));
    }

    @ExceptionHandler(VendaNaoEncontradaException.class)
    public ResponseEntity<Map<String, String>> VendaNaoEncontradaException(VendaNaoEncontradaException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detalhar(ex));
    }

    @ExceptionHandler(RegistroContabilNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> RegistroContabilNaoEncontradoException(RegistroContabilNaoEncontradoException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(detalhar(ex));
    }











    public Map<String, String> detalhar(Exception ex) {
        Map<String, String> map = new HashMap();
        map.put("erro", ex.getClass().getSimpleName());
        map.put("mensagem", ex.getMessage());
        return map;
    }
}
