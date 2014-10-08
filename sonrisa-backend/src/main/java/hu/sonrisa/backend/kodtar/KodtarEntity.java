/*
 *   Copyright (c) 2013  Sonrisa Informatikai Kft. All Rights Reserved.
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
package hu.sonrisa.backend.kodtar;

import hu.sonrisa.backend.versionedobject.VersionedObjectEntity;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author joe
 */
@Entity
@DiscriminatorValue(value = "KODTAR")
public class KodtarEntity extends VersionedObjectEntity<Kodtar> implements Comparable<KodtarEntity>{

 private static final long serialVersionUID = 1L;

    // ------------------------------------------------------------------------
    // ~ Constructors
    // ------------------------------------------------------------------------
    /**
     *
     */
    public KodtarEntity() {
        super(Kodtar.class);
    }

    @Id
    @Override
    public String getId() {
        return super.getId();
    }

    @Override
    public void setId(String id) {
        super.setId(id);
    }

    // ------------------------------------------------------------------------
    // ~ Lifecycle methods
    // ------------------------------------------------------------------------
    @Override
    public void onWrap(Kodtar obj) {
        super.onWrap(obj);
        String m = obj.getMegnevezes().length() > 1999 ? obj.getMegnevezes().substring(0, 1999) : obj.getMegnevezes();        
        setMegnevezes(m);
        obj.setDirty(false);
    }

    @Override
    public void onUnWrap(Kodtar obj) {
        super.onUnWrap(obj);
        obj.setMegnevezes(getMegnevezes());
        obj.setDirty(false);
    }

    @Override
    public String toString() {
        return "kodtar[Kod:" + getKod() + ",Megnevezes:" + getMegnevezes();
    }

    @Override
    public int compareTo(KodtarEntity other) {
        int ret = getKod().toLowerCase().compareTo(other.getKod().toLowerCase());
        if (ret != 0) {
            return ret;
        } else {
            return -getCreatedAt().compareTo(other.getCreatedAt());
        }
    }

    
}
