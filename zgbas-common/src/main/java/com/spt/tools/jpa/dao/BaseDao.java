/**
 * 
 */
package com.spt.tools.jpa.dao;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author huangjian
 * @param <T>
 *
 */
@NoRepositoryBean
public  interface  BaseDao<T>  extends PagingAndSortingRepository<T, Long>, JpaSpecificationExecutor<T>{
	default T findOne(Long id) {
		if (id == null)
			return null;
		return findById(id).orElse(null);
	}
	
	default void delete(Long id) {
		deleteById(id);
	}
	
	default boolean exists(Long id) {
		return existsById(id);
	}
}
