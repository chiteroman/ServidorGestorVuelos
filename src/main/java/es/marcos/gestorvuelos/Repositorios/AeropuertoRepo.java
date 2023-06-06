package es.marcos.gestorvuelos.Repositorios;

import es.marcos.gestorvuelos.Model.Aeropuerto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AeropuertoRepo extends JpaRepository<Aeropuerto, Long> {
    @Query("select hash from Admin")
    String getAdminHash();
}
