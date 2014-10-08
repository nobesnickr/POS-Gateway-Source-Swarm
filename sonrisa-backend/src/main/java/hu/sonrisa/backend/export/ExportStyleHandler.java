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
package hu.sonrisa.backend.export;

/**
 * TODO: refaktorálni ezt valami pluggable cuccba
 * 
 * @author Golyo
 */
public final class ExportStyleHandler {
    public static final String COURIER_FONTNAME = "Courier";
    public static final String COURIER8_STYLE = "Courier8";
    public static final String STYLE_DELIM = "@";
    public static final String HEADER_STYLE = "header";
    public static final String LEFT_STYLE = "left";
    public static final String CENTER_STYLE = "center";
    public static final String RIGHT_STYLE = "right";
    public static final String S_HEADER_STYLE = "sheader";
    public static final String L_HEADER_STYLE = "lheader";
    public static final String S_LEFT_STYLE = "sleft";
    public static final String S_CENTER_STYLE = "scenter";
    public static final String S_RIGHT_STYLE = "sright";
    public static final String S_INT_NUMBER_STYLE = "sintnumber";
    public static final String S_DEC_NUMBER_STYLE = "sdecnumber";
    public static final String S_DATE_STYLE = "sdate";
    public static final String DATE_STYLE = "date";
    public static final String DECIMAL_FORMAT = "#,##0.0###";
    public static final String INTEGER_FORMAT = "#,##0";
    public static final String DATE_FORMAT = "yyyy/MM/dd";
    public static final String TITLE_STYLE = "title";
    public static final String CENTER_BORDERED_STYLE = "centerBordered";

    private ExportStyleHandler() {
    }
}