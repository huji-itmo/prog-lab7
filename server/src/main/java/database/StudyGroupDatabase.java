package database;

import commands.exceptions.CommandException;
import dataStructs.FormOfEducation;
import dataStructs.StudyGroup;
import dataStructs.User;
import dataStructs.communication.SessionByteArray;
import database.undo.UndoLog;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Getter
public class StudyGroupDatabase implements Database<StudyGroup, Long> {

    private final SessionFactory factory;
    private final Function<SessionByteArray, String> sessionToUserNameFunction;

    Map<SessionByteArray, Stack<UndoLog<StudyGroup>>> undoLogStacksBySession = Collections.synchronizedMap(new HashMap<>());

    private final Collection<StudyGroup> collection = Collections.synchronizedCollection(new ArrayDeque<>());

    @Setter
    public ConfirmDeleteInterface<StudyGroup> confirmDelete;

    public StudyGroupDatabase(SessionFactory factory, Function<SessionByteArray, String> sessionToUserNameFunction) {

        this.sessionToUserNameFunction = sessionToUserNameFunction;

        try (Session session = factory.openSession()) {

            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
            CriteriaQuery<StudyGroup> query = entityManager.getCriteriaBuilder().createQuery(StudyGroup.class);

            List<StudyGroup> list = session.createQuery(query.select(query.from(StudyGroup.class))).getResultList();
            list.forEach(group -> group.setOwner(group.getOwner().strip()));
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
    public void clear(SessionByteArray sessionStr) {
        String userName = sessionToUserNameFunction.apply(sessionStr);
        List<StudyGroup> elementsToDelete = getCollection().stream().toList();

        List<StudyGroup> permittedToDelete = elementsToDelete
                .stream()
                .filter(studyGroup -> studyGroup.getOwner().equals(userName))
                .toList();

        if (permittedToDelete.size() != elementsToDelete.size()) {
            if (!confirmDelete.confirm(elementsToDelete, sessionStr)) {
                return;
            }
        }

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            permittedToDelete.forEach(session::delete);
            session.getTransaction().commit();
        }

        permittedToDelete.forEach(getCollection()::remove);

        pushToUndoStack(UndoLog.deletedElements(elementsToDelete), sessionStr);
    }

    /**
     * @param id         primary key
     * @param greater    true for greater, false for smaller
     * @param sessionStr session
     * @return list of deleted elements
     */
    @Override
    public List<StudyGroup> removeGreaterOrLowerThanPrimaryKey(Long id, boolean greater, SessionByteArray sessionStr) {
        String userName = sessionToUserNameFunction.apply(sessionStr);

        List<StudyGroup> elementsToDelete = getCollection()
                .stream()
                .filter(studyGroup -> (studyGroup.getId() > id) == greater)
                .toList();

        List<StudyGroup> permittedToDelete = elementsToDelete
                .stream()
                .filter(studyGroup -> studyGroup.getOwner().equals(userName))
                .toList();

        if (permittedToDelete.size() != elementsToDelete.size()) {
            if (!confirmDelete.confirm(permittedToDelete, sessionStr)) {
                return List.of();
            }
        }

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
            elementsToDelete.forEach(entityManager::remove);
            session.getTransaction().commit();
        }

        elementsToDelete.forEach(getCollection()::remove);

        pushToUndoStack(UndoLog.deletedElements(elementsToDelete), sessionStr);

        return elementsToDelete;
    }

    @Override
    public List<StudyGroup> getElementsDescendingByPrimaryKey() {
        return getCollection().stream()
                .sorted(Comparator.comparingLong(StudyGroup::getId).reversed())
                .toList();
    }

