package com.iot.parking.utils;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.iot.parking.models.AccessToken;
import com.iot.parking.models.Entry;
import com.iot.parking.models.Rate;
import com.iot.parking.models.Reservation;
import com.iot.parking.models.User;

public class DatabaseUtil {

	static StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
	static Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();
	static SessionFactory factory = meta.getSessionFactoryBuilder().build();

	public static User getUserByUsername(String username) {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		Query<User> query = session.createQuery("FROM User u WHERE u.username = :username", User.class);
		query.setParameter("username", username);
		User user = query.uniqueResult();
		tx.commit();
		return user;
	}

	public static User saveUser(User user) {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(user);
		tx.commit();
		return user;
	}
	
	public static AccessToken saveAccessToken(User user) {
		AccessToken accessToken = user.getAccessToken();
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		Query<AccessToken> query = session.createQuery("FROM AccessToken a WHERE a.userId = :id", AccessToken.class);
		query.setParameter("id", user.getId());
		AccessToken dbToken = query.uniqueResult();
		if(dbToken != null) {
			dbToken.setToken(accessToken.getToken());
			accessToken = dbToken;
		}
		session.saveOrUpdate(accessToken);
		tx.commit();
		return accessToken;
	}

	public static User getUserByToken(String token) {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();

		Query<User> query = session.createNativeQuery("SELECT users.* FROM users INNER JOIN access_tokens ON users.id = access_tokens.user_id WHERE access_tokens.token = :token", User.class);
		
		query.setParameter("token", token);
		User user = query.uniqueResult();
		tx.commit();
		return user;
	}

	public static List<Reservation> getReservations() {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		Query<Reservation> query = session.createQuery("FROM Reservation", Reservation.class);
		List<Reservation> reservations = query.getResultList();
		tx.commit();
		return reservations;
	}

	public static List<Reservation> getReservationsByUser(User user) {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		Query<Reservation> query = session.createQuery("FROM Reservation r WHERE r.userId = :id", Reservation.class);
		query.setParameter("id", user.getId());
		List<Reservation> reservations = query.getResultList();
		tx.commit();
		return reservations;
	}

	public static Reservation makeReservation(Reservation reservation) {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(reservation);
		tx.commit();
		return reservation;
	}
	
	public static Reservation saveReservation(Reservation reservation) {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(reservation);
		tx.commit();
		return reservation;
	}
	
	
	
	public static Reservation checkReservation(String licensePlate, String status) {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		Query<Reservation> query;
		if(status != null) {
			query = session.createQuery("FROM Reservation r WHERE r.licensePlate = :licensePlate AND r.status = :status", Reservation.class);
			query.setParameter("status", status);
		}
		else {
			query = session.createQuery("FROM Reservation r WHERE r.licensePlate = :licensePlate", Reservation.class);

		}
		query.setParameter("licensePlate", licensePlate);
		query.setFirstResult(0);
        query.setMaxResults(1);
        Reservation reservation = query.uniqueResult();
		tx.commit();
		return reservation;
	}
	
	public static Boolean deleteReservation(Long reservationId) {
			Session session = factory.getCurrentSession();
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("DELETE FROM Reservation r WHERE r.id = :id");
			query.setParameter("id", reservationId);
	        query.executeUpdate();
			tx.commit();
			return true;
	}
	
	public static Rate getRate(String spotId) {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		Query<Rate> query = session.createQuery("FROM Rate rate WHERE rate.spotId = :spotId", Rate.class);
		query.setParameter("spotId", spotId);
		Rate rate = query.uniqueResult();
		tx.commit();
		return rate;
	}

	public static List<Rate> getRates(List<String> spotIds) {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		Query<Rate> query = session.createQuery("FROM Rate rate WHERE rate.spotId IN :spotIds", Rate.class);
		List<Rate> rates = query.getResultList();
		tx.commit();
		return rates;
	}

	public static Rate saveRate(Rate rate) {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(rate);
		tx.commit();
		return rate;
	}

	public static List<Entry> getEntries() {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		Query<Entry> query = session.createQuery("FROM Entry", Entry.class);
		List<Entry> entries = query.getResultList();
		tx.commit();
		return entries;
	}
	
	public static Entry getActiveEntry(String licensePlate) {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		Query<Entry> query = session.createQuery("FROM Entry e WHERE e.licensePlate = :licensePlate AND e.leaveTime IS NULL", Entry.class);
		query.setParameter("licensePlate", licensePlate);
		Entry entry = query.uniqueResult();
		tx.commit();
		return entry;
	}

	public static Entry saveEntry(Entry entry) {
		Session session = factory.getCurrentSession();
		Transaction tx = session.beginTransaction();
		session.saveOrUpdate(entry);
		tx.commit();
		return entry;
	}

}
