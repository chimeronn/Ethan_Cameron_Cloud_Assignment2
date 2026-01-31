package edu.utexas.cs.cs378;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Utils {

	/**
	 * Reads back a list of DataItems from a byte array.
	 * 
	 * @param dataRead
	 * @return
	 */
	public static List<DataItem> readFromAPage(byte[] dataRead) {

		ByteBuffer byteBuffer = ByteBuffer.wrap(dataRead);

		// Get the number of objects that we have to read.
		int numberOfObjects = byteBuffer.getInt(); // 4 bytes
		
		System.out.println("number of Objects is: " + numberOfObjects );

		// pre-allocate a list of objects.
		List<DataItem> myDataItems = new ArrayList<DataItem>(numberOfObjects);

		// 1. read first the object size as an integer
		// 2. read back the object bytes
		// 3. de-serialze an object
		// 4. add the object to the list and return. 

		for (int i = 0; i < numberOfObjects; i++) {

			int objectSize = byteBuffer.getInt(); // 4 bytes
			byte[] objectBytes = new byte[objectSize]; // get the object bytes
			// now get the bytes of the object. 
			byteBuffer.get(objectBytes, 0, objectSize);

			// De-serialize an object from the objectButes
			DataItem myDataItem = new DataItem().deserializeFromBytes(objectBytes);
			
			// Add it to the return list.
			myDataItems.add(myDataItem);

		}

		return myDataItems;

	}

	/**
	 * This method packages a list of DataItems into list of pages. It may happen
	 * that some portion of a page at the end will remain empty.
	 * 
	 * @param dataItems
	 * @return
	 */
	public static List<byte[]> packageToPages(List<DataItem> dataItems) {

		int sizeCounter = 4; // we need 4 bytes of an int to write the number of objects at the beginning of
							 // each page.

		// initializations 
		List<byte[]> objectsInBytesTemp = new ArrayList<byte[]>();

		List<byte[]> pageList = new ArrayList<byte[]>();
	
		ByteBuffer byteBuffer = ByteBuffer.allocate(Const.PAGESIZE);

		// counter for objects in each page.
		int objectCounter = 0;
		
		
		for (int i = 0; i < dataItems.size(); i++) {
			
			// get the objects bytes and de-serialize them 
			DataItem myDataItem = dataItems.get(i);

			byte[] bytesOfIt = myDataItem.handSerializationWithByteBuffer();

			objectsInBytesTemp.add(bytesOfIt);

			// increase the size
			sizeCounter = sizeCounter + 4 + bytesOfIt.length;
			
			objectCounter++;

			// if we have enough objects for a page, then dump it to a page and get a new
			// page.
			if (sizeCounter > Const.PAGESIZE) {
                //skip the last object because it cause a page overflow. 
				//1. add the count of objects on this page. 
				byteBuffer.putInt(objectCounter-1);
				
				// We add all object, and skip the last object because it cause a page overflow. 
				for (int j = 0; j < objectsInBytesTemp.size() - 1 ; j++) {
					
					byteBuffer.putInt(objectsInBytesTemp.get(j).length);
					byteBuffer.put(objectsInBytesTemp.get(j));
				}
	
				
				// a page is ready
				pageList.add(byteBuffer.array());
				
				// reset for a new page and adding the last object before full. 
				byteBuffer = ByteBuffer.allocate(Const.PAGESIZE);
				byte[] lastObjectBeforeFull = objectsInBytesTemp.get(objectsInBytesTemp.size() -1 );
				
				// get a fresh list 
				objectsInBytesTemp = new ArrayList<byte[]>();
				// add the last object to the new page. 
				objectsInBytesTemp.add(lastObjectBeforeFull);
				
				sizeCounter = 4; // 4 for the object numbers, and 4 for the length of the last object. 
				sizeCounter = sizeCounter + 4 + lastObjectBeforeFull.length; 
				objectCounter = 1;
				

			}// end of IF statement. 

		}
		
		
		// one page at the end, for the case that we have data less than a page. 
		
		byteBuffer.putInt(objectCounter);
		for (int j = 0; j < objectsInBytesTemp.size(); j++) {
			
			byteBuffer.putInt(objectsInBytesTemp.get(j).length);
			byteBuffer.put(objectsInBytesTemp.get(j));
		}
		pageList.add(byteBuffer.array());
		System.out.println("Page number " + pageList.size() );

		System.out.println("Number of data pages to send to server is: " + pageList.size());

		return pageList;

	}

	
	/*
	 * Generates some example Data.
	 */
	public static List<DataItem> generateExampleData(int number) {
		List<DataItem> items = new ArrayList<DataItem>(number);

		for (int i = 0; i < number; i++) {
			DataItem myObject = new DataItem("ID-NUMBER-" + i, i + 1, i + 2);
			items.add(myObject);
		}
		return items;

	}

}
