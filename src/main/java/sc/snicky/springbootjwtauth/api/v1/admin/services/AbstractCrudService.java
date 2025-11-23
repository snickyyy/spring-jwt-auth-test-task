package sc.snicky.springbootjwtauth.api.v1.admin.services;

import org.springframework.data.domain.Page;

import java.util.Optional;

public interface AbstractCrudService <T, ID> {
    T create(T entity);
    Optional<T> read(ID id);
    Long count();
    Page<T> readAll(int page, int size);
    void delete(ID id);
}
