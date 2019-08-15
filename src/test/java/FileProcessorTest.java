import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.fail;

public class FileProcessorTest {
    @Test
    public void testValidXMLFile () {
        FileProcessor fileProcessor = null;
        try {
            fileProcessor = new FileProcessor("PATH_TEST");
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
        fileProcessor.run();

        Assert.assertEquals(2, fileProcessor.allBooks.size());
        Assert.assertEquals(3, fileProcessor.allBooks.get("book-2").inboundQueue.size());
        Assert.assertEquals(2, fileProcessor.allBooks.get("book-2").inboundQueue.peek().getOrderId());
    }

    @Test
    public void testWithoutFile() {
        FileProcessor fileProcessor = null;
        try {
            fileProcessor = new FileProcessor("PATH_TEST_INVALID");
        } catch (IOException e) {
            return; // passed
        }
        fail();
    }

}
