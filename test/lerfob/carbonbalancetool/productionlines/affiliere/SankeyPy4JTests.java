package lerfob.carbonbalancetool.productionlines.affiliere;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import py4j.ClientServer;
import py4j.GatewayServer;
import repicea.util.ObjectUtility;


public class SankeyPy4JTests {

	
	/**
	 * An interface to provide access to methods on Python's end.
	 */
	public interface SankeyProxy {
		
		/**
		 * Read the content of an excel file and convert it
		 * into Sankey objects on Python's end.<p>
		 * This method returns tuples in Python. The tuple is converted into string.
		 * @param filename the path to the Excel file.
		 * @return a String 
		 */
		public String readFromExcel(String filename);

		/**
		 * Write Sankey objects to an Excel file.
		 * @param filename the output filename
		 * @param mode either "a" (append) or "w" (write)
		 * @return null
		 */
		public String writeToExcel(String filename, String mode);
		
		public Object getNodes();
		
		public String requestShutdown();

	}
	
	
	private static String getProperFilenameForPython(String originalFilename) {
		return originalFilename.startsWith("/") ? originalFilename.substring(1) : originalFilename;
	}
	
	/*
	 * This test still needs to start the Python server on its own.
	 */
	@Test
	public void simpleReadWriteTest() throws IOException {
		GatewayServer.turnLoggingOff();
		ClientServer server = new ClientServer(null);
		SankeyProxy sankey = (SankeyProxy) server.getPythonServerEntryPoint(new Class[] { SankeyProxy.class });
		String inputFilename = getProperFilenameForPython(ObjectUtility.getPackagePath(SankeyPy4JTests.class) + "example_alexandre.xlsx");
		String outputFilename = getProperFilenameForPython(ObjectUtility.getPackagePath(SankeyPy4JTests.class) + "example_alexandreCopy.xlsx");
		File outputFile = new File(outputFilename);
		if (outputFile.exists()) {
			boolean hasBeenDeleted = outputFile.delete();
			if (!hasBeenDeleted) {
				throw new IOException("The output file cannot be deleted!");
			}
		}
		try {
			String message = sankey.readFromExcel(inputFilename);
			System.out.println(message);
			Assert.assertTrue("Check if message contains true", message.contains("True"));
			
			Object o = sankey.writeToExcel(outputFilename, "w");
			Assert.assertTrue("Check if return object is null", o == null);
			Assert.assertTrue("Check if copy now exists", outputFile.exists());
			double fileSizeKb = outputFile.length() / 1024d;
			Assert.assertEquals("Checking file size", 9.2675, fileSizeKb, 0.5); // apparently the size is not constant
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
		sankey.requestShutdown(); // TODO FP fix this it does not shut the Python server down. MF 20240917
	}

	
	
	public static void main(String[] args) {
		GatewayServer.turnLoggingOff();
		GatewayServer server = new GatewayServer();
		server.start();
		SankeyProxy hello = (SankeyProxy) server.getPythonServerEntryPoint(new Class[] { SankeyProxy.class });
		String path = ObjectUtility.getPackagePath(SankeyPy4JTests.class) + "example_alexandre.xlsx";
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		try {
			String message = hello.readFromExcel(path);
			System.out.println(message);
			Object nodes = hello.getNodes();
			System.out.println(nodes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		server.shutdown();
	}
	 

}
