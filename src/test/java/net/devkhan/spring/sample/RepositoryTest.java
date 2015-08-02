package net.devkhan.spring.sample;

import com.google.common.collect.Lists;
import net.devkhan.spring.sample.configuration.SampleConfiguration;
import net.devkhan.spring.sample.model.Note;
import net.devkhan.spring.sample.model.Tag;
import net.devkhan.spring.sample.repository.NoteRepository;
import net.devkhan.spring.sample.repository.TagRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by KHAN on 2015-08-03.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SampleConfiguration.class)
public class RepositoryTest {

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private NoteRepository noteRepository;

	@Before
	@Transactional
	public void setup() {
		noteRepository.deleteAll();
		tagRepository.deleteAll();

		Tag tag1 = tagRepository.save(new Tag("Spring projects"));
		Tag tag2 = tagRepository.save(new Tag("Apache projects"));
		Tag tag3 = tagRepository.save(new Tag("Open source"));

		Note note1 = new Note("Spring Boot", "Takes an opinionated view of building production-ready Spring applications.");
		note1.addTags(tag1, tag3);
		noteRepository.save(note1);
		Note note2 = new Note("Spring Framework", "Core support for dependency injection, transaction management, web applications, data access, messaging, testing and more.");
		note2.addTags(tag1, tag3);
		noteRepository.save(note2);
		Note note3 = new Note("Spring Integration", "Extends the Spring programming model to support the well-known Enterprise Integration Patterns.");
		note3.addTags(tag1, tag3);
		noteRepository.save(note3);
		Note note4 = new Note("Tomcat", "Apache Tomcat is an open source software implementation of the Java Servlet and JavaServer Pages technologies.");
		note4.addTags(tag2, tag3);
		noteRepository.save(note4);

		entityManager.flush();

		entityManager.detach(tag1);
		entityManager.detach(tag2);
		entityManager.detach(tag3);

		entityManager.detach(note1);
		entityManager.detach(note2);
		entityManager.detach(note3);
		entityManager.detach(note4);
	}

	@Test
	@Transactional
	public void noteTest() {
		Note note1 = noteRepository.findOne(2l);
		List<Tag> tags = note1.getTags();
		assertEquals(tags.size(), 2);

		List<Note> notes = Lists.newArrayList(noteRepository.findAll());
		assertEquals(notes.size(), 4);

	}
}
