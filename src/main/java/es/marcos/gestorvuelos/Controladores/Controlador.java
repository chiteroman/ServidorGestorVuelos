package es.marcos.gestorvuelos.Controladores;

import com.fasterxml.jackson.databind.node.ObjectNode;
import es.marcos.gestorvuelos.Model.Aeropuerto;
import es.marcos.gestorvuelos.Model.Usuario;
import es.marcos.gestorvuelos.Model.Vuelo;
import es.marcos.gestorvuelos.Model.data.AndroidIdData;
import es.marcos.gestorvuelos.Model.data.UsuarioData;
import es.marcos.gestorvuelos.Repositorios.AeropuertoRepo;
import es.marcos.gestorvuelos.Repositorios.UsuarioRepo;
import es.marcos.gestorvuelos.Repositorios.VueloRepo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/")
@CrossOrigin(origins = "*")
public class Controlador {
    @Autowired
    private AeropuertoRepo aeropuertoRepo;
    @Autowired
    private VueloRepo vueloRepo;
    @Autowired
    private UsuarioRepo usuarioRepo;

    @GetMapping("/aeropuertos")
    public ResponseEntity<?> getAll() {
        var list = aeropuertoRepo.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/vuelos")
    public ResponseEntity<?> getAllVuelos() {
        var list = vueloRepo.findAll();
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @GetMapping("/vuelos/{id_aeropuerto}")
    public ResponseEntity<?> getVuelosById(@PathVariable long id_aeropuerto) {
        List<Vuelo> list = vueloRepo.findAllByAeropuertoSalida_Id(id_aeropuerto);
        return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
    }

    @PostMapping("/vuelos/reservar")
    public ResponseEntity<?> reservarVuelo(@RequestBody UsuarioData data) {
        if (StringUtils.isBlank(data.getAndroidId())) return ResponseEntity.badRequest().build();
        Optional<Vuelo> optional = vueloRepo.findById(data.getVuelo_id());
        if (optional.isEmpty()) return ResponseEntity.badRequest().build();
        Vuelo vuelo = optional.get();
        Usuario usuario = usuarioRepo.findUsuarioByAndroidId(data.getAndroidId());
        if (usuario == null) {
            usuario = new Usuario(data.getAndroidId());
            usuario = usuarioRepo.saveAndFlush(usuario);
        }
        vuelo.getUsuarios().add(usuario);
        vueloRepo.saveAndFlush(vuelo);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/vuelos/reservar/cancelar")
    public ResponseEntity<?> cancelarReserva(@RequestBody UsuarioData data) {
        if (StringUtils.isBlank(data.getAndroidId())) return ResponseEntity.badRequest().build();
        Usuario usuario = usuarioRepo.findUsuarioByAndroidId(data.getAndroidId());
        Vuelo vuelo = vueloRepo.findByUsuarios_AndroidIdAndId(data.getAndroidId(), data.getVuelo_id());
        if (usuario == null || vuelo == null) return ResponseEntity.badRequest().build();
        if (!Objects.equals(vuelo.getId(), data.getVuelo_id())) return ResponseEntity.noContent().build();
        boolean result = vuelo.getUsuarios().remove(usuario);
        vueloRepo.saveAndFlush(vuelo);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/usuarios/vuelos")
    public ResponseEntity<?> getVuelosUsuario(@RequestBody AndroidIdData data) {
        List<Vuelo> vuelos = vueloRepo.findByUsuarios_AndroidId(data.getAndroidId());
        return vuelos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(vuelos);
    }

    @PostMapping("/aeropuertos/add")
    public ResponseEntity<?> add(@RequestBody ObjectNode node, @RequestHeader String authorization) {
        if (node.size() != 2 || !node.has("nombre") || !node.has("url")) {
            return ResponseEntity.badRequest().build();
        }
        authorization = DigestUtils.sha256Hex(authorization);
        String adminHash = aeropuertoRepo.getAdminHash();
        System.out.println("Admin hash: " + adminHash);
        if (!StringUtils.equalsIgnoreCase(authorization, adminHash)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String nombre = node.get("nombre").asText();
        String url = node.get("url").asText();
        if (StringUtils.isBlank(nombre) || StringUtils.isBlank(url)) {
            return ResponseEntity.badRequest().build();
        }
        Aeropuerto aeropuerto = new Aeropuerto();
        aeropuerto.setNombre(nombre);
        aeropuerto.setUrl(url);
        aeropuerto = aeropuertoRepo.saveAndFlush(aeropuerto);
        return ResponseEntity.ok(aeropuerto);
    }
}
