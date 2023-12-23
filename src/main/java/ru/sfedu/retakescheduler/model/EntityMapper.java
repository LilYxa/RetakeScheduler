package ru.sfedu.retakescheduler.model;

@FunctionalInterface
public interface EntityMapper<T> {
	Object[] mapEntity(T entity);
}
