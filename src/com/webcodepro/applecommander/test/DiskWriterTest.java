/*
 * AppleCommander - An Apple ][ image utility.
 * Copyright (C) 2002 by Robert Greene
 * robgreene at users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the 
 * Free Software Foundation; either version 2 of the License, or (at your 
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along 
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package com.webcodepro.applecommander.test;

import com.webcodepro.applecommander.storage.DiskFullException;
import com.webcodepro.applecommander.storage.DosFormatDisk;
import com.webcodepro.applecommander.storage.FileEntry;
import com.webcodepro.applecommander.storage.FormattedDisk;
import com.webcodepro.applecommander.storage.OzDosFormatDisk;
import com.webcodepro.applecommander.storage.ProdosFormatDisk;
import com.webcodepro.applecommander.storage.UniDosFormatDisk;
import com.webcodepro.applecommander.storage.FormattedDisk.DiskUsage;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

/**
 * Test Disk and FormattedDisk for write.
 * <p>
 * Date created: Oct 3, 2002 11:35:26 PM
 * @author: Rob Greene
 */
public class DiskWriterTest extends TestCase {
	/**
	 * Determine if the created disk images should be saved for later
	 * perusal.
	 */
	private static final boolean saveImage = false;

	public DiskWriterTest(String name) {
		super(name);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(DiskWriterTest.class);
	}

	public void testWriteToDos33() throws DiskFullException, IOException {
		FormattedDisk[] disks = DosFormatDisk.create("write-test-dos33.dsk");
		writeFiles(disks, "B", "T", false);
		saveDisks(disks);
	}
	
	public void testWriteToProdos140kDisk() throws DiskFullException, IOException {
		FormattedDisk[] disks = ProdosFormatDisk.create(
			"write-test-prodos-140k.dsk", "TEST", ProdosFormatDisk.APPLE_140KB_DISK);
		writeFiles(disks, "BIN", "TXT", true);
		saveDisks(disks);
	}

	public void testWriteToProdos800kDisk() throws DiskFullException, IOException {
		FormattedDisk[] disks = ProdosFormatDisk.create(
			"write-test-prodos-800k.po", "TEST", ProdosFormatDisk.APPLE_800KB_DISK);
		writeFiles(disks, "BIN", "TXT", true);
		saveDisks(disks);
	}

	public void testWriteToProdos5mbDisk() throws DiskFullException, IOException {
		FormattedDisk[] disks = ProdosFormatDisk.create(
			"write-test-prodos-5mb.hdv", "TEST", ProdosFormatDisk.APPLE_5MB_HARDDISK);
		writeFiles(disks, "BIN", "TXT", true);
		saveDisks(disks);
	}
	
	public void testCreateAndDeleteDos33() throws IOException {
		FormattedDisk[] disks = DosFormatDisk.create(
			"createanddelete-test-dos33.dsk");
		createAndDeleteFiles(disks, "B");
		saveDisks(disks);
	}

	public void testCreateAndDeleteOzDos() throws IOException {
		FormattedDisk[] disks = OzDosFormatDisk.create(
			"createanddelete-test-ozdos.po");
		createAndDeleteFiles(disks, "B");
		saveDisks(disks);
	}

	public void testCreateAndDeleteUniDos() throws IOException {
		FormattedDisk[] disks = UniDosFormatDisk.create(
			"createanddelete-test-unidos.dsk");
		createAndDeleteFiles(disks, "B");
		saveDisks(disks);
	}

	public void testCreateAndDeleteProdos140kDisk() throws IOException {
		FormattedDisk[] disks = ProdosFormatDisk.create(
			"createanddelete-test-prodos-140k.dsk", "TEST", 
			ProdosFormatDisk.APPLE_140KB_DISK);
		createAndDeleteFiles(disks, "BIN");
		saveDisks(disks);
	}

	public void testCreateAndDeleteProdos800kDisk() throws IOException {
		FormattedDisk[] disks = ProdosFormatDisk.create(
			"createanddelete-test-prodos-800k.dsk", "TEST",
			ProdosFormatDisk.APPLE_800KB_2IMG_DISK);
		createAndDeleteFiles(disks, "BIN");
		saveDisks(disks);
	}
	
	protected void writeFiles(FormattedDisk[] disks, String binaryType, 
		String textType, boolean testText) throws DiskFullException {
		FormattedDisk disk = disks[0];
		showDirectory(disks, "BEFORE FILE CREATION");
		writeFile(disk, 1, binaryType, true);
		writeFile(disk, 2, binaryType, true);
		writeFile(disk, 4, binaryType, true);
		writeFile(disk, 8, binaryType, true);
		writeFile(disk, 16, binaryType, true);
		writeFile(disk, 256, binaryType, true);
		writeFile(disk, 512, binaryType, true);
		writeFile(disk, 1234, binaryType, true);
		writeFile(disk, 54321, binaryType, true);
		writeFile(disk, 
			"This is a test text file create from the DiskWriterTest".getBytes(), 
			textType, testText);
		if (disk.getPhysicalSize() > disk.APPLE_140KB_DISK) {
			// create a few big files
			writeFile(disk, 150000, binaryType, true);
			writeFile(disk, 300000, binaryType, true);
		}
		showDirectory(disks, "AFTER FILE CREATION");
	}
	
