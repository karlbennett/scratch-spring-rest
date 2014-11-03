package scratch.spring.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import scratch.spring.webapp.data.UserRepository;
import scratch.user.Address;
import scratch.user.Id;
import scratch.user.User;
import scratch.user.Users;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.concurrent.Callable;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * A controller that maps user RESTful requests.
 *
 * @author Karl Bennett
 */
@RestController
@RequestMapping("/users")
public class UserController implements Users {

    @Autowired
    private UserRepository repository;

    /**
     * Persist a new user using the user object that has been deserialised from the {@code JSON} in the body of the
     * {@code POST} request.
     *
     * This operation will fail if a user exists with the emil supplied in the new user. Also if an ID is supplied it
     * will be ignored.
     *
     * @param user the user to persist.
     * @return the newly persisted user.
     * @throws org.springframework.dao.DataIntegrityViolationException
     *          if the deserialised user contains an unique data that has already been persisted.
     */
    @RequestMapping(method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(CREATED)
    public Callable<Id> asyncCreate(@Valid @RequestBody final User user) {

        return new Callable<Id>() {

            @Override
            public Id call() throws Exception {

                return create(user);
            }
        };
    }

    /**
     * Retrieve the user with the supplied ID.
     *
     * @param id the is of the user to retrieve.
     * @return the requested user.
     * @throws javax.persistence.EntityNotFoundException
     *          if no user exists with the supplied id.
     */
    @RequestMapping(value = "/{id}", method = GET, produces = APPLICATION_JSON_VALUE)
    public Callable<User> asyncRetrieve(@PathVariable final Long id) {

        return new Callable<User>() {

            @Override
            public User call() throws Exception {

                return retrieve(id);
            }
        };
    }

    /**
     * Retrieve all the persisted user.
     *
     * @return all the users that have been persisted.
     */
    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public Callable<Iterable<User>> asyncRetrieve() {

        return new Callable<Iterable<User>>() {

            @Override
            public Iterable<User> call() throws Exception {

                return retrieve();
            }
        };
    }

    /**
     * Updated the user that has been deserialised from the {@code JSON} in the body of the {@code PUT} request.
     *
     * @param id   the ID of the user to update.
     * @param user the deserialised user minus the ID.
     * @return the updated user.
     * @throws javax.persistence.EntityNotFoundException
     *          if no user exists with the supplied id.
     */
    @RequestMapping(value = "/{id}", method = PUT, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    public Callable<String> asyncUpdate(@PathVariable Long id, @Valid @RequestBody final User user) {

        user.setId(id);

        return new Callable<String>() {

            @Override
            public String call() throws Exception {

                update(user);

                return "";
            }
        };
    }

    /**
     * Delete the user with the supplied ID.
     *
     * @param id the ID of the user to delete.
     * @return the delete user.
     * @throws javax.persistence.EntityNotFoundException
     *          if no user exists with the supplied id.
     */
    @RequestMapping(value = "/{id}", method = DELETE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    public Callable<String> asyncDelete(@PathVariable final Long id) {

        return new Callable<String>() {

            @Override
            public String call() throws Exception {

                delete(id);

                return "";
            }
        };
    }

    @RequestMapping(method = DELETE, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    public Callable<String> asyncDeleteAll() {

        return new Callable<String>() {

            @Override
            public String call() throws Exception {

                deleteAll();

                return "";
            }
        };
    }

    @Override
    public Id create(User user) {

        // Null out the ID's to make sure that an attempt is made at a create not an update.
        user.setId(null);

        final Address address = user.getAddress();
        if (null != address) {
            address.setId(null);
        }

        return new Id(repository.save(user));
    }

    @Override
    public User retrieve(Long id) {

        checkExists(id);

        return repository.findOne(id);
    }

    @Override
    public Iterable<User> retrieve() {

        return repository.findAll();
    }

    @Override
    public void update(User user) {

        checkExists(user.getId());

        repository.save(user);
    }

    @Override
    public void delete(Long id) {

        checkExists(id);

        repository.delete(id);
    }

    @Override
    public void deleteAll() {

        repository.deleteAll();
    }

    private void checkExists(Long id) {

        if (!repository.exists(id)) {
            throw new EntityNotFoundException(format("A user with the ID (%d) could not be found.", id));
        }
    }

    public static class ErrorResponse {

        private final String error;

        private final String message;


        public ErrorResponse(String error, String message) {

            this.error = error;
            this.message = message;
        }


        public String getError() {

            return error;
        }

        public String getMessage() {

            return message;
        }
    }

    @ExceptionHandler
    public ErrorResponse handleException(EntityNotFoundException e, HttpServletResponse response) {

        response.setStatus(404);

        return new ErrorResponse(e.getClass().getSimpleName(), e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse handleException(Exception e, HttpServletResponse response) {

        response.setStatus(400);

        return new ErrorResponse(e.getClass().getSimpleName(), e.getMessage());
    }
}
