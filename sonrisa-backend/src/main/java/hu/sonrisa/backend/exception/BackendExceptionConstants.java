/*
 *   Copyright (c) 2010 Sonrisa Informatikai Kft. All Rights Reserved.
 * 
 *  This software is the confidential and proprietary information of
 *  Sonrisa Informatikai Kft. ("Confidential Information").
 *  You shall not disclose such Confidential Information and shall use it only in
 *  accordance with the terms of the license agreement you entered into
 *  with Sonrisa.
 * 
 *  SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 *  THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *  TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 *  PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 *  ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 *  DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package hu.sonrisa.backend.exception;

/**
 * A Sonrisa Backend reusable components project-ben található 
 * exception üzeneteknek megfelelő üzenetkód konstansokat tartalmazó osztály
 * 
 * @author Borosan
 */
public abstract class BackendExceptionConstants extends ExceptionConstants {
    public static final String BEND_00001 = "A folyamat nem lehet null.";
    public static final String BEND_00002 = "Nincs ennyi paraméter az üzenetben ";
    public static final String BEND_00003 = "Üres vagy hibás fájl!";
    public static final String BEND_00004 = "Not supported yet";
    public static final String BEND_00005 = " XML payload-ja null";
    
    public static final String BEND_00006 = " not found in style map";
    public static final String BEND_00007 = "endTable must call before start new one.";
    public static final String BEND_00008 = "BaseFont must be defined.";
    public static final String BEND_00009 = "Error while close table";
    public static final String BEND_00010 = "Error while add paragraph";
    
    public static final String BEND_00011 = "Error while init writer";
    public static final String BEND_00012 = "Error while create workbook ";
    public static final String BEND_00013 = "Error while close workbook ";
    public static final String BEND_00014 = "exportResources.properties not found for ";
    public static final String BEND_00015 = "Style not found ";
    
    public static final String BEND_00016 = "Writer bezárása közben hiba történt!";
    public static final String BEND_00017 = "Ismeretlen hash algotitmus: ";
    public static final String BEND_00018 = "FATAL: UTF-8 encoding not supported.";
    public static final String BEND_00019 = "UniCode path extra data must have at least 5 bytes.";
    public static final String BEND_00020 = "Unsupported version for UniCode path extra data: ";
    
    public static final String BEND_00021 = "Given CRC checksum doesn't match real checksum: ";
    public static final String BEND_00022 = " doesn't implement ZipExtraField";
    public static final String BEND_00023 = " is not a concrete class";
    public static final String BEND_00024 = "\'s no-arg constructor is not public";
    public static final String BEND_00025 = "bad extra field starting at ";
    
    public static final String BEND_00026 = "unknown UnparseableExtraField key: ";
    public static final String BEND_00027 = "JarMarker doesn't expect any data";
    public static final String BEND_00028 = "Error parsing extra fields for entry: ";
    public static final String BEND_00029 = "Found unsupported compression method ";
    public static final String BEND_00030 = "central directory is empty, can't expand corrupt archive.";
    
    public static final String BEND_00031 = "archive is not a ZIP archive";
    public static final String BEND_00032 = "Failed to decode name: ";
    public static final String BEND_00033 = "failed to skip file name in local file header";
    public static final String BEND_00034 = "Invalid compression level: ";
    public static final String BEND_00035 = "bad CRC checksum for entry ";
    
    public static final String BEND_00036 = "bad size for entry ";
    public static final String BEND_00037 = "uncompressed size is required for STORED method when not writing to a file";
    public static final String BEND_00038 = "crc checksum is required for STORED method when not writing to a file";
    public static final String BEND_00039 = "Failed to encode name: ";
    public static final String BEND_00040 = "Block length exceeds remaining data: ";
    
    public static final String BEND_00041 = " instead of ";
    public static final String BEND_00042 = "An error occurred during the cloning!";
    
   
    
}
