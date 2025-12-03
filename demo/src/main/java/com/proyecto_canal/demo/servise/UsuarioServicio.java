package com.proyecto_canal.demo.servise;

import com.proyecto_canal.demo.modelo.UsuarioModel;
import com.proyecto_canal.demo.modelo.UsuarioModel.TipoAlerta; // Necesario para la comprobación
import com.proyecto_canal.demo.repositorio.UsuarioRepositorio;
import com.proyecto_canal.demo.service.EmailService; // Importamos el servicio de correo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    
    // INYECCIÓN DEL SERVICIO DE CORREO: Necesario para enviar las alertas críticas
    @Autowired
    private EmailService emailService;

    /**
     * Obtiene todos los reportes de sensores.
     * @return Una lista de UsuarioModel.
     */
    public List<UsuarioModel> obtenerTodos() {
        return usuarioRepositorio.findAll();
    }

    /**
     * Obtiene reportes filtrados por una fecha específica.
     * @param fecha La fecha a buscar.
     * @return Una lista de UsuarioModel para esa fecha.
     */
    public List<UsuarioModel> obtenerPorFecha(LocalDate fecha) {
        return usuarioRepositorio.findByFecha(fecha);
    }

    /**
     * Obtiene reportes filtrados por TipoAlerta.
     * @param tipoAlerta El nombre del tipo de alerta (BAJA, MEDIA, ALTA).
     * @return Una lista de UsuarioModel con ese tipo de alerta.
     */
    public List<UsuarioModel> obtenerPorTipo(String tipoAlerta) {
        try {
            UsuarioModel.TipoAlerta tipo = UsuarioModel.TipoAlerta.valueOf(tipoAlerta.toUpperCase());
            return usuarioRepositorio.findByTipoAlerta(tipo);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    /**
     * Filtra reportes combinando fecha y tipo de alerta.
     * @param fecha Fecha para filtrar (opcional).
     * @param tipoAlerta Tipo de alerta para filtrar (opcional).
     * @return Lista de reportes que cumplen ambos criterios.
     */
    public List<UsuarioModel> filtrar(LocalDate fecha, String tipoAlerta) {
        List<UsuarioModel> resultados = obtenerTodos();

        if (fecha != null) {
            resultados = resultados.stream()
                .filter(s -> s.getFecha().equals(fecha))
                .collect(Collectors.toList());
        }

        if (tipoAlerta != null && !tipoAlerta.isEmpty()) {
            try {
                UsuarioModel.TipoAlerta tipo = UsuarioModel.TipoAlerta.valueOf(tipoAlerta.toUpperCase());
                resultados = resultados.stream()
                    .filter(s -> s.getTipoAlerta().equals(tipo))
                    .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
           
            }
        }

        return resultados;
    }

    /**
     * Obtiene un reporte por su ID.
     * @param id El ID del reporte.
     * @return El UsuarioModel si existe, o null.
     */
    public UsuarioModel obtenerPorId(Long id) {
        return usuarioRepositorio.findById(id).orElse(null);
    }

    /**
     * Guarda el registro del sensor.
     * * LÓGICA DE ALERTA AUTOMÁTICA: Después de guardar, verifica si el TipoAlerta 
     * es ALTA para enviar una notificación crítica.
     * * @param usuario El objeto UsuarioModel con los datos del sensor.
     * @return El reporte guardado.
     */
    public UsuarioModel guardar(UsuarioModel usuario) {
        // 1. Guardar el nuevo registro en la base de datos.
        // Se asume que UsuarioModel asigna automáticamente TipoAlerta (BAJA/MEDIA/ALTA) 
        // basado en el nivelDeAguaCm antes de ser guardado.
        UsuarioModel reporteGuardado = usuarioRepositorio.save(usuario);
        
        // 2. Comprobación y Envío de Alerta (Detección automática de peligro)
        if (reporteGuardado.getTipoAlerta() == TipoAlerta.ALTA) {
            System.out.println("⚠️ ALERTA CRÍTICA: Nivel ALTA detectado. Enviando notificación.");
            
            // 3. Llamar al servicio de correo para notificar al administrador
            emailService.sendHighAlertEmail(
                reporteGuardado.getSensorId(), 
                reporteGuardado.getNivelDeAguaCm()
            );
        }
        
        return reporteGuardado;
    }

    /**
     * Elimina un reporte por su ID.
     * @param id El ID del reporte a eliminar.
     */
    public void eliminar(Long id) {
        usuarioRepositorio.deleteById(id);
    }
}
