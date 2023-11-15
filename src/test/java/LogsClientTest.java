import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import ru.sfedu.retakescheduler.RetakeScheduler;
import static org.junit.Assert.*;

public class LogsClientTest {
	private static final Logger log = LogManager.getLogger(RetakeScheduler.class);
	private RetakeScheduler client;
	@Before
	public void initTest() {
		client = new RetakeScheduler();
	}

	@After
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

	@Rule
	public TestWatcher watcher = new TestWatcher() {
		@Override
		protected void succeeded(Description description) {
			log.debug(description.getMethodName() + " - OK");
		}

		@Override
		protected void failed(Throwable e, Description description) {
			log.debug(description.getMethodName() + " - FAIL");
		}
	};
}
