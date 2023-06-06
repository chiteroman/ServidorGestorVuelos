package es.marcos.gestorvuelos.Repositorios;

import es.marcos.gestorvuelos.Model.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VueloRepo extends JpaRepository<Vuelo, Long> {
    List<Vuelo> findAllByAeropuertoSalida_Id(long id);

    List<Vuelo> findByUsuarios_AndroidId(String usuarios_androidId);

    Vuelo findByUsuarios_AndroidIdAndId(String usuarios_androidId, long id);
}
