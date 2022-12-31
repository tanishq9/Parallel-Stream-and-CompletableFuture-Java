import com.learnjava.completablefuture.CompletableFutureException;
import com.learnjava.service.HelloWorldService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class TestCompletableFuture {

	@Mock
	HelloWorldService helloWorldService = Mockito.mock(HelloWorldService.class);

	@InjectMocks // helloWorldService mock would be injected into completableFutureException
	CompletableFutureException completableFutureException;

	@Test
	void test() {
		MockitoAnnotations.initMocks(this);

		// given
		Mockito.when(helloWorldService.helloWorld()).thenCallRealMethod();

		// when
		String result = completableFutureException.useHandle();

		// then
		Assertions.assertEquals(result, "HELLO WORLD");
	}

	@Test
	void testExceptionHandlingUsingHandle() {
		MockitoAnnotations.initMocks(this);

		// given
		Mockito.when(helloWorldService.helloWorld()).thenThrow(new RuntimeException("Exception"));

		// when
		String result = completableFutureException.useHandle();

		// then
		Assertions.assertEquals(result, "DEFAULT VALUE");
	}

	@Test
	void testExceptionHandlingUsingExceptionally() {
		MockitoAnnotations.initMocks(this);

		// given
		Mockito.when(helloWorldService.helloWorld()).thenThrow(new RuntimeException("Exception"));

		// when
		String result = completableFutureException.useExceptionally();

		// then
		Assertions.assertEquals(result, "EXCEPTION DEFAULT VALUE 2");
	}
}
