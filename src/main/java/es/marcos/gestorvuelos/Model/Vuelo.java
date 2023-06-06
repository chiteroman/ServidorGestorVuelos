package es.marcos.gestorvuelos.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Vuelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
    @JsonManagedReference
    private Avion avion;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
    @JsonManagedReference
    private Aeropuerto aeropuertoSalida;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = false)
    @JsonManagedReference
    private Aeropuerto aeropuertoDestino;
    @Column(nullable = false)
    private ZonedDateTime horaSalidaOrigen;
    @Column(nullable = false)
    private ZonedDateTime horaLlegadaDestino;
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "usuarios_vuelos", joinColumns = @JoinColumn(name = "vuelo_id"), inverseJoinColumns = @JoinColumn(name = "usuario_id"))
    @JsonManagedReference
    private List<Usuario> usuarios = new LinkedList<>();
}
