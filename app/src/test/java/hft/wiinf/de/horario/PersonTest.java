package hft.wiinf.de.horario;

import org.junit.Before;
import org.junit.Test;

import hft.wiinf.de.horario.model.Person;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class PersonTest {
    Person person = new Person();
    //delete all persons from database to ensure corrects test results
   // @BeforeClass
    public static void deleteAllPersons(){
       // SugarRecord.deleteAll(Person.class);
    }
    //sets the Person to the default values to ensure the correct data before each test
    @Before
    public void createStandardPerson(){
        person.setName("Hans Meyer");
        person.setPhoneNumber("12345");
    }
    @Test
    //writes the standard person into database and reads it out (by ID)
    public void searchPersonById(){
         person.save();
        // Person personFromDatabase = SugarRecord.findById(Person.class, person.getId());
       //  assertEquals(person.getId(), personFromDatabase.getId());
       // assertEquals(person.getPhoneNumber(), personFromDatabase.getPhoneNumber());
       // assertEquals(person.getName(),person.getName());

    }
    @Test
    //saves and delete the standard person
    public void deletePerson(){
        person.save();
        person.delete();
      // assertEquals(Person.listAll(Person.class).size(),0);

    }
    //saves and updates the standard person
    public void updatePerson(){
        person.save();
        person.setName("Hanswurst");
        person.save();
  //      Person personFromDatabase = SugarRecord.findById(Person.class, person.getId());
  //      assertEquals(person.getId(), personFromDatabase.getId());
  //      assertEquals(person.getPhoneNumber(), personFromDatabase.getPhoneNumber());
  //      assertEquals(person.getName(),person.getName());
    }

}