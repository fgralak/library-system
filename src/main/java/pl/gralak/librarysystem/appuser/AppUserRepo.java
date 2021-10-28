package pl.gralak.librarysystem.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, Long>
{
    AppUser findByUsername(String username);

    @Query("SELECT a FROM AppUser a WHERE a.username = ?1 AND a.authProvider = ?2")
    AppUser findByUsernameAndProvider(String username, Provider provider);
}
