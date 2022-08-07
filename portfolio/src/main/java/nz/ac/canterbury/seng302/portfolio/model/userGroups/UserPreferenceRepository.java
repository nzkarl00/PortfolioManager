package nz.ac.canterbury.seng302.portfolio.model.userGroups;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferenceRepository extends CrudRepository<UserPreference, Integer> {
    UserPreference findById(int id);
}
