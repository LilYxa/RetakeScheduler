import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import ru.sfedu.retakescheduler.Main;

public class LogsClientTest {
	private static final Logger log = LogManager.getLogger(LogsClientTest.class.getName());
	private Main client;
	@BeforeAll
	public void initTest() {
		client = new Main();
	}

	@AfterAll
	public void afterTest() {
		client = null;
	}
	@Test
	public void logBasicSystemInfo() {
		client.logBasicSystemInfo();

		assertTrue(log.isDebugEnabled());
		assertTrue(log.isInfoEnabled());
		assertTrue(log.isErrorEnabled());
	}

//	@Rule
//	public TestWatcher watcher = new TestWatcher() {
//		@Override
//		protected void succeeded(Description description) {
//			log.debug(description.getMethodName() + " - OK");
//		}
//
//		@Override
//		protected void failed(Throwable e, Description description) {
//			log.debug(description.getMethodName() + " - FAIL");
//		}
//	};
}
