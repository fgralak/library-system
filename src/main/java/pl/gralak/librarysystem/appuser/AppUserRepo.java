package pl.gralak.librarysystem.appuser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AppUserRepo extends JpaRepository<AppUser, Long>
{
    @Transactional
    @Query("SELECT a FROM AppUser a WHERE a.username = ?1 AND a.authProvider = ?2")
    AppUser findByUsernameAndProvider(String username, Provider provider);

    @Transactional
    @Modifying
    @Query("UPDATE AppUser a SET a.isEnabled = true WHERE a.username = ?1")
    void enableAppUser(String username);
}
