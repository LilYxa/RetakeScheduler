import org.junit.Test;
import ru.sfedu.retakescheduler.Planet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class PlanetTest {

	private Planet planet = new Planet();
	@Test
	public void testListOfPlanets() throws IOException {
		List<String> planets = List.of("Earth","Mars","Jupiter","Neptune");
		assertEquals(planets, planet.listOfPlanets());
	}

	@Test
	public void testNameOfMonth() throws IOException {
		Map<Integer, String> expectedMonths = new HashMap<>();
		expectedMonths.put(1, "January");
		expectedMonths.put(2, "February");
		expectedMonths.put(3, "March");

		assertEquals(expectedMonths, planet.nameOfMonth());
	}
}