	protected void writeFile(FormattedDisk disk, int size, String fileType,
		boolean test) throws DiskFullException {
		byte[] data = new byte[size];
		for (int i=0; i<data.length; i++) {
			data[i] = (byte)(Math.random() * 1024);
		}
		writeFile(disk, data, fileType, test);
	}
	
	protected void writeFile(FormattedDisk disk, byte[] data, String fileType,
		boolean test) throws DiskFullException {
		FileEntry entry = disk.createFile();
		entry.setFilename("file-" + data.length);
		entry.setFiletype(fileType);
		entry.setFileData(data);
		byte[] data2 = entry.getFileData();
		if (test) {
			assertTrue("File lengths do not match", data.length == data2.length);
			//assertTrue("File contents do not match", Arrays.equals(data, data2));
			for (int i=0; i<data.length; i++) {
				assertTrue("File contents differ at " + i, data[i] == data2[i]);
			}
		}
	}
	
	protected void showDirectory(FormattedDisk[] formattedDisks, String title) {
		System.out.println();
		System.out.println("************************************************");
		System.out.println(title);
		for (int i=0; i<formattedDisks.length; i++) {
			FormattedDisk formattedDisk = formattedDisks[i];
			System.out.println();
			System.out.println(formattedDisk.getDiskName());
			List files = formattedDisk.getFiles();
			if (files != null) {
				showFiles(files, "");
			}
			System.out.println(formattedDisk.getFreeSpace() + " bytes free.");
			System.out.println(formattedDisk.getUsedSpace() + " bytes used.");
			System.out.println("This disk " + (formattedDisk.canHaveDirectories() ? "does" : "does not") +
				" support directories.");
			System.out.println("This disk is formatted in the " + formattedDisk.getFormat() + " format.");
			System.out.println();
			
			showDiskUsage(formattedDisk);
		}
		System.out.println();
		System.out.println("************************************************");
		System.out.println();
	}
	
	protected void showFiles(List files, String indent) {
		for (int i=0; i<files.size(); i++) {
			FileEntry entry = (FileEntry) files.get(i);
			if (!entry.isDeleted()) {
				List data = entry.getFileColumnData(FormattedDisk.FILE_DISPLAY_NATIVE);
				System.out.print(indent);
				for (int d=0; d<data.size(); d++) {
					System.out.print(data.get(d));
					System.out.print(" ");
				}
				System.out.println();
			}
			if (entry.isDirectory()) {
				showFiles(entry.getFiles(), indent + "  ");
			}
		}
	}
	
	protected void showDiskUsage(FormattedDisk disk) {
		int[] dimensions = disk.getBitmapDimensions();
		DiskUsage usage = disk.getDiskUsage();
		if (usage == null) {
			System.out.println("A bitmap is not available.");
			return;
		}
		if (dimensions == null) {
			int i=0;
			while (usage.hasNext()) {
				if (i > 0 && i % 80 == 0) System.out.println();
				usage.next();
				System.out.print(usage.isFree() ? "." : "U");
				i++;
			}
			System.out.println();
		} else {
			for (int y=dimensions[0]-1; y>=0; y--) {
				for (int x=0; x<dimensions[1]; x++) {
					usage.next();
					System.out.print(usage.isFree() ? "." : "U");
				}
				System.out.println();
			}
		}
		System.out.println("U = used, . = free");
	}
	
	/**
	 * Create a bunch of files and then delete them repeatedly.
	 * This is intended to excersize not only creating and deleting
	 * files but the disk management (ala Disk Map).
	 */
	protected void createAndDeleteFiles(FormattedDisk[] disks, String filetype) {
		byte[] data = new byte[129 * 1024];
		for (int i=0; i<data.length; i++) {
			data[i] = (byte)(Math.random() * 1024);
		}
		for (int d=0; d<disks.length; d++) {
			FormattedDisk disk = disks[d];
			System.out.println("Excercising disk " + disk.getDiskName() + 
				" in the " + disk.getFormat() + " format.");
			int originalUsed = disk.getUsedSpace();
			int originalFree = disk.getFreeSpace();
			for (int count=0; count<5; count++) {
				// Fill the disk with files:
				try {
					while (true) {
						writeFile(disk, data, filetype, false);
					}
				} catch (DiskFullException ex) {
					// ignored
				}
				// Remove the files:
				List files = disk.getFiles();
				for (int i=0; i<files.size(); i++) {
					FileEntry entry = (FileEntry) files.get(i);
					entry.delete();
				}
				// Verify that we're back to what we started with:
				assertTrue("Free space does not match", 
					originalFree == disk.getFreeSpace());
				assertTrue("Used space does not match", 
					originalUsed == disk.getUsedSpace());
			}
		}
	}
	
	/**
	 * Save a disk, if the saveImage flag has been set to true.
	 */
	protected void saveDisks(FormattedDisk[] disks) throws IOException {
		if (saveImage) {
			for (int i=0; i<disks.length; i++) {
				disks[i].save();
			}
		}
	}
}
