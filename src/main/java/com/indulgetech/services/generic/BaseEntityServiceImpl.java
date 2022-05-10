package com.indulgetech.services.generic;


import com.indulgetech.exceptions.CustomException;
import com.indulgetech.exceptions.EntityType;
import com.indulgetech.exceptions.ExceptionType;
import com.indulgetech.models.common.generics.BaseEntity;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

/**
 * @param <T> entity type
 */
@RequiredArgsConstructor
public abstract class BaseEntityServiceImpl<K extends Serializable & Comparable<K>, E extends BaseEntity<K, ?>>
	implements BaseEntityService<K, E> {
	
	/**
	 * Class of the entity, determined from the generic parameters.
	 */
	private Class<E> objectClass;


    private JpaRepository<E, K> repository;

	@SuppressWarnings("unchecked")
	public BaseEntityServiceImpl(JpaRepository<E, K> repository) {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.objectClass = (Class<E>) genericSuperclass.getActualTypeArguments()[1];
		this.repository = repository;
	}
	
	protected final Class<E> getObjectClass() {
		return objectClass;
	}


	public E getById(K id) {
		return repository.getById(id);
	}

	
	public void save(E entity) throws ServiceException {
		repository.saveAndFlush(entity);
	}

	public Optional<E> findById(K id) {
		return repository.findById(id);
	}


	public void create(E entity) throws ServiceException {
		save(entity);
	}


	public void update(E entity) throws ServiceException {
		save(entity);
	}
	

	public void delete(E entity) throws ServiceException {
		repository.delete(entity);
	}
	
	
	public void flush() {
		repository.flush();
	}
	

	
	public List<E> list() {
		return repository.findAll();
	}
	

	public Long count() {
		return repository.count();
	}
	
	protected E saveAndFlush(E entity) {
		return repository.saveAndFlush(entity);
	}


	/**
	 * Returns a new RuntimeException
	 *
	 * @param entityType
	 * @param exceptionType
	 * @param args
	 * @return
	 */
	public RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
		return CustomException.throwException(entityType, exceptionType, args);
	}

	/**
	 * Returns a new RuntimeException
	 *
	 * @param entityType
	 * @param exceptionType
	 * @param args
	 * @return
	 */
	public RuntimeException exceptionWithId(EntityType entityType, ExceptionType exceptionType, Integer id, String... args) {
		return CustomException.throwExceptionWithId(entityType, exceptionType, id, args);
	}

}