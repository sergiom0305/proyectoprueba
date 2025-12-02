package com.proyecto_canal.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto_canal.demo.modelo.UsuarioModel;
import com.proyecto_canal.demo.servise.UsuarioServicio;

@RestController
@RequestMapping("/api/sensores")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsuarioController {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping
    public ResponseEntity<List<UsuarioModel>> obtenerTodos() {
        List<UsuarioModel> sensores = usuarioServicio.obtenerTodos();
        return ResponseEntity.ok(sensores);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<UsuarioModel>> filtrar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) String tipo) {
        List<UsuarioModel> sensores = usuarioServicio.filtrar(fecha, tipo);
        return ResponseEntity.ok(sensores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioModel> obtenerPorId(@PathVariable Long id) {
        UsuarioModel sensor = usuarioServicio.obtenerPorId(id);
        if (sensor != null) {
            return ResponseEntity.ok(sensor);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<UsuarioModel> crear(@RequestBody UsuarioModel usuario) {
        UsuarioModel nuevoUsuario = usuarioServicio.guardar(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioModel> actualizar(@PathVariable Long id, @RequestBody UsuarioModel usuarioActualizado) {
        UsuarioModel sensor = usuarioServicio.obtenerPorId(id);
        if (sensor != null) {
            usuarioActualizado.setId(id);
            UsuarioModel actualizado = usuarioServicio.guardar(usuarioActualizado);
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        UsuarioModel sensor = usuarioServicio.obtenerPorId(id);
        if (sensor != null) {
            usuarioServicio.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

     @GetMapping("/register")
    public String showRegistrationForm() {
        return "registro"; 
    }


    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; 
    }
}
