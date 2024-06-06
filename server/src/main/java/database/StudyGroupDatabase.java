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

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
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
            entityManager.getTransaction().begin();
            CriteriaQuery<StudyGroup> query = entityManager.getCriteriaBuilder().createQuery(StudyGroup.class);

            List<StudyGroup> list = session.createQuery(query.select(query.from(StudyGroup.class))).getResultList();
            list.forEach(group -> group.setOwner(group.getOwner().strip()));
            entityManager.flush();
            list.forEach(System.out::println);
            getCollection().addAll(list);

            entityManager.getTransaction().commit();
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
        deleteElements(permittedToDelete);

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

        List<StudyGroup> elementsToDelete = getCollection().stream()
                .filter(studyGroup -> (studyGroup.getId() > id) == greater)
                .toList();

        List<StudyGroup> permittedToDelete = elementsToDelete.stream()
                .filter(studyGroup -> studyGroup.getOwner().equals(userName))
                .toList();

        if (permittedToDelete.size() != elementsToDelete.size()) {
            if (!confirmDelete.confirm(permittedToDelete, sessionStr)) {
                return List.of();
            }
        }

        deleteElements(permittedToDelete);

        pushToUndoStack(UndoLog.deletedElements(elementsToDelete), sessionStr);

        return elementsToDelete;
    }

    @Override
    public StudyGroup updateElementByPrimaryKey(Long id, StudyGroup new_element, SessionByteArray sessionStr) throws IllegalArgumentException {
        String userName = sessionToUserNameFunction.apply(sessionStr);

        StudyGroup deleted = getCollection()
                .stream()
                .filter(studyGroup -> studyGroup.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Element with id: " + id + " doesn't exist."));

        if (!deleted.getOwner().equals(userName)) {
            throw new IllegalArgumentException("Can't change this element because it belongs to a different user.");
        }

        new_element.setId(id);
        new_element.setOwner(userName);

        try (Session session = factory.openSession()) {
            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            getCollection().add(entityManager.merge(new_element));
            entityManager.flush();
            entityManager.getTransaction().commit();
        }

        getCollection().remove(deleted);

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

        deleteElements(List.of(deleted));

        pushToUndoStack(UndoLog.deletedElements(deleted),sessionStr);

        return deleted;
    }

    @Override
    public void addElement(StudyGroup group, SessionByteArray sessionStr) {
        group.setOwner(sessionToUserNameFunction.apply(sessionStr).strip());

        try (Session session = factory.openSession()) {
            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(group);
            entityManager.flush();
            entityManager.getTransaction().commit();
        }

        getCollection().add(group);

        pushToUndoStack(UndoLog.addedElements(group), sessionStr);
    }


    public void deleteElements(Iterable<StudyGroup> elements) {
        try (Session session = factory.openSession()) {
            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();
            elements.forEach(entityManager::remove);
            entityManager.flush();
            entityManager.getTransaction().commit();
        }
        elements.forEach(getCollection()::remove);
    }

    //----------------------------UNDO------------------------------------
    @Override
    public boolean popUndoStackWithSession(SessionByteArray session) {

        Stack<UndoLog<StudyGroup>> undoLogsByClient = getUndoLogStacksBySession().get(session);

        return undo(undoLogsByClient.pop());
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
            getCollection().add(entityManager.merge(studyGroup));
        };

        try (Session session = factory.openSession())
        {
            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();
            entityManager.getTransaction().begin();

            log.getChangesList().forEach(change -> {
                if (change.isAdded()) {
                    remover.accept(change.getElement(), entityManager);
                } else {
                    adder.accept(change.getElement(), entityManager);
                }
            });

            entityManager.flush();
            entityManager.getTransaction().commit();
        }

        return true;
    }

    //----------------------------LOGIN AND REGISTER------------------------------------


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

    public boolean checkAlreadyExistsUser(String userName) {
        try (Session session = factory.openSession()) {
            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

            User user = entityManager.find(User.class, userName);

            return user != null;
        }
    }

    public String login(String userName, String password) {
        try (Session session = factory.openSession()) {
            EntityManager entityManager = session.getEntityManagerFactory().createEntityManager();

            User user = entityManager.getReference(User.class, userName);

            if (!user.getPassword().equals(password)) {
                throw new CommandException("Wrong user name or password!");
            }

            return user.getUserName();
        } catch (EntityNotFoundException e) {
            throw new CommandException("Wrong user name or password!");
        }
    }


    //------------------------DATA OPERATIONS-----------------------------

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

    @Override
    public List<StudyGroup> getElementsDescendingByPrimaryKey() {
        return getCollection().stream()
                .sorted(Comparator.comparingLong(StudyGroup::getId).reversed())
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return getCollection().stream().anyMatch(studyGroup -> studyGroup.getId() == id);
    }

    @Override
    public List<StudyGroup> getElements() {
        return getCollection()
                .stream()
                .sorted(Comparator.comparingLong(StudyGroup::getId))
                .toList();
    }
}