package org.primo.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class BaseRepository<T> {
    protected final SessionFactory sessionFactory;

    public BaseRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected <R> R execute(Function<Session, R> function) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                R result = function.apply(session);
                tx.commit();
                return result;
            } catch (Exception e) {
                tx.rollback();
                throw e;
            }
        }
    }

    protected Optional<T> findSingleResult(TypedQuery<T> query) {
        List<T> resultList = query.getResultList();
        return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList.get(0));
    }

    protected List<T> findListResult(TypedQuery<T> query) {
        return query.getResultList();
    }
}
