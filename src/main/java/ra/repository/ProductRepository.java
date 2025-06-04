package ra.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.entity.Product;

import java.util.List;

@Repository
public class ProductRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public List<Product> findAll() {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Product p ORDER BY p.createdAt DESC";
            Query<Product> query = session.createQuery(hql, Product.class);
            return query.getResultList();
        }
    }

    public List<Product> findByIsActive(boolean isActive) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Product p WHERE p.isActive = :isActive ORDER BY p.createdAt DESC";
            Query<Product> query = session.createQuery(hql, Product.class);
            query.setParameter("isActive", isActive);
            return query.getResultList();
        }
    }

    public Product findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Product.class, id);
        }
    }

    public Product save(Product product) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.save(product);
            session.getTransaction().commit();
            return product;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public Product update(Product product) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.update(product);
            session.getTransaction().commit();
            return product;
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            Product product = session.get(Product.class, id);
            if (product != null) {
                session.delete(product);
            }
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
            throw e;
        } finally {
            session.close();
        }
    }

    public List<Product> searchByName(String keyword) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM Product p WHERE p.name LIKE :keyword AND p.isActive = true ORDER BY p.name";
            Query<Product> query = session.createQuery(hql, Product.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.getResultList();
        }
    }
}