package database;

import commands.exceptions.CommandException;
import dataStructs.FormOfEducation;
import dataStructs.StudyGroup;
import dataStructs.User;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.id.ForeignGenerator;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class StudyGroupDatabaseInstance implements Database<StudyGroup, Long> {

    private final SessionFactory factory;

     Stack<UndoLog<StudyGroup>> undoLogStacksByClient = new Stack<>();

    private final Collection<StudyGroup> collection;

    @Override
    public void clear() {
        List<StudyGroup> deleted = List.copyOf(getCollection());

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            deleted.forEach(session::delete);
            session.getTransaction().commit();
        }
        getCollection().clear();

        pushToUndoStack(UndoLog.deletedElements(deleted));
    }

    @Override
    public Comparator<Long> getPrimaryKeyComparator() {
        return Long::compareTo;
    }

    @Override
    public Function<StudyGroup, Long> getPrimaryKey() {
        return StudyGroup::getId;
    }

    @Override
    public BiConsumer<StudyGroup, Long> setPrimaryKeyTo() {
        return StudyGroup::setId;
    }

    @Override
    public Class<StudyGroup> getElementClass() {
        return StudyGroup.class;
    }

    @Override
    public String getInfo() {

        return "TODO";
    }

    @Override
    public List<StudyGroup> removeGreaterOrLowerThanPrimaryKey(Long id, boolean greater) {


        List<StudyGroup> toDelete = getCollection()
                .stream()
                .filter(studyGroup -> (studyGroup.getId() > id) == greater)
                .toList();

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            toDelete.forEach(session::delete);
            session.getTransaction().commit();
        }

        pushToUndoStack(UndoLog.deletedElements(toDelete));

        return toDelete;
    }

    @Override
    public String getElementsDescendingByPrimaryKey() {
        return getCollection().stream()
                .sorted(Comparator.comparingLong(StudyGroup::getId))
                .map(str -> str + "\n")
                .collect(Collectors.joining());
    }

    @Override
    public StudyGroup updateElementByPrimaryKey(Long id, StudyGroup new_element) throws IllegalArgumentException {
        StudyGroup deleted = getCollection()
                .stream()
                .filter(studyGroup -> studyGroup.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Element with id: " + id + " doesn't exist."));

        new_element.setId(id);

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.merge(new_element);
            session.getTransaction().commit();
        }

        pushToUndoStack(UndoLog.changedElement(new_element,deleted));

        return deleted;
    }

    public StudyGroupDatabaseInstance(SessionFactory factory, Collection<StudyGroup> collection) {

        this.collection = collection;

        try (Session session = factory.openSession()) {

            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
            CriteriaQuery<StudyGroup> query = entityManager.getCriteriaBuilder().createQuery(StudyGroup.class);

            List<StudyGroup> list = session.createQuery(query.select(query.from(StudyGroup.class))).getResultList();
            list.forEach(System.out::println);
            getCollection().addAll(list);
        }

        this.factory = factory;
    }

    @Override
    public StudyGroup removeElementByPrimaryKey(Long id) throws IllegalArgumentException {
        StudyGroup deleted = getCollection()
                .stream()
                .filter(studyGroup -> studyGroup.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Element with id: " + id + " doesn't exist."));


        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.delete(deleted);
            session.getTransaction().commit();
        }

        pushToUndoStack(UndoLog.deletedElements(deleted));

        return deleted;
    }

    @Override
    public void addElement(StudyGroup group) {
        getCollection().add(group);

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.save(group);
            session.getTransaction().commit();
        }

        pushToUndoStack(UndoLog.addedElements(group));
    }

    @Override
    public String serializeAllElements() {
        return getCollection().stream()
                .map(Objects::toString)
                .map(string -> string + "\n")
                .collect(Collectors.joining());
    }

    @Override
    public void pushToUndoStack(UndoLog<StudyGroup> log) {
        if (log.changesList.isEmpty()) {
            return;
        }
        System.out.println("added to stack " + log);

        undoLogStacksByClient.push(log);
    }

    @Override
    public boolean undo(UndoLog<StudyGroup> log) throws RuntimeException{
        if (undoLogStacksByClient.isEmpty())
            return false;

        BiConsumer<StudyGroup, EntityManager> remover = (studyGroup, entityManager) -> {
            getCollection().remove(studyGroup);
            entityManager.remove(studyGroup);
        };

        BiConsumer<StudyGroup, EntityManager> adder = (studyGroup, entityManager) -> {
            getCollection().add(studyGroup);
            entityManager.merge(studyGroup);
        };

        try (Session session = factory.openSession())
        {
            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
            session.beginTransaction();
            log.changesList.forEach(change -> {
                if (change.isAdded) {
                    remover.accept(change.element, entityManager);
                } else {
                    adder.accept(change.element, entityManager);
                }
            });

            session.getTransaction().commit();
        }

        return true;
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public long registerNewUser(String userName, String password) {
        if (checkAlreadyExistsUser(userName)) {
            throw new CommandException("User with name " + userName + " already exists!");
        }

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            User user = User.builder().user_name(userName).password(password).build();

            Long id = (Long) session.save(user);
            session.getTransaction().commit();

            return id;
        } catch (NoResultException e) {
            return -1;
        }
    }

    public boolean checkAlreadyExistsUser(String userName) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root =query.from(User.class);

             query.select(root).where(builder.and(
                    builder.equal(root.get("name"), userName)));

             return !(session.createQuery(query).getResultList().isEmpty());
        }
    }

    public long login(String userName, String password) {
        try (Session session = factory.openSession()) {
            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

            CriteriaBuilder builder =entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root =query.from(User.class);

            query.select(root).where(builder.and(
                    builder.equal(root.get("name"), userName),
                    builder.equal(root.get("password"), password)));


            User user = entityManager.createQuery(query).getSingleResult();

            return user.getId();
        } catch (NoResultException e) {
            return -1;
        }
    }

    public OptionalInt getMinStudnentCount() {
        return getCollection()
                .stream()
                .filter(studyGroup -> studyGroup.getStudentsCount() != null)
                .mapToInt(StudyGroup::getStudentsCount)
                .min();
    }

    public double getSumOfAverageMark() {
        return getCollection().stream()
                .mapToLong(StudyGroup::getAverageMark)
                .summaryStatistics()
                .getAverage();
    }

    public long getCountLessThanFormOfEducation(FormOfEducation formOfEducation) {
        return getCollection().stream().filter(group -> group.getFormOfEducation().ordinal() < formOfEducation.ordinal()).count();
    }
}
