package ra.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ra.entity.User;
import ra.entity.Product;
import ra.entity.Order;
import ra.dto.DashboardStats;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@Repository
public class AdminRepository {

    @Autowired
    private SessionFactory sessionFactory;

    // User Statistics
    public Long getTotalUsers() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(u) FROM User u", Long.class);
            return query.getSingleResult();
        }
    }

    public Long getActiveUsers() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(u) FROM User u WHERE u.isActive = true", Long.class);
            return query.getSingleResult();
        }
    }

    public List<User> getAllUsers() {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User u ORDER BY u.createdAt DESC", User.class);
            return query.getResultList();
        }
    }

    // Product Statistics
    public Long getTotalProducts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(p) FROM Product p", Long.class);
            return query.getSingleResult();
        }
    }

    public List<Product> getAllProducts() {
        try (Session session = sessionFactory.openSession()) {
            Query<Product> query = session.createQuery("FROM Product p ORDER BY p.createdAt DESC", Product.class);
            return query.getResultList();
        }
    }

    // Order Statistics
    public Long getTotalOrders() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(o) FROM Order o", Long.class);
            return query.getSingleResult();
        }
    }

    public Long getPendingOrders() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(o) FROM Order o WHERE o.orderStatus = 'PENDING'", Long.class);
            return query.getSingleResult();
        }
    }

    public Double getTotalRevenue() {
        try (Session session = sessionFactory.openSession()) {
            Query<Double> query = session.createQuery("SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o WHERE o.orderStatus = 'DELIVERED'", Double.class);
            return query.getSingleResult();
        }
    }

    public Double getMonthlyRevenue(int year, int month) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o " +
                    "WHERE o.orderStatus = 'DELIVERED' " +
                    "AND YEAR(o.createdAt) = :year AND MONTH(o.createdAt) = :month";
            Query<Double> query = session.createQuery(hql, Double.class);
            query.setParameter("year", year);
            query.setParameter("month", month);
            return query.getSingleResult();
        }
    }

    public Double getYearlyRevenue(int year) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT COALESCE(SUM(o.totalAmount), 0.0) FROM Order o " +
                    "WHERE o.orderStatus = 'DELIVERED' AND YEAR(o.createdAt) = :year";
            Query<Double> query = session.createQuery(hql, Double.class);
            query.setParameter("year", year);
            return query.getSingleResult();
        }
    }

    public List<Order> getAllOrders() {
        try (Session session = sessionFactory.openSession()) {
            Query<Order> query = session.createQuery("FROM Order o ORDER BY o.createdAt DESC", Order.class);
            return query.getResultList();
        }
    }

    // Dashboard Statistics
    public DashboardStats getDashboardStats() {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        return new DashboardStats(
                getTotalUsers(),
                getTotalProducts(),
                getTotalOrders(),
                getTotalRevenue(),
                getActiveUsers(),
                getPendingOrders(),
                getMonthlyRevenue(currentYear, currentMonth),
                getYearlyRevenue(currentYear)
        );
    }
}