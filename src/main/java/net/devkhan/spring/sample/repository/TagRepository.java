package net.devkhan.spring.sample.repository;

import net.devkhan.spring.sample.model.Tag;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by KHAN on 2015-08-03.
 */
public interface TagRepository extends CrudRepository<Tag, Long> {
}
