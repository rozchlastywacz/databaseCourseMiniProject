package pl.cwikla.bazy.projekt.datamanage;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import pl.cwikla.bazy.projekt.model.DataRecord;
import pl.cwikla.bazy.projekt.model.Province;
import pl.cwikla.bazy.projekt.model.Region;
import pl.cwikla.bazy.projekt.model.State;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class DBGetter {
    private static final SessionFactory SESSION_FACTORY;
    private final ThreadLocal<Session> session = new ThreadLocal<>();

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            SESSION_FACTORY = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return SESSION_FACTORY.openSession();
    }

    public List<State> getAllStates() {
        session.set(getSession());
        Transaction tx = session.get().beginTransaction();


        CriteriaBuilder criteriaBuilder = session.get().getCriteriaBuilder();
        CriteriaQuery<State> criteriaQuery = criteriaBuilder.createQuery(State.class);
        Root<State> root = criteriaQuery.from(State.class);
        criteriaQuery.select(root);
        TypedQuery<State> query = session.get().createQuery(criteriaQuery);

        List<State> stateList = query.getResultList();
        stateList.forEach(Hibernate::initialize);
        tx.commit();
        session.get().close();
        session.remove();

        return stateList;
    }

    public List<Region> getAllRegionsIn(Object state) {
        if (state instanceof String) {
            return Collections.emptyList();
        }
        session.set(getSession());
        Transaction tx = session.get().beginTransaction();


        CriteriaBuilder criteriaBuilder = session.get().getCriteriaBuilder();
        CriteriaQuery<Region> criteriaQuery = criteriaBuilder.createQuery(Region.class);
        Root<Region> root = criteriaQuery.from(Region.class);
        criteriaQuery
                .select(root)
                .where(criteriaBuilder.equal(root.get("state"), state));
        TypedQuery<Region> query = session.get().createQuery(criteriaQuery);

        List<Region> regionList = query.getResultList();
        regionList.forEach(Hibernate::initialize);
        tx.commit();
        session.get().close();
        session.remove();

        return regionList;
    }

    public List<Province> getAllProvincesIn(Object region) {
        if (region instanceof String) {
            return Collections.emptyList();
        }
        session.set(getSession());
        Transaction tx = session.get().beginTransaction();


        CriteriaBuilder criteriaBuilder = session.get().getCriteriaBuilder();
        CriteriaQuery<Province> criteriaQuery = criteriaBuilder.createQuery(Province.class);
        Root<Province> root = criteriaQuery.from(Province.class);
        criteriaQuery
                .select(root)
                .where(criteriaBuilder.equal(root.get("region"), region));
        TypedQuery<Province> query = session.get().createQuery(criteriaQuery);

        List<Province> provincesList = query.getResultList();
        provincesList.forEach(Hibernate::initialize);
        tx.commit();
        session.get().close();
        session.remove();

        return provincesList;
    }

    public List<DataRecord> getDataFrom(State state, Region region, Province province) {
        session.set(getSession());
        Transaction tx = session.get().beginTransaction();
        String hql = "SELECT f from DataRecord as f"
                + "    inner join f.province as q"
                + "    inner join q.region as w"
                + "    inner join w.state as e"
                + "    where e.name = :stateName and w.name = :regionName and q.name = :provinceName";

        TypedQuery<DataRecord> query = session.get().createQuery(hql, DataRecord.class);
        query.setParameter("stateName", state.getName());
        query.setParameter("regionName", region.getName());
        query.setParameter("provinceName", province.getName());
        List<DataRecord> dataRecords = query.getResultList();
        dataRecords.forEach(Hibernate::initialize);
        tx.commit();
        session.get().close();
        session.remove();

        return dataRecords;
    }

    public List<DataRecord> getDataFrom(State state, Region region) {
        session.set(getSession());
        Transaction tx = session.get().beginTransaction();
        String hql = "SELECT f from DataRecord as f"
                + "    inner join f.province as q"
                + "    inner join q.region as w"
                + "    inner join w.state as e"
                + "    where e.name = :stateName and w.name = :regionName";

        TypedQuery<DataRecord> query = session.get().createQuery(hql, DataRecord.class);
        query.setParameter("stateName", state.getName());
        query.setParameter("regionName", region.getName());
        List<DataRecord> dataRecords = query.getResultList();
        dataRecords.forEach(Hibernate::initialize);
        tx.commit();
        session.get().close();
        session.remove();

        return dataRecords;
    }

    public List<DataRecord> getDataFrom(State state) {
        session.set(getSession());
        Transaction tx = session.get().beginTransaction();
        String hql = "SELECT f from DataRecord as f"
                + "    inner join f.province as q"
                + "    inner join q.region as w"
                + "    inner join w.state as e"
                + "    where e.name = :stateName";

        TypedQuery<DataRecord> query = session.get().createQuery(hql, DataRecord.class);
        query.setParameter("stateName", state.getName());
        List<DataRecord> dataRecords = query.getResultList();
        dataRecords.forEach(Hibernate::initialize);
        tx.commit();
        session.get().close();
        session.remove();

        return dataRecords;
    }

    public LocalDate getLastUpdate(String stateName) {
        session.set(getSession());
        Transaction tx = session.get().beginTransaction();
        String hql = "select dt.date from DataRecord as dt" +
                "    inner join dt.province as pr" +
                "    inner join pr.region as rg" +
                "    inner join rg.state as st" +
                "    where st.name = :stateName" +
                "    order by dt.date desc";
        TypedQuery<LocalDate> query = session.get().createQuery(hql, LocalDate.class);
        query.setParameter("stateName", stateName);
        query.setMaxResults(1);
        LocalDate lastUpdate = null;
        try {
            lastUpdate = query.getSingleResult();
        } catch (NoResultException nre) {
            if (stateName.equals("ITA"))
                lastUpdate = LocalDate.parse("2020-02-24");
            else if(stateName.equals("USA"))
                lastUpdate = LocalDate.parse("2020-01-21");
            else
                lastUpdate = LocalDate.parse("2020-01-01");
        }
        tx.commit();
        session.get().close();
        session.remove();

        return lastUpdate;
    }
}
