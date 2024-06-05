package database;

import commands.exceptions.CommandException;
import dataStructs.FormOfEducation;
import dataStructs.StudyGroup;
import dataStructs.User;
import database.undo.UndoLog;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

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
public class StudyGroupDatabase implements Database<StudyGroup, Long> {

    private final SessionFactory factory;
    private final Function<String, Long> sessionToIdFunction;

    Map<Long, Stack<UndoLog<StudyGroup>>> undoLogStacksByClient = new HashMap<>();

    private final Collection<StudyGroup> collection;

    public StudyGroupDatabase(SessionFactory factory, Collection<StudyGroup> collection, Function<String, Long> sessionToIdFunction) {

        this.collection = collection;
        this.sessionToIdFunction = sessionToIdFunction;

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
    public String getInfo() {
        //TODO: fill this
        return "TODO";
    }

    @Override
    public void clear(String sessionStr) {
        Long clientId = sessionToIdFunction.apply(sessionStr);
        boolean canClear = getCollection().stream().allMatch(studyGroup -> studyGroup.getOwner() == clientId);

        if (!canClear) {
            return;
        }

        List<StudyGroup> deleted = List.copyOf(getCollection());

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            deleted.forEach(session::delete);
            session.getTransaction().commit();
        }
        getCollection().clear();

        pushToUndoStack(UndoLog.deletedElements(deleted), sessionStr);
    }

    @Override
    public List<StudyGroup> removeGreaterOrLowerThanPrimaryKey(Long id, boolean greater, String sessionStr) {
        //TODO: Prompt to delete all that can or not (need to show what elements will not be deleted with their owners)

        List<StudyGroup> toDelete = getCollection()
                .stream()
                .filter(studyGroup -> (studyGroup.getId() > id) == greater)
                .toList();

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            toDelete.forEach(session::delete);
            session.getTransaction().commit();
        }

        pushToUndoStack(UndoLog.deletedElements(toDelete), sessionStr);

        return toDelete;
    }

    @Override
    public List<StudyGroup> getElementsDescendingByPrimaryKey() {
        return getCollection().stream()
                .sorted(Comparator.comparingLong(StudyGroup::getId))
                .toList();
    }

    @Override
    public StudyGroup updateElementByPrimaryKey(Long id, StudyGroup new_element, String sessionStr) throws IllegalArgumentException {
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

        pushToUndoStack(UndoLog.changedElement(new_element,deleted), sessionStr);

        return deleted;
    }

    @Override
    public StudyGroup removeElementByPrimaryKey(Long id, String sessionStr) throws IllegalArgumentException {
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

        pushToUndoStack(UndoLog.deletedElements(deleted),sessionStr);

        return deleted;
    }

    @Override
    public void addElement(StudyGroup group, String sessionStr) {
        getCollection().add(group);

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.save(group);
            session.getTransaction().commit();
        }

        pushToUndoStack(UndoLog.addedElements(group), sessionStr);
    }

    @Override
    public List<StudyGroup> getElements() {
        return getCollection()
                .stream()
                .sorted(Comparator.comparingLong(StudyGroup::getId))
                .toList();
    }

    @Override
    public void pushToUndoStack(UndoLog<StudyGroup> log, String session) {
        if (log.getChangesList().isEmpty()) {
            return;
        }
        System.out.println("added to stack " + log);

        undoLogStacksByClient.get(sessionToIdFunction.apply(session)).push(log);
    }

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
            log.getChangesList().forEach(change -> {
                if (change.isAdded()) {
                    remover.accept(change.getElement(), entityManager);
                } else {
                    adder.accept(change.getElement(), entityManager);
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

    @Override
    public boolean popUndoStackWithSession(String session) {
        return false;
    }

    public boolean checkAlreadyExistsUser(String userName) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root =query.from(User.class);

             query.select(root).where(builder.and(
                    builder.equal(root.get("user_name"), userName)));

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
                    builder.equal(root.get("user_name"), userName),
                    builder.equal(root.get("password"), password)));

            User user = entityManager.createQuery(query).getSingleResult();

            return user.getId();
        } catch (NoResultException e) {
            throw new CommandException("Wrong user name or password!");
        }
    }

    public OptionalLong getMinStudentCount() {
        return getCollection()
                .stream()
                .filter(studyGroup -> studyGroup.getStudentsCount() != null)
                .mapToInt(StudyGroup::getStudentsCount)
                .mapToLong(Integer::toUnsignedLong)
                .min();
    }

    public long getSumOfAverageMark() {
        return getCollection().stream()
                .mapToLong(StudyGroup::getAverageMark)
                .summaryStatistics()
                .getSum();
    }

    public long getCountLessThanFormOfEducation(FormOfEducation formOfEducation) {
        return getCollection().stream().filter(group -> group.getFormOfEducation().ordinal() < formOfEducation.ordinal()).count();
    }
}
