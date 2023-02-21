package edwin.tou.ivvqlibrary.repository;

import edwin.tou.ivvqlibrary.domain.Borrow;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowRepository extends CrudRepository<Borrow, Long> {
    List<Borrow> findAllByBorrowerUsername(String username);

    boolean existsByIsbn13AndReturnDateIsNull(String isbn13);

    List<Borrow> findAllByIsbn13InAndReturnDateIsNull(Iterable<String> isbn13s);

    Borrow findByIsbn13AndReturnDateIsNull(String isbn13);

    List<Borrow> findAllByIsbn13(String isbn13);
}
