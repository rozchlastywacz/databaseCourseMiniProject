package pl.cwikla.bazy.projekt.datamanage;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import pl.cwikla.bazy.projekt.model.DataRecord;
import pl.cwikla.bazy.projekt.model.Province;
import pl.cwikla.bazy.projekt.model.Region;
import pl.cwikla.bazy.projekt.model.State;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.time.LocalDate;

public class DBFiller {
    private static final String LINE_TO_SKIP = "In fase di definizione/aggiornamento";
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


    public boolean updateDB(DataSource dataSource) throws IOException, InterruptedException {
        session.set(getSession());
        Transaction tx = session.get().beginTransaction();


        while(dataSource.moveToNextRecord()){
            addDataRecord(dataSource);
        }

        tx.commit();
        session.get().close();
        session.remove();
        return true;
    }

    private void addDataRecord(DataSource dataSource) {
        LocalDate date = dataSource.getDate();
        int numberOfCases = dataSource.getNumberOfCases();
        DataRecord dataRecord = new DataRecord(date, numberOfCases);
        String provinceName = dataSource.getProvinceName();
        dataRecord.setProvince(getOrCreateProvince(provinceName, dataSource));
        session.get().save(dataRecord);
    }

    private Province getOrCreateProvince(String provinceName, DataSource dataSource) {
        String code = dataSource.getProvinceCode();

        CriteriaBuilder criteriaBuilder = session.get().getCriteriaBuilder();
        CriteriaQuery<Province> criteriaQuery = criteriaBuilder.createQuery(Province.class);
        Root<Province> root = criteriaQuery.from(Province.class);
        criteriaQuery
                .select(root)
                .where(criteriaBuilder.equal(root.get("name"), provinceName))
                .where(criteriaBuilder.equal(root.get("code"), code));
        Query query = session.get().createQuery(criteriaQuery);

        Province province;
        try {
            province = (Province) query.getSingleResult();
        } catch (NoResultException nre) {
            province = new Province(provinceName, code);
            String regionName = dataSource.getRegionName();
            province.setRegion(getOrCreateRegion(regionName, dataSource));
            session.get().save(province);

        }
        return province;
    }

    private Region getOrCreateRegion(String regionName, DataSource dataSource) {
        String code = dataSource.getRegionCode();

        CriteriaBuilder criteriaBuilder = session.get().getCriteriaBuilder();
        CriteriaQuery<Region> criteriaQuery = criteriaBuilder.createQuery(Region.class);
        Root<Region> root = criteriaQuery.from(Region.class);
        criteriaQuery
                .select(root)
                .where(criteriaBuilder.equal(root.get("name"), regionName))
                .where(criteriaBuilder.equal(root.get("code"), code));
        Query query = session.get().createQuery(criteriaQuery);
        Region region;
        try {
            region = (Region) query.getSingleResult();
        } catch (NoResultException nre) {
            region = new Region(regionName, code);
            String stateName = dataSource.getStateName();
            region.setState(getOrCreateState(stateName));
            session.get().save(region);
        }
        return region;
    }

    private State getOrCreateState(String stateName) {
        CriteriaBuilder criteriaBuilder = session.get().getCriteriaBuilder();
        CriteriaQuery<State> criteriaQuery = criteriaBuilder.createQuery(State.class);
        Root<State> root = criteriaQuery.from(State.class);
        criteriaQuery
                .select(root)
                .where(criteriaBuilder.equal(root.get("name"), stateName));
        Query query = session.get().createQuery(criteriaQuery);
        State state;
        try {
            state = (State) query.getSingleResult();
        } catch (NoResultException nre) {
            state = new State(stateName);
            session.get().save(state);
        }
        return state;
    }

}
