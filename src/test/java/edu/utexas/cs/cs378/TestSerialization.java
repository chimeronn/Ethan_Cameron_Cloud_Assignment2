package edu.utexas.cs.cs378;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestSerialization {
	public List<DataItem> myData;

	@Before
	public void setUp() throws Exception {
		
		myData = Utils.generateExampleData(10);
	}

	@Test
	public void testSerialization() {
		
		// iterate over the test data
		for (DataItem dataItem : myData) {
			// We serialize the object into byte array. 
			byte[] bytesOfObject = dataItem.handSerializationWithByteBuffer();
			
			// Now we de-serialize it back from the byte array. 
			DataItem tmp = new DataItem().deserializeFromBytes(bytesOfObject);
			
			
			assertEquals(dataItem.getLine(), tmp.getLine() );
			assertEquals(dataItem.getValueA(), tmp.getValueA(), 0.0001);
			assertEquals(dataItem.getValueB(), tmp.getValueB(), 0.0001);
			
		}
		
		
		
		
	}

}
