package org.jboss.arquillian.container.weld.ee.embedded_1_1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.weld.ee.embedded_1_1.entities.Dog;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author Michal Huniewicz
 * 
 */
@RunWith(Arquillian.class)
public class JpaLifecycleEventExecuterTestCase {

	@PersistenceContext
	private EntityManager entityManager;

	@Deployment
	public static Archive<?> createTestArchive()
			throws IllegalArgumentException, IOException {
		return ShrinkWrap
				.create(JavaArchive.class)
				.addAsManifestResource(
						new File(
								"src/test/resources/META-INF/persistence-test.xml"),
						ArchivePaths.create("persistence.xml"))
				.addClasses(Dog.class, JpaLifecycleEventExecuterTestCase.class);
	}

	@Test
	public void shouldInjectNonNullEntityManager() {
		assertNotNull(
				"Entity manager injected via @PersistenceContext should not be null.",
				entityManager);
	}

	@Test
	public void shouldPersistAndRetrieveEntity() {
		entityManager.getTransaction().begin();
		Dog dog = new Dog("Sega");
		entityManager.persist(dog);
		entityManager.getTransaction().commit();

		Dog retrievedDog = (Dog) entityManager
				.createQuery("FROM Dog where name = :name")
				.setParameter("name", "Sega").getResultList().get(0);
		assertEquals(
				"Created and persisted dog should be the same as retrieved dog.",
				dog, retrievedDog);
	}
}
