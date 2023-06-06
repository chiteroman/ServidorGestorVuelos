package es.marcos.gestorvuelos.Repositorios;

import es.marcos.gestorvuelos.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, Long> {
    Usuario findUsuarioByAndroidId(String androidId);
}