    @Override
    public StudyGroup updateElementByPrimaryKey(Long id, StudyGroup new_element, SessionByteArray sessionStr) throws IllegalArgumentException {
        String userName = sessionToUserNameFunction.apply(sessionStr);

        StudyGroup deleted = getCollection()
                .stream()
                .filter(studyGroup -> studyGroup.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Element with id: " + id + " doesn't exist."));

        if (deleted.getOwner().equals(userName)) {
            throw new IllegalArgumentException("Can't change this element because it belongs to a different user.");
        }

        new_element.setId(id);
        new_element.setOwner(userName);

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.merge(new_element);
            session.getTransaction().commit();
        }

        getCollection().remove(deleted);
        getCollection().add(new_element);


        pushToUndoStack(UndoLog.changedElement(new_element,deleted), sessionStr);

        return deleted;
    }

    @Override
    public StudyGroup removeElementByPrimaryKey(Long id, SessionByteArray sessionStr) throws IllegalArgumentException {
        StudyGroup deleted = getCollection()
                .stream()
                .filter(studyGroup -> studyGroup.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Element with id: " + id + " doesn't exist."));

        if (!deleted.getOwner().equals(sessionToUserNameFunction.apply(sessionStr))) {
            throw new IllegalArgumentException("Can't remove this element because it belongs to a different user.");
        }

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            session.delete(deleted);
            session.getTransaction().commit();
        }

        getCollection().remove(deleted);

        pushToUndoStack(UndoLog.deletedElements(deleted),sessionStr);

        return deleted;
    }

    @Override
    public void addElement(StudyGroup group, SessionByteArray sessionStr) {
        group.setOwner(sessionToUserNameFunction.apply(sessionStr).strip());

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            Long id = (Long) session.save(group);
            session.getTransaction().commit();

            group.setId(id);
        }

        getCollection().add(group);

        pushToUndoStack(UndoLog.addedElements(group), sessionStr);
    }

    @Override
    public List<StudyGroup> getElements() {
        return getCollection()
                .stream()
                .sorted(Comparator.comparingLong(StudyGroup::getId))
                .toList();
    }

    private void pushToUndoStack(UndoLog<StudyGroup> log, SessionByteArray session) {
        if (log.getChangesList().isEmpty()) {
            return;
        }
        System.out.println("added to stack " + log);

        undoLogStacksBySession.get(session).push(log);
    }

    public boolean undo(UndoLog<StudyGroup> log) throws RuntimeException {
        if (undoLogStacksBySession.isEmpty())
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
                    session.save(change.getElement());
                    adder.accept(change.getElement(), entityManager);
                }
            });

            session.getTransaction().commit();
        } catch (EntityExistsException e) {
            throw e;
        }

        return true;
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public String registerNewUser(String userName, String password) {
        if (checkAlreadyExistsUser(userName)) {
            throw new CommandException("User with name " + userName + " already exists!");
        }

        try (Session session = factory.openSession()) {
            session.beginTransaction();
            User user = User.builder().userName(userName).password(password).build();

            session.save(user);
            session.getTransaction().commit();

            return user.getUserName();
        }
    }

    @Override
    public boolean popUndoStackWithSession(SessionByteArray session) {

        Stack<UndoLog<StudyGroup>> undoLogsByClient = getUndoLogStacksBySession().get(session);

        return undo(undoLogsByClient.pop());
    }

    public boolean checkAlreadyExistsUser(String userName) {
        try (Session session = factory.openSession()) {
            session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root =query.from(User.class);

             query.select(root).where(builder.and(
                     builder.equal(root.get("userName"), userName)));

             return !(session.createQuery(query).getResultList().isEmpty());
        }
    }

    public String login(String userName, String password) {
        try (Session session = factory.openSession()) {
            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

            CriteriaBuilder builder =entityManager.getCriteriaBuilder();
            CriteriaQuery<User> query = builder.createQuery(User.class);
            Root<User> root =query.from(User.class);

            query.select(root).where(builder.and(
                    builder.equal(root.get("userName"), userName),
                    builder.equal(root.get("password"), password)));

            User user = entityManager.createQuery(query).getSingleResult();

            return user.getUserName();
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
        return getCollection()
                .stream()
                .filter(group -> group.getFormOfEducation() != null)
                .filter(group -> group.getFormOfEducation().ordinal() < formOfEducation.ordinal())
                .count();
    }
}
