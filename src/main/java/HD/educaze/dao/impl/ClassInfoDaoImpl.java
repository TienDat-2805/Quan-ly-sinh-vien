package HD.educaze.dao.impl;

import HD.educaze.dao.IClassInfoDao;
import HD.educaze.hibernateDao.HibernateGenericDao;
import HD.educaze.model.ClassInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class ClassInfoDaoImpl extends HibernateGenericDao<String, ClassInfo> implements IClassInfoDao {

    private static Logger logger = LogManager.getLogger(ClassInfoDaoImpl.class);

    public ClassInfoDaoImpl(final EntityManager entityManager) {
        super(ClassInfo.class, entityManager);
    }

    @Override
    public ClassInfo save(ClassInfo classInfo) {
        logger.info("Saving ClassInfo: {}", classInfo.getName());
        return create(classInfo);
    }

    @Override
    public ClassInfo update(ClassInfo classInfo) {
        logger.info("Updating ClassInfo: {}", classInfo.getId());
        return super.update(classInfo);
    }

    @Override
    public void delete(String id) {
        logger.info("Deleting ClassInfo: {}", id);
        deleteByPk(id);
    }

    @Override
    public ClassInfo getById(String id) {
        logger.info("Getting ClassInfo by id: {}", id);
        try {
            return getEntityManager().find(ClassInfo.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public ClassInfo getByName(String name) {
        logger.info("Getting ClassInfo by name: {}", name);
        try {
            TypedQuery<ClassInfo> query = getEntityManager()
                    .createQuery("SELECT c FROM ClassInfo c WHERE c.name = :name", ClassInfo.class)
                    .setParameter("name", name);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<ClassInfo> getAll() {
        logger.info("Getting all ClassInfo");
        try {
            TypedQuery<ClassInfo> query = getEntityManager()
                    .createQuery("SELECT c FROM ClassInfo c ORDER BY c.createdTime DESC", ClassInfo.class);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ClassInfo> getByDomain(String domain) {
        logger.info("Getting ClassInfo by domain: {}", domain);
        try {
            TypedQuery<ClassInfo> query = getEntityManager()
                    .createQuery("SELECT c FROM ClassInfo c WHERE c.domain = :domain ORDER BY c.createdTime DESC", ClassInfo.class)
                    .setParameter("domain", domain);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ClassInfo> search(String query, Integer lowerLimit, Integer upperLimit, String orderBy, String orderType) {
        logger.info("Searching ClassInfo with query: {}", query);
        return search(query, upperLimit, lowerLimit, orderBy, orderType);
    }

    @Override
    public Integer getCount(String query) {
        logger.info("Getting ClassInfo count");
        try {
            TypedQuery<Long> countQuery = getEntityManager()
                    .createQuery("SELECT COUNT(c) FROM ClassInfo c", Long.class);
            return countQuery.getSingleResult().intValue();
        } catch (NoResultException e) {
            return 0;
        }
    }
}
