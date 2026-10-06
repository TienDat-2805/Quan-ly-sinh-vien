package HD.educaze.hibernateDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class HibernateGenericDao<K, T> {
    private final Class<T> entityClass;
    private final EntityManager entityManager;
    protected HibernateGenericDao(Class<T> entityClass, EntityManager entityManager) {
        this.entityClass = entityClass;
        this.entityManager = entityManager;
    }
    protected EntityManager getEntityManager() { return entityManager; }
    public T create(T entity) { entityManager.persist(entity); return entity; }
    public T update(T entity) { return entityManager.merge(entity); }
    public void deleteByPk(K id) {
        T entity = entityManager.find(entityClass, id);
        if (entity == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Class not found");
        entityManager.remove(entity);
    }
    public List<T> search(String query, Integer lowerLimit, Integer upperLimit, String orderBy, String orderType) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteria = cb.createQuery(entityClass);
        Root<T> root = criteria.from(entityClass);
        criteria.select(root);
        if (query != null && !query.isBlank()) criteria.where(cb.like(cb.lower(root.get("name")), "%" + query.toLowerCase(java.util.Locale.ROOT) + "%"));
        String field = orderBy != null && List.of("name", "createdTime", "domain", "id").contains(orderBy) ? orderBy : "createdTime";
        criteria.orderBy("ASC".equalsIgnoreCase(orderType) ? cb.asc(root.get(field)) : cb.desc(root.get(field)));
        int offset = lowerLimit == null ? 0 : Math.max(0, lowerLimit);
        int limit = upperLimit == null ? 10 : Math.max(1, Math.min(200, upperLimit));
        return entityManager.createQuery(criteria).setFirstResult(offset).setMaxResults(limit).getResultList();
    }
}
