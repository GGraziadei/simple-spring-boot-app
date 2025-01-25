package com.ggraziadei.test.simple_spring_boot_app.services;

public interface DAOPattern<T, K> {
    T save(T entity);
    T findById(K id);
    T update(T entity);
    void delete(K id);
}
