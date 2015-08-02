package net.devkhan.spring.sample.repository;

import net.devkhan.spring.sample.model.Note;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by KHAN on 2015-08-03.
 */
public interface NoteRepository extends CrudRepository<Note, Long> {
}
