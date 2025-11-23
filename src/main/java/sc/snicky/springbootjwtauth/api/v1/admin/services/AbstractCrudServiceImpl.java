package sc.snicky.springbootjwtauth.api.v1.admin.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public abstract class AbstractCrudServiceImpl <T, ID> implements AbstractCrudService<T, ID> {
    abstract protected JpaRepository<T, ID> getRepo();

    @Override
    public T create(T entity) {
        getRepo().save(entity);
        return null;
    }

    @Override
    public Long count() {
        return getRepo().count();
    }

    @Override
    public Page<T> readAll(int page, int size) {
        return getRepo().findAll(PageRequest.of(page, size));
    }

    @Override
    public Optional<T> read(ID id) {
        return getRepo().findById(id);
    }

    @Override
    public void delete(ID id) {
        getRepo().deleteById(id);
    }
}
