import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import ru.sfedu.retakescheduler.utils.YamlConfigUtil;

import java.io.IOException;

public class YamlConfigUtilTest {
	private static final Logger log = LogManager.getLogger(YamlConfigUtilTest.class);
	@Test
	public void testGetProperty() {
		log.info("testGetProperty[1]: start test");
		try {
			String username = YamlConfigUtil.getProperty("database.username");
			log.debug("testGetProperty[2]: username = {}", username);

		} catch (IOException e) {
			log.error("testGetProperty[3]: {}", e.getMessage());
		}
	}
}
