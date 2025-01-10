package lerfob.carbonbalancetool.productionlines.affiliere;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import lerfob.carbonbalancetool.productionlines.affiliere.AffiliereJSONImportReader.SankeyProxy;
import py4j.ClientServer;
import py4j.GatewayServer;
import repicea.util.ObjectUtility;


public class SankeyPy4JTests {

	
	
	
	
	/*
	 * This test still needs to start the Python server on its own.
	 */
	@Ignore
	@Test
	public void simpleReadWriteTest() throws IOException {
		GatewayServer.turnLoggingOff();
		ClientServer server = new ClientServer(null);
		SankeyProxy sankey = (SankeyProxy) server.getPythonServerEntryPoint(new Class[] { SankeyProxy.class });
		String inputFilename = AffiliereJSONImportReader.getProperFilenameForPython(ObjectUtility.getPackagePath(SankeyPy4JTests.class) + "GE_bois_reconciled vu JLM.xlsx");
		String outputFilename = AffiliereJSONImportReader.getProperFilenameForPython(ObjectUtility.getPackagePath(SankeyPy4JTests.class) + "example_Copy.xlsx");
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
	}
	 

}
