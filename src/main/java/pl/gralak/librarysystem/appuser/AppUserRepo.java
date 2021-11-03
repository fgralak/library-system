package pl.gralak.librarysystem.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface AppUserRepo extends JpaRepository<AppUser, Long>
{
    @Query("SELECT a FROM AppUser a WHERE a.username = ?1 AND a.authProvider = ?2")
    AppUser findByUsernameAndProvider(String username, Provider provider);

    @Modifying
    @Query("UPDATE AppUser a SET a.enabled = true WHERE a.username = ?1")
    void enableAppUser(String username);

    @Query("SELECT a FROM AppUser a WHERE a.role = pl.gralak.librarysystem.appuser.Role.ROLE_EMPLOYEE")
    List<AppUser> findAllEmployees();

    @Query("SELECT a FROM AppUser a WHERE a.username = ?1 and a.role = pl.gralak.librarysystem.appuser.Role.ROLE_USER")
    Optional<AppUser> findByUsername(String username);
}
