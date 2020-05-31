package pl.cwikla.bazy.projekt;

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
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public static void main(String[] args) throws IOException, InterruptedException {
        new DBFiller().updateDB();
    }

    public void updateDB() throws IOException, InterruptedException {
        DataDownloader downloader = new DataDownloader();
        session.set(getSession());
        Transaction tx = session.get().beginTransaction();

        LocalDate date = getLastUpdate();

        DataParser.Result result;
        while (date.isBefore(LocalDate.parse("2020-02-28"))) {
            try (InputStream inputStream = downloader.downloadFile(date)) {
                result = new DataParser().parseData(inputStream);
                while (result.hasNextRecord()) {
                    result.moveToNextRecord();
                    if (!result.getValue("denominazione_provincia").equals(LINE_TO_SKIP)) {
                        addDataRecord(result);
                    }
                }
            }
            date = date.plusDays(1);
        }

        tx.commit();
        session.get().close();
        session.remove();
    }

    private LocalDate getLastUpdate() {
        CriteriaBuilder criteriaBuilder = session.get().getCriteriaBuilder();
        CriteriaQuery<DataRecord> criteriaQuery = criteriaBuilder.createQuery(DataRecord.class);
        Root<DataRecord> root = criteriaQuery.from(DataRecord.class);
        criteriaQuery
                .select(root)
                .orderBy(criteriaBuilder.desc(root.get("date")));
        Query query = session.get().createQuery(criteriaQuery);

        LocalDate lastUpdate;
        try{
            lastUpdate = ((DataRecord) query.setFirstResult(0).setMaxResults(1).getSingleResult()).getDate();
            lastUpdate = lastUpdate.plusDays(1);
        }catch (NoResultException nre){
            lastUpdate = LocalDate.parse("2020-02-24");
        }
        return lastUpdate;
    }

    private void addDataRecord(DataParser.Result result) {
        LocalDate date = LocalDateTime.parse(result.getValue("data")).toLocalDate();
        int numberOfCases = Integer.parseInt(result.getValue("totale_casi"));
        DataRecord dataRecord = new DataRecord(date, numberOfCases);
        String provinceName = result.getValue("denominazione_provincia");
        dataRecord.setProvince(getOrCreateProvince(provinceName, result));
        session.get().save(dataRecord);
    }

    private Province getOrCreateProvince(String provinceName, DataParser.Result result) {
        String code = result.getValue("codice_provincia");

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
            String regionName = result.getValue("denominazione_regione");
            province.setRegion(getOrCreateRegion(regionName, result));
            session.get().save(province);

        }
        return province;
    }

    private Region getOrCreateRegion(String regionName, DataParser.Result result) {
        String code = result.getValue("codice_regione");

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
            String stateName = result.getValue("stato");
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
