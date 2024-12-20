package com.crosska.api.socksApi.dao;

import com.crosska.api.socksApi.model.Sock;
import com.crosska.api.socksApi.util.HibernateSessionFactoryUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.LongSummaryStatistics;

@Repository
public class DAOImpl implements DAO {

    @Override
    public Sock findById(int id) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            return session.get(Sock.class, id);
        }
    }

    public Sock findSingleByColorAndCotton(String color, int cotton) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "FROM Sock WHERE color = :color AND cotton = :cotton";
            Query<Sock> query = session.createQuery(hql, Sock.class);
            query.setParameter("color", color);
            query.setParameter("cotton", cotton);
            List<Sock> socks = query.list();
            try {
                return socks.get(0);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
    }

    @Override
    public void save(Sock sock) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx1 = session.beginTransaction();
            session.save(sock);
            tx1.commit();
        }
    }

    @Override
    public void update(Sock sock) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx1 = session.beginTransaction();
            session.update(sock);
            tx1.commit();
        }
    }

    @Override
    public void delete(Sock sock) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx1 = session.beginTransaction();
            session.delete(sock);
            tx1.commit();
        }
    }

    @Override
    public List<Sock> findAll() {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Sock").list();
        }
    }

    public long findManyWithoutParameters() {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "select sum(s.amount) FROM Sock s";
            Query<Long> query = session.createQuery(hql, Long.class);
            return query.getSingleResult();
        }
    }

    public long findManyWithAllParameters(String color, String comparison, int cotton) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            String hql;
            switch (comparison) {
                case "moreThan":
                    hql = "select sum(s.amount) FROM Sock s WHERE color = :color AND cotton > :cotton";
                    break;
                case "lessThan":
                    hql = "select sum(s.amount) FROM Sock s WHERE color = :color AND cotton < :cotton";
                    break;
                case "equal":
                    hql = "select sum(s.amount) FROM Sock s WHERE color = :color AND cotton = :cotton";
                    break;
                default:
                    return 0;
            }
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("color", color);
            query.setParameter("cotton", cotton);
            return query.getSingleResult();
        }
    }

    public long findManyWithCottonParameters(String comparison, int cotton) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            String hql;
            switch (comparison) {
                case "moreThan":
                    hql = "select sum(s.amount) FROM Sock s WHERE cotton > :cotton";
                    break;
                case "lessThan":
                    hql = "select sum(s.amount) FROM Sock s WHERE cotton < :cotton";
                    break;
                case "equal":
                    hql = "select sum(s.amount) FROM Sock s WHERE cotton = :cotton";
                    break;
                default:
                    return 0;
            }
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("cotton", cotton);
            return query.getSingleResult();
        }
    }

    public long findManyWithColorParameters(String color) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            String hql = "select sum(s.amount) FROM Sock s WHERE color = :color";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("color", color);
            return query.getSingleResult();
        }
    }


}
