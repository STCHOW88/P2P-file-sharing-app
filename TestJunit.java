import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class TestJunit {
   @Test
   public void testString() {
      String str= "Junit, Hello world!" ;
      assertEquals("Junit, Hello world!",str);
   }
}