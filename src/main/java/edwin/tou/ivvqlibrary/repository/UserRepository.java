package edwin.tou.ivvqlibrary.repository;

import edwin.tou.ivvqlibrary.domain.User;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    boolean existsByApiKey(UUID apiKey);

    User findByApiKey(UUID apiKey);

    boolean existsByUsername(String username);
}
