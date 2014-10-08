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
package hu.sonrisa.backend;

import hu.sonrisa.backend.auth.Felhasznalo;
import hu.sonrisa.backend.auth.SessionCredentialsProvider;
import java.util.Locale;
import org.springframework.stereotype.Component;

/**
 *
 * @author János Cserép <cserepj@sonrisa.hu>
 */
@Component
public class MockSessionCredentials implements SessionCredentialsProvider {

    @Override
    public Felhasznalo getSessionFelhasznalo() {
        return new Felhasznalo("TEST", "TEST", "");
    }

    @Override
    public String getSessionSzervezet() {
        return "";
    }

    @Override
    public String getRemoteIp() {
        return "127.0.0.1";
    }
    
    @Override
    public Locale getLocale() {
        return null;
    }
}
