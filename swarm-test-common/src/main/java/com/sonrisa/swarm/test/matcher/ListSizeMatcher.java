/*
 *   Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.
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
package com.sonrisa.swarm.test.matcher;

import java.util.List;

import org.mockito.ArgumentMatcher;

/**
 * Customer Mockite matcher matching the size of a list
 * @author sonrisa
 *
 */
public class ListSizeMatcher<T> extends ArgumentMatcher<List<T>> {
    
    private int matchSize;
    
    public ListSizeMatcher(int size){ 
        this.matchSize = size;
    }

    @Override
    public boolean matches(Object argument) {
        return ((List<T>)argument).size() == matchSize;
    }
}
