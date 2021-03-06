package com.staff.service;

import com.staff.api.dao.ICrudDao;
import com.staff.api.entity.IEntity;
import com.staff.api.service.ICrudService;
import com.staff.api.sort.ISort;
import com.staff.api.specification.ISpecification;

import java.util.List;

public abstract class CrudService<T> implements ICrudService<T> {

    ICrudDao<T> dao;

    @Override
    public void saveOrUpdate(IEntity<T> entity, ISpecification<T> specification) {

        if (Read(specification)==null) {
            this.dao.save(entity);
        } else {
            this.dao.update(entity);
        }
    }

    @Override
    public void delete(ISpecification<T> specification) {
        this.dao.delete(specification);
    }

    @Override
    public List<T> Find(ISpecification<T> specification) {
        return this.dao.Find(specification);
    }

    @Override
    public List<T> FindWithPaging(ISpecification<T> specification, ISort sort, int page, int pageSize) {
        return this.dao.FindWithPaging(specification, sort, page, pageSize);
    }

    @Override
    public T Read(ISpecification<T> specification) {
        return this.dao.Read(specification);
    }

    @Override
    public int Count(ISpecification<T> specification) {
        return this.dao.Count(specification);
    }

}
